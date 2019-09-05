/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.vaadin.nano

import com.vaadin.flow.router.Route
import com.vaadin.flow.server.startup.RouteRegistryInitializer
import com.vaadin.flow.server.startup.ServletDeployer
import io.undertow.Handlers.path
import io.undertow.Handlers.redirect
import io.undertow.Undertow
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.ServletContainerInitializerInfo
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.rapidpm.dependencies.core.logger.HasLogger
import org.rapidpm.frp.model.Result.failure
import org.rapidpm.frp.model.Result.success
import org.reflections8.Reflections
import java.lang.Integer.valueOf
import java.lang.System.getProperty
import java.lang.System.setProperty
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.servlet.ServletException

/**
 *
 */
class CoreUIKotlinService : HasLogger {

  val CORE_UI_SERVER_HOST_DEFAULT = "0.0.0.0"
  val CORE_UI_SERVER_PORT_DEFAULT = "8899"

  val CORE_UI_SERVER_HOST = "core-ui-server-host"
  val CORE_UI_SERVER_PORT = "core-ui-server-port"
  val CORE_UI_BASE_PKG = "core-ui-base-package"

  val CLI_HOST = "host"
  val CLI_PORT = "port"
  val CLI_BASE_PKG = "pkg"

  val DEFAULT_BASE_PKG = "org.rapidpm"


  var undertow = failure<Undertow>("not initialised so far")

  @Throws(ParseException::class)
  fun startup(args: Array<String>) {
    val options = Options()
    options.addOption(CLI_HOST, true, "host to use")
    options.addOption(CLI_PORT, true, "port to use")
    options.addOption(CLI_BASE_PKG, true, "base package to use to scan for Route annotated classes")

    val parser = DefaultParser()
    val cmd = parser.parse(options, args)

    if (cmd.hasOption(CLI_HOST)) {
      setProperty(CORE_UI_SERVER_HOST, cmd.getOptionValue(CLI_HOST))
    }
    if (cmd.hasOption(CLI_PORT)) {
      setProperty(CORE_UI_SERVER_PORT, cmd.getOptionValue(CLI_PORT))
    }
    if (cmd.hasOption(CLI_BASE_PKG)) {
      setProperty(CORE_UI_BASE_PKG, cmd.getOptionValue(CLI_BASE_PKG))
    }
    startup()
  }


  fun startup() {
    val classLoader = CoreUIJavaService::class.java.classLoader
    val servletBuilder = Servlets.deployment()
        .setClassLoader(classLoader)
        .setContextPath("/")
        .setDeploymentName("ROOT.war")
        .setDefaultEncoding("UTF-8")
        .setResourceManager(
            ClassPathResourceManager(classLoader, "META-INF/resources/"))
        .addServletContainerInitializer(
            ServletContainerInitializerInfo(RouteRegistryInitializer::class.java,
                setOfRouteAnnotatedClasses()))
        .addListener(Servlets.listener(ServletDeployer::class.java))

    val manager = Servlets.defaultContainer()
        .addDeployment(servletBuilder)
    manager.deploy()

    try {
      val path = path(redirect("/")).addPrefixPath("/", manager.start())
      val u = Undertow.builder()
          .addHttpListener(valueOf(getProperty(CORE_UI_SERVER_PORT, CORE_UI_SERVER_PORT_DEFAULT)),
              getProperty(CORE_UI_SERVER_HOST, CORE_UI_SERVER_HOST_DEFAULT))
          .setHandler(path)
          .build()
      u.start()

      u.listenerInfo
          .forEach { e -> logger().info(e.toString()) }

      undertow = success(u)
    } catch (e: ServletException) {
      e.printStackTrace()
      undertow = failure(e.message)
    }

  }

  private fun setOfRouteAnnotatedClasses(): Set<Class<*>> {
    return Reflections(getProperty(CORE_UI_BASE_PKG, DEFAULT_BASE_PKG)).getTypesAnnotatedWith(Route::class.java)
        .stream()
        .peek { cls ->
          logger().info(
              "found Route annotate class " + cls.getName())
        }
        .collect(Collectors.toSet<Class<*>>())
  }

  fun shutdown() {
    undertow.ifPresent({ it.stop() })
  }
}
