/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.org.rapidpm.vaadin.v10.tb.demo

import com.vaadin.flow.component.button.testbench.ButtonElement
import com.vaadin.flow.component.html.testbench.SpanElement
import org.openqa.selenium.WebDriver
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ContainerInfo
import org.rapidpm.vaadin.addons.testbench.junit5.pageobject.AbstractVaadinPageObject
import org.rapidpm.vaadin.v10.tb.demo.VaadinApp.Companion.BTN_CLICK_ME
import org.rapidpm.vaadin.v10.tb.demo.VaadinApp.Companion.LB_CLICK_COUNT
import java.lang.Integer.valueOf

class VaadinAppPageObject(webdriver: WebDriver, containerInfo: ContainerInfo) : AbstractVaadinPageObject(webdriver, containerInfo) {

  fun btnClickMe(): ButtonElement {
    return btn().id(BTN_CLICK_ME)
  }

  fun lbClickCount(): SpanElement {
    return span().id(LB_CLICK_COUNT)
  }

  fun click() {
    btnClickMe().click()
  }

  fun clickCountAsString(): String {
    return lbClickCount().text
  }

  // no exception handling
  fun clickCount(): Int {
    return valueOf(clickCountAsString())
  }

}
