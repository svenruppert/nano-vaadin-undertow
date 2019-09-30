package demo;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.rapidpm.dependencies.core.logger.HasLogger;


@Route("")
public class MainView
    extends Composite<VerticalLayout>
    implements HasLogger {

  private final Button    btn   = new Button();
  private final TextField input = new TextField();

  private final ComboBox<String> comboBox = new ComboBox<>();

  public MainView() {
    final VerticalLayout content = getContent();
    input.setPlaceholder("your name, please");
    btn.setText("Click Me");
    btn.addClickListener(event -> {
      if (input.isEmpty()) {
        logger().info("clicked without input value");
      } else content.add(new Span(input.getValue()));
    });
    content.add(new Span("Hello World"), btn, input, comboBox);
  }
}
