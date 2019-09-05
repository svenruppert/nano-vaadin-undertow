package org.rapidpm.vaadin.nano;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.startup.RouteRegistryInitializer;
import com.vaadin.flow.server.startup.ServletDeployer;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.Result;
import org.reflections8.Reflections;

import javax.servlet.ServletException;
import java.util.Set;
import java.util.stream.Collectors;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.redirect;
import static java.lang.Integer.valueOf;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.util.stream.Collectors.toSet;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;

public class CoreUIJavaService
    implements HasLogger {

  public static final String CORE_UI_SERVER_HOST_DEFAULT = "0.0.0.0";
  public static final String CORE_UI_SERVER_PORT_DEFAULT = "8899";

  public static final String CORE_UI_SERVER_HOST = "core-ui-server-host";
  public static final String CORE_UI_SERVER_PORT = "core-ui-server-port";
  public static final String CORE_UI_BASE_PKG    = "core-ui-base-package";

  public static final String CLI_HOST     = "host";
  public static final String CLI_PORT     = "port";
  public static final String CLI_BASE_PKG = "pkg";

  public static final String DEFAULT_BASE_PKG = "org.rapidpm";

  public Result<Undertow> undertow = failure("not initialised so far");

  public void startup(String[] args) throws ParseException {
    final Options options = new Options();
    options.addOption(CLI_HOST, true, "host to use");
    options.addOption(CLI_PORT, true, "port to use");
    options.addOption(CLI_BASE_PKG, true, "base package to use to scan for Route annotated classes");

    DefaultParser parser = new DefaultParser();
    CommandLine   cmd    = parser.parse(options, args);

    if (cmd.hasOption(CLI_HOST)) {
      setProperty(CORE_UI_SERVER_HOST, cmd.getOptionValue(CLI_HOST));
    }
    if (cmd.hasOption(CLI_PORT)) {
      setProperty(CORE_UI_SERVER_PORT, cmd.getOptionValue(CLI_PORT));
    }
    if (cmd.hasOption(CLI_BASE_PKG)) {
      setProperty(CORE_UI_BASE_PKG, cmd.getOptionValue(CLI_BASE_PKG));
    }
    startup();
  }


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
                           .addHttpListener(
                               Integer.parseInt(getProperty(CORE_UI_SERVER_PORT, CORE_UI_SERVER_PORT_DEFAULT)),
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

  private Set<Class<?>> setOfRouteAnnotatedClasses() {
    return new Reflections(getProperty(CORE_UI_BASE_PKG, DEFAULT_BASE_PKG)).getTypesAnnotatedWith(Route.class)
                                                                           .stream()
                                                                           .peek(cls -> logger().info(
                                                                            "found Route annotate class " + cls.getName()))
                                                                           .collect(toSet());
  }

  public void shutdown() {
    undertow.ifPresent(Undertow::stop);
  }
}