package io.noobymatze.wicket.html.dsl

import org.apache.wicket.AttributeModifier
import org.apache.wicket.Component


/**
 * An [Html] describes an html document with
 */
internal sealed interface Html {

    /**
     * A [Node]
     *
     * @param name
     * @param attributes
     * @param children
     */
    data class Node(
        val name: String,
        val attributes: List<Attribute>,
        val children: List<Html>
    ): Html

    data class Text(
        val content: String,
    ): Html

    data class Component(
        val tag: String? = null,
        val component: org.apache.wicket.Component,
        val attributes: List<AttributeModifier>,
        val children: List<Html>,
    ): Html

}

