package io.noobymatze.wicket.html

import io.noobymatze.wicket.html.dsl.Attribute
import io.noobymatze.wicket.html.dsl.HtmlWebPage
import io.noobymatze.wicket.html.dsl.HtmlBuilder
import org.apache.wicket.AttributeModifier
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.form.FormComponent
import org.apache.wicket.markup.html.panel.FeedbackPanel
import org.apache.wicket.model.Model

class ExamplePage: HtmlWebPage() {

    private val name = Model.of("World")

    private fun changeName() {
        name.`object` = "Matthias"
    }

    override fun HtmlBuilder.render() = frame("Hello World!") {
        div {
            val label = component(Label("text", name.map { "Hello $it!" })
                .setOutputMarkupPlaceholderTag(true))
            link("link", onClick = ::changeName) {
                text("Update this")
            }
            ajaxLink("ajaxLink", onClick = {
                changeName()
                it.add(label)
            }) {
                text("Update with Ajax")
            }

            form("myForm", AttributeModifier.append("class", "")) {
                formField("Name", "select") {
                    DropDownChoice("newName", name, listOf("Matthias", "World"))
                }

                submitLink("test") {
                    text("Submit this")
                }
            }
        }
    }

    companion object {

        const val PATH = "/builder/example"

    }

}

fun <T> HtmlBuilder.formField(
    label: String,
    tag: String? = null,
    component: () -> FormComponent<T>
) {
    val c = component()
    div(Attribute("class", "row")) {
        node("label", Attribute("wicket:for", "${c.markupId}")) {
            text(label)
        }

        if (tag == null) {
            component(c)
        }
        else {
            component(tag, c)
        }

        component(FeedbackPanel("${c.markupId}.feedback", ComponentFeedbackMessageFilter(c)))
    }
}

fun HtmlBuilder.frame(title: String, f: HtmlBuilder.() -> Unit) {
    html {
        head {
            title(title)
        }

        body {
            f()
        }
    }
}

