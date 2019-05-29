package demo

import org.rapidpm.vaadin.nano.CoreUIKotlinService
import java.util.*

fun main() {
  object : CoreUIKotlinService() {
    override fun setOfRouteAnnotatedClasses(): Set<Class<*>> {
      return Collections.singleton(VaadinKotlinApp::class.java)
    }
  }.startup()
}