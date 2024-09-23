package httpkit.html

import org.jsoup.nodes.Element

/**
 * Represents a single HTML element, providing access toits tag, attributes, and content.
 *
 * @property element The underlying Jsoup [Element] object.
 */
class HTMLElement internal constructor(private val element: Element) : HTMLParentElement {

    /**
     * The name of the HTML tag for this element.
     */
    val tag: String = element.tagName()

    /**
     * The text content of this element and its children.
     */
    val text: String = element.text()

    /**
     * The raw HTML content of this element and its children.
     */
    val html: String = element.html()

    /**
     * A map of attributes associated with this element.
     */
    val attributes: Map<String, String> = element.attributes().associate { it.key to it.value }

    /**
     * The parent element of this element, or null if it has no parent.
     */
    val parent: HTMLElement? by lazy { element.parent()?.toHTMLElement() }

    /**
     * A list of sibling elements of this element.
     */
    val siblings: HTMLElements by lazy { element.siblingElements().toHTMLElements() }

    /**
     * The previous sibling element of this element, or null if there is no previous sibling.
     */
    val previousSibling: HTMLElement? by lazy { element.previousElementSibling()?.toHTMLElement() }

    /**
     * The next sibling element of this element, or null if there is no next sibling.
     */
    val nextSibling: HTMLElement? by lazy { element.nextElementSibling()?.toHTMLElement() }

    override val children: HTMLElements = HTMLElements(element.children())

    /**
     * Gets an attribute value by name, or null if the attribute is not present.
     *
     * @param name The name of the attribute.
     * @return The attribute value, or null if not found.
     */
    fun getAttributeOrNull(name: String): String? = attributes[name]

}
