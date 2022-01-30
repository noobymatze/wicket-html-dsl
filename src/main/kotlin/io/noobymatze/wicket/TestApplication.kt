package io.noobymatze.wicket

import io.noobymatze.wicket.html.ExamplePage
import org.apache.wicket.Page
import org.apache.wicket.protocol.http.WebApplication

class TestApplication: WebApplication() {
    override fun getHomePage(): Class<out Page> =
        ExamplePage::class.java
}