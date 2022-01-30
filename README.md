# Wicket HTML DSL

[Apache Wicket][2] is a component based web framework for the
JVM. Pages and components are typically implemented by separating
markup (HTML) and the actual behavior (Kotlin/Java/Scala) into
separate files. To connect them back together, HTML elements are given
a `wicket:id` attribute with an id, which is referenced in the
corresponding class.

This project was an attempt at implementing a DSL in the spirit of
[kotlinx.html][1] for Wicket, while still being able to embed (and
thus reuse) existing components. Take a look at the following
[example](#Example) to get an idea of how that might have looked. 

You can also jump directly to the [problems](#Problems), which are the
reason, why I won't further follow up on this.

If you have an idea on how to make this work, feel free to fork it or
create an issue. 


## Example

Here is an example, of how to implement a simple counter with the DSL
and without it.

```kotlin
class MyPage: HtmlWebPage() {

    private val counter = Model.of(0)

    fun HtmlBuilder.render() = html {
        head {
            title { +"Hello World!" }
        }
        
        body {
            h1 { +"Count ${counter.object}" }
            link(onClick = { counter.`object`++ }) {
                +"Increment"
            }
            // link is a convenience function for the following
            component("a", object: Link<Void>(newId()) {
                override fun onClick() {
                    counter.`object`--
                }
            })
        }
    }
    
}
```

In ordinary Wicket, this would require two separate files and look
like the following.

```kotlin
// MyPage.kt
class MyPage: WebPage() {

    private val counter = Model.of(0)
    
    override fun onInitilize() {
        super.onInitialize()
        
        add(Label("count", counter))
        
        add(object: Link<Nothing>("increment") {
            override fun onClick() {
                counter.`object`++
            }
        })
        add(object: Link<Nothing>("decrement") {
            override fun onClick() {
                counter.`object`--
            }
        })
    }
    
}
```

```html
// MyPage.html
<html>
    <head>
        <title>Hello World</title>
    </head>
    <body>
        <h1 wicket:id="count"></h1>
        <a wicket:id="increment">Increment</a>
        <a wicket:id="decrement">Decrement</a>
    </body>
</html>
```


## Problem

There is a problem which breaks this DSL a bit, when working with
repeaters, such as `ListView`. To understand that problem, we need to
take a look at how the DSL is implemented and the order in which it
will be executed.

Wicket renders a page in multiple phases. For our purposes only two
are relevant.

1. Retrieve the markup of the page to be rendered. The default way is
   to have a separate markup file with the same name (as seen in the
   example). However, a page or component can implement the interface
   `IMarkupResourceStreamProvider` to decide by itself, where the
   markup comes from. This is how the `HtmlWebPage` from the example
   does it.
2. Wicket will call some methods in a specific order to render the
   page and connect components with specific markup elements.

Knowing this process, adding a `ListView` would work in the following
way.

```kotlin
class MyPage: HtmlWebPage() {

    private val data = ListModel(listOf("World", "Earth"))

    fun HtmlBuilder.render() = html {
        head {
            title { +"Hello World!" }
        }
        
        body {
            component("div", object: ListView<String>(newId(), data) {
                override fun onPopulateItem(item: ListItem<String>) {
                    item.add(Label("name", item.model))
                }
            }) {
                span(Attribute("wicket:id", "name")) {}
            }
        }
    }
    
}
```

The problem with this, is that users need to manage markup ids by
themselves once again. That is, because by the time, the markup is
requested by Wicket, the ListView has not yet been rendered. So it is
impossible to implement the following function:

```kotlin
class MyPage: HtmlWebPage() {

    private val data = ListModel(listOf("World", "Earth"))

    fun HtmlBuilder.render() = html {
        head {
            title { +"Hello World!" }
        }
        
        body {
            listView("div", data) { item ->
                component("span", Label(newId(), item))
            }
        }
    }
    
}
```



[1]: https://github.com/Kotlin/kotlinx.html
[2]: https://wicket.apache.org/
