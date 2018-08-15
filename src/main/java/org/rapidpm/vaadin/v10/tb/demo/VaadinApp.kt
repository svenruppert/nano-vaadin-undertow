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
package org.rapidpm.vaadin.v10.tb.demo


import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.rapidpm.dependencies.core.logger.HasLogger

import java.lang.String.valueOf
import org.rapidpm.vaadin.addons.framework.ComponentIDGenerator.buttonID
import org.rapidpm.vaadin.addons.framework.ComponentIDGenerator.spanID

@Route("")
class VaadinApp : Composite<Div>(), HasLogger {

  private val btnClickMe = Button("click me")
  private val lbClickCount = Span("0")
  private val layout = VerticalLayout(btnClickMe, lbClickCount)

  private var clickcount = 0

  init {
    btnClickMe.setId(BTN_CLICK_ME)
    btnClickMe.addClickListener { event -> lbClickCount.text = (++clickcount).toString() }

    lbClickCount.setId(LB_CLICK_COUNT)

    //set the main Component
    logger().info("setting now the main ui content..")
    content.add(layout)
  }

  companion object {

    // read http://vaadin.com/testing for more infos
    val BTN_CLICK_ME = buttonID().apply(VaadinApp::class.java, "btn-click-me")
    val LB_CLICK_COUNT = spanID().apply(VaadinApp::class.java, "lb-click-count")
  }
}
