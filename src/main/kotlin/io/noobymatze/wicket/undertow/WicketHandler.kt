package io.noobymatze.wicket.undertow

import io.noobymatze.wicket.Main
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.DeploymentInfo
import io.undertow.servlet.api.InstanceFactory
import io.undertow.servlet.api.InstanceHandle
import org.apache.wicket.protocol.http.IWebApplicationFactory
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.protocol.http.WicketFilter
import org.apache.wicket.protocol.http.WicketServlet

class WicketHandler(
    private val path: String = "/",
    private val create: () -> WebApplication,
): HttpHandler {

    private val app: DeploymentInfo

    private val manager: HttpHandler

    init {
        this.app = Servlets.deployment()
            .setClassLoader(Main::class.java.classLoader)
            .setContextPath(path)
            .setDeploymentName("demo")
            .addServlet(
                Servlets.servlet("demo", WicketServlet::class.java,
                    WicketInstanceHandle(create)).addInitParam(WicketFilter.FILTER_MAPPING_PARAM, "/*").addMapping("/*"))

        this.manager = Servlets.defaultContainer()
            .addDeployment(app).apply {
                deploy()
            }.start()
    }

    class WicketInstanceHandle(
        val create: () -> WebApplication
    ) : InstanceFactory<WicketServlet> {

        override fun createInstance(): InstanceHandle<WicketServlet> = object : InstanceHandle<WicketServlet> {
            override fun release() = Unit

            override fun getInstance(): WicketServlet =
                Serv(create)
        }


        private class Serv(private val create: () -> WebApplication) : WicketServlet() {

            override fun newWicketFilter(): WicketFilter = object : WicketFilter() {
                override fun getApplicationFactory(): IWebApplicationFactory = object : IWebApplicationFactory {
                    override fun destroy(p0: WicketFilter?) = Unit
                    override fun createApplication(p0: WicketFilter?): WebApplication =
                        create()
                }
            }

        }

    }
    override fun handleRequest(exchange: HttpServerExchange) {
        manager.handleRequest(exchange)
    }

}

