package io.noobymatze.wicket.html.dsl

import org.apache.wicket.Component
import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.IMarkupCacheKeyProvider
import org.apache.wicket.markup.IMarkupResourceStreamProvider
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.util.resource.IResourceStream
import org.apache.wicket.util.resource.StringResourceStream
import java.util.*

/**
 *
 */
abstract class HtmlWebPage(): WebPage(), IMarkupResourceStreamProvider, IMarkupCacheKeyProvider {

    /**
     * This uuid is used to
     *
     */
    private val uuid = UUID.randomUUID().toString()

    /**
     *
     */
    private var children: MutableList<Component> = mutableListOf()

    override fun onInitialize() {
        super.onInitialize()

        children.forEach { add(it) }
    }

    /**
     * @see [IMarkupResourceStreamProvider.getMarkupResourceStream]
     */
    override fun getMarkupResourceStream(
        container: MarkupContainer,
        containerClass: Class<*>
    ): IResourceStream {
        val pageBuilder = HtmlBuilder().apply { render() }
        val markupBuilder = StringBuilder()
        pageBuilder.children.forEach {
            this.children.addAll(renderHtml(it, markupBuilder))
        }

        return StringResourceStream(markupBuilder.toString())
    }

    /**
     * @see [IMarkupCacheKeyProvider.getCacheKey]
     */
    override fun getCacheKey(
        container: MarkupContainer,
        containerClass: Class<*>
    ): String? = uuid

    /**
     *
     */
    private fun renderHtml(
        html: Html,
        markupBuilder: StringBuilder
    ): List<Component> = when (html) {
        is Html.Node -> {
            markupBuilder.append("<${html.name}>")
            val result = mutableListOf<Component>()
            html.children.forEach {
                result.addAll(renderHtml(it, markupBuilder))
            }
            markupBuilder.append("</${html.name}>")
            result
        }

        is Html.Text -> {
            markupBuilder.append(html.content)
            emptyList()
        }

        is Html.Component -> when (html.component) {
            is Link<*> ->
                renderComponent("a", html.component, html, markupBuilder)

            is DropDownChoice<*> ->
                renderComponent("select", html.component, html, markupBuilder)

            is MarkupContainer ->
                renderComponent("div", html.component, html, markupBuilder)

            else -> {
                val tag = html.tag ?: "div"
                markupBuilder.append("<$tag wicket:id='${html.component.markupId}'>")
                html.children.forEach {
                    renderHtml(it, markupBuilder)
                }
                markupBuilder.append("</$tag>")
                listOf(html.component)
            }
        }
    }

    private fun renderComponent(
        tag: String,
        markupComponent: MarkupContainer,
        html: Html.Component,
        markupBuilder: StringBuilder
    ): List<Component> {
        val tag = html.tag ?: tag
        markupBuilder.append("<$tag wicket:id='${html.component.markupId}'>")
        html.children.forEach {
            renderHtml(it, markupBuilder).forEach {
                markupComponent.add(it)
            }
        }
        markupBuilder.append("</$tag>")
        return listOf(html.component)
    }

    /**
     *
     */
    abstract fun HtmlBuilder.render()

}

