package com.svenruppert.vaadin.nano.demo;

import com.svenruppert.dependencies.core.logger.HasLogger;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView
    extends HorizontalLayout
    implements HasLogger {


  public MainView() {
    Button btn = new Button();
    btn.setText("Button");
    add(btn);
  }
}
