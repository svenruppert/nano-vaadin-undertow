package org.rapidpm.vaadin.nano;

import com.vaadin.flow.server.startup.RouteRegistryInitializer;
import com.vaadin.flow.server.startup.ServletDeployer;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.Result;

import javax.servlet.ServletException;

import java.util.Set;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.redirect;
import static java.lang.Integer.valueOf;
import static java.lang.System.getProperty;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;

public abstract class CoreUIJavaService
    implements HasLogger {

  public static final String CORE_UI_SERVER_HOST_DEFAULT = "0.0.0.0";
  public static final String CORE_UI_SERVER_PORT_DEFAULT = "8899";

  public static final String CORE_UI_SERVER_HOST = "core-ui-server-host";
  public static final String CORE_UI_SERVER_PORT = "core-ui-server-port";

  public Result<Undertow> undertow = failure("not initialised so far");

  public void startup() {
    final ClassLoader classLoader = CoreUIJavaService.class.getClassLoader();
    DeploymentInfo servletBuilder = Servlets.deployment()
                                            .setClassLoader(classLoader)
                                            .setContextPath("/")
                                            .setDeploymentName("ROOT.war")
                                            .setDefaultEncoding("UTF-8")
                                            .setResourceManager(
                                                new ClassPathResourceManager(classLoader, "META-INF/resources/"))
                                            .addServletContainerInitializer(
                                                new ServletContainerInitializerInfo(RouteRegistryInitializer.class,
                                                                                    setOfRouteAnnotatedClasses()))
                                            .addListener(Servlets.listener(ServletDeployer.class));

    final DeploymentManager manager = Servlets.defaultContainer()
                                              .addDeployment(servletBuilder);
    manager.deploy();

    try {
      PathHandler path = path(redirect("/")).addPrefixPath("/", manager.start());
      Undertow u = Undertow.builder()
                           .addHttpListener(valueOf(getProperty(CORE_UI_SERVER_PORT, CORE_UI_SERVER_PORT_DEFAULT)),
                                            getProperty(CORE_UI_SERVER_HOST, CORE_UI_SERVER_HOST_DEFAULT))
                           .setHandler(path)
                           .build();
      u.start();

      u.getListenerInfo()
       .forEach(e -> logger().info(e.toString()));

      undertow = success(u);
    } catch (ServletException e) {
      e.printStackTrace();
      undertow = failure(e.getMessage());
    }
  }

  public abstract Set<Class<?>> setOfRouteAnnotatedClasses();
}