# Wicket HTML DSL

This project was an attempt at implementing a DSL in the spirit of
[kotlinx.html][1] for [Apache Wicket][2]. Its goal was to allow
embedding (and thus reusing) existing components, while eliminating
the need for separate markup files and therefore eliminating the need
for managing `wicket:id`s. Take a look at the following
[example](#Example) to get an idea of how that might have looked.

However, due to the nature of Wicket, working with repeaters, such as
`ListView`, becomes [problematic](#Problem). While it is possible to
embed them, it is necessary to manage `wicket:id`s again. This breaks
the abstraction and may result in confusion about when to manage
`wicket:id`s and when not to. Therefore, I will not work on this any
further. If you have an idea on how to make this work nicely, feel
free to fork it or create an issue.


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

While the above example works fine for most cases, a problem arises,
when we take a stab at working with repeaters such as `ListView`. The
reason for this is simple: Since markup and behavior are separate, the
full markup of a page or component must be known, before child
components are actually added and then populated with data. This means
the markup in a `ListView` cannot vary between items and has to be
more or less static.

Therefore it would be impossible to implement the ergonomic and
natural way to work with a `ListView`, shown in the following example.

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

Instead, it would be necessary to drop down to managing `wicket:id`s
by ourselves again. This breaks the abstraction and is confusing.

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

This means, the separation of markup files and components/behavior, is
so ingrained in Wicket, that it is impossible to nicely bridge it,
except for probably compiling to it.



[1]: https://github.com/Kotlin/kotlinx.html
[2]: https://wicket.apache.org/
