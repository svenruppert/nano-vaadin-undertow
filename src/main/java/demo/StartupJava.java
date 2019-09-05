package demo;

import org.apache.commons.cli.ParseException;
import org.rapidpm.vaadin.nano.CoreUIJavaService;

public class StartupJava {


  private StartupJava() {
  }

  public static void main(String[] args) throws ParseException {
    new CoreUIJavaService().startup(args);
  }

  public static void shutdown() {

  }
}
