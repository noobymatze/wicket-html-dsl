package io.noobymatze.wicket.html.dsl

import org.apache.wicket.AttributeModifier
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.SubmitLink
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.IModel
import java.util.*


/**
 *
 * @param children
 */
@HtmlBuilder.DslBuilder
class HtmlBuilder internal constructor(
    internal val children: MutableList<Html> = mutableListOf(),
) {

    @kotlin.DslMarker
    annotation class DslBuilder

    /**
     * Returns a new unique ID for an element.
     */
    fun newId(): String =
        UUID.randomUUID().toString()

    /**
     * @param name
     * @param attributes
     */
    fun node(
        name: String,
        vararg attributes: Attribute,
        init: HtmlBuilder.() -> Unit
    ) {
        val result = HtmlBuilder().apply(init)
        this.children.add(Html.Node(name, attributes.toList(), result.children))
    }

    /**
     *
     * @param component
     * @param attributes
     * @param init
     * @return
     */
    fun <C: Component> component(
        component: C,
        vararg attributes: AttributeModifier,
        init: HtmlBuilder.() -> Unit = {}
    ): C {
        val result = HtmlBuilder().apply(init)
        component.setMarkupId(component.id)
        this.children.add(Html.Component(null, component, attributes.toList(), result.children))
        return component
    }

    /**
     *
     * @param tag
     * @param component
     * @param attributes
     * @param init
     */
    fun <C: Component> component(
        tag: String,
        component: C,
        vararg attributes: AttributeModifier,
        init: HtmlBuilder.() -> Unit = {}
    ): C {
        val result = HtmlBuilder().apply(init)
        component.setMarkupId(component.id)
        this.children.add(Html.Component(tag, component, attributes.toList(), result.children))
        return component
    }

    /**
     *
     * @param onClick
     * @param visible
     * @param attributes
     * @param init
     */
    fun link(
        vararg attributes: AttributeModifier,
        onClick: (() -> Unit)? = null,
        visible: (() -> Boolean)? = null,
        f: HtmlBuilder.() -> Unit
    ) {
        component(object: Link<Nothing>(newId()) {
            override fun onClick() {
                if (onClick != null) {
                    onClick()
                }
            }

            override fun onConfigure() {
                super.onConfigure()

                if (visible != null) {
                    isVisible = visible()
                }
            }
        }, *attributes) { f() }
    }

    /**
     *
     * @param onClick
     * @param visible
     * @param attributes
     * @param init
     */
    fun ajaxLink(
        onClick: ((AjaxRequestTarget) -> Unit)? = null,
        visible: (() -> Boolean)? = null,
        vararg attributes: AttributeModifier,
        init: HtmlBuilder.() -> Unit
    ) {
        component(object: AjaxLink<Nothing>(newId()) {
            override fun onClick(target: AjaxRequestTarget) {
                if (onClick != null) {
                    onClick(target)
                }
            }

            override fun onConfigure() {
                super.onConfigure()

                if (visible != null) {
                    isVisible = visible()
                }
            }
        }, *attributes) { init() }
    }

    /**
     *
     * @param onClick
     * @param visible
     * @param attributes
     * @param init
     */
    fun submitLink(
        vararg attributes: AttributeModifier,
        onSubmit: (() -> Unit)? = null,
        visible: (() -> Boolean)? = null,
        init: HtmlBuilder.() -> Unit
    ) {
        component("button", object: SubmitLink(newId()) {
            override fun onSubmit() {
                if (onSubmit != null) {
                    onSubmit()
                }
            }

            override fun onConfigure() {
                super.onConfigure()

                if (visible != null) {
                    isVisible = visible()
                }
            }
        }, *attributes) { init() }
    }

    /**
     *
     * @param onSubmit
     * @param visible
     * @param attributes
     * @param
     */
    fun form(
        id: String,
        vararg attributes: AttributeModifier,
        onSubmit: (() -> Unit)? = null,
        visible: (() -> Boolean)? = null,
        init: HtmlBuilder.() -> Unit
    ) {
        component("form", object: Form<Nothing>(id) {
            override fun onSubmit() {
                super.onSubmit()

                onSubmit?.invoke()
            }

            override fun onConfigure() {
                super.onConfigure()

                if (visible != null) {
                    isVisible = visible()
                }
            }
        }, *attributes) { init() }
    }

    /**
     *
     */
    fun <T> listView(tag: String, model: IModel<List<T>>, f: HtmlBuilder.(IModel<T>) -> Unit) {
        component(tag, object: ListView<T>(newId(), model) {
            override fun populateItem(item: ListItem<T>) {
                val result = HtmlBuilder().apply { f(item.model) }
            }
        })

    }

    /**
     *
     */
    fun div(
        vararg attributes: Attribute,
        init: HtmlBuilder.() -> Unit
    ) {
        node("div", *attributes) { init() }
    }

    fun text(content: String) {
        this.children.add(Html.Text(content))
    }

    operator fun String.unaryPlus() {
        text(this)
    }

    fun html(vararg attributes: Attribute, f: HtmlBuilder.() -> Unit) {
        node("html", *attributes) { f() }
    }

    fun head(vararg attributes: Attribute, f: HtmlBuilder.() -> Unit) {
        node("head", *attributes) { f() }
    }

    fun body(vararg attributes: Attribute, f: HtmlBuilder.() -> Unit) {
        node("body", *attributes) { f() }
    }

    fun title(content: String) {
        node("title") {
            text(content)
        }
    }

    /**
     *
     */
    fun h1(
        vararg attributes: Attribute,
        f: HtmlBuilder.() -> Unit
    ) {
        node("h1", *attributes) { f() }
    }

    fun button(vararg attributes: Attribute, f: HtmlBuilder.() -> Unit) {
        node("button", *attributes) { f() }
    }

}
