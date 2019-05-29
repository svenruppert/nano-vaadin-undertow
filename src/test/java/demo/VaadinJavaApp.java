package demo;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.rapidpm.dependencies.core.logger.HasLogger;

@Route("")
public class VaadinJavaApp
    extends Composite<Div>
    implements HasLogger {

  public static final String BTN_CLICK_ME   = "btn-click-me";
  public static final String LB_CLICK_COUNT = "lb-click-count";

  private final Button         btnClickMe   = new Button("click me");
  private final Span           lbClickCount = new Span("0");
  private final VerticalLayout layout       = new VerticalLayout(btnClickMe, lbClickCount);

  private int clickcount = 0;

  public VaadinJavaApp() {
    btnClickMe.setId(BTN_CLICK_ME);
    btnClickMe.addClickListener(event -> lbClickCount.setText(String.valueOf(++clickcount)));

    lbClickCount.setId(LB_CLICK_COUNT);

    logger().info("and now..  setting the main content.. ");
    getContent().add(layout);
  }
}