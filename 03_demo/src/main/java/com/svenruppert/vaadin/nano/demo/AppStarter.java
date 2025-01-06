package com.svenruppert.vaadin.nano.demo;

import org.apache.commons.cli.ParseException;
import com.svenruppert.vaadin.nano.CoreUIServiceJava;

public class AppStarter {

  private AppStarter() {
  }

  public static void main(String[] args) throws ParseException {
    CoreUIServiceJava.main(args);
  }
}
