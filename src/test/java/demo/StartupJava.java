package demo;

import org.apache.commons.cli.ParseException;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.functions.CheckedFunction;
import org.rapidpm.frp.model.Result;
import org.rapidpm.vaadin.nano.CoreUIJavaService;

import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.vaadin.nano.CoreUIJavaService.CORE_UI_BASE_PKG;

public class StartupJava
    implements HasLogger {


  private StartupJava() {
  }

  private Result<CoreUIJavaService> service = failure("not initialized until now");

  private void run(String[] args) throws ParseException {
    final String value = StartupJava.class.getPackage()
                                          .toString();
    logger().info("CORE_UI_BASE_PKG -> " + value);
    System.setProperty(CORE_UI_BASE_PKG, value);
    service = ((CheckedFunction<CoreUIJavaService, CoreUIJavaService>) s -> {
      s.startup(args);
      return s;
    }).apply(new CoreUIJavaService());
  }


  public static void main(String[] args) throws ParseException {
    new StartupJava().run(args);
  }

  public void shutdown() {
//    logger().info();
  }
}
