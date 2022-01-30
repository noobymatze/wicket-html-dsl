package io.noobymatze.wicket

import io.noobymatze.wicket.undertow.WicketHandler
import io.undertow.Handlers
import io.undertow.Undertow

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val handler = WicketHandler { TestApplication() }

        val server = Undertow.builder()
            .addHttpListener(
                8080, "localhost", Handlers.path()
                    .addPrefixPath("/", handler)
            )
            .build()

        server.start()
    }
}