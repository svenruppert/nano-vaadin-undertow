package demo;

import org.rapidpm.vaadin.nano.CoreUIJavaService;

import java.util.Collections;
import java.util.Set;

public class StartupJava {

    public static void main(String[] args) {
      new CoreUIJavaService() {
        @Override
        public Set<Class<?>> setOfRouteAnnotatedClasses() {
          return Collections.singleton(VaadinJavaApp.class);
        }
      }.startup();
    }
}
