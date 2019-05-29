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

import com.vaadin.flow.server.startup.RouteRegistryInitializer
import com.vaadin.flow.server.startup.ServletDeployer
import io.undertow.Handlers.path
import io.undertow.Handlers.redirect
import io.undertow.Undertow
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.ServletContainerInitializerInfo
import org.rapidpm.dependencies.core.logger.HasLogger
import org.rapidpm.frp.model.Result.failure
import org.rapidpm.frp.model.Result.success
import java.lang.Integer.valueOf
import java.lang.System.getProperty
import javax.servlet.ServletException

/**
 *
 */
abstract class CoreUIKotlinService : HasLogger {

  var undertow : org.rapidpm.frp.model.Result<Undertow> = failure("not initialised so far")

  fun startup() {
    val classLoader = CoreUIKotlinService::class.java.getClassLoader()
    val servletBuilder = Servlets.deployment()
        .setClassLoader(classLoader)
        .setContextPath("/")
        .setDeploymentName("ROOT.war")
        .setDefaultEncoding("UTF-8")
        .setResourceManager(ClassPathResourceManager(classLoader, "META-INF/resources/"))
        .addServletContainerInitializer(
            ServletContainerInitializerInfo(RouteRegistryInitializer::class.java,
                setOfRouteAnnotatedClasses())
        )
        .addListener(Servlets.listener(ServletDeployer::class.java))

    val manager = Servlets
        .defaultContainer()
        .addDeployment(servletBuilder)
    manager.deploy()

    try {
      val path = path(redirect("/"))
          .addPrefixPath("/", manager.start())
      val u = Undertow.builder()
          .addHttpListener(valueOf(getProperty(CORE_UI_SERVER_PORT,
              CORE_UI_SERVER_PORT_DEFAULT)),
              getProperty(CORE_UI_SERVER_HOST,
                  CORE_UI_SERVER_HOST_DEFAULT)
          )
          .setHandler(path)
          .build()
      u.start()

      u.getListenerInfo().forEach({ e -> logger().info(e.toString()) })

      undertow = success(u)
    } catch (e: ServletException) {
      e.printStackTrace()
      undertow = failure(e.message)
    }

  }

  abstract fun setOfRouteAnnotatedClasses(): Set<Class<*>>

  companion object {

    val CORE_UI_SERVER_HOST_DEFAULT = "0.0.0.0"
    val CORE_UI_SERVER_PORT_DEFAULT = "8899"

    val CORE_UI_SERVER_HOST = "core-ui-server-host"
    val CORE_UI_SERVER_PORT = "core-ui-server-port"
  }
}
