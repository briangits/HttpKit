package httpkit.html

import org.jsoup.select.Elements

/**
 * Represents a collection of [HTMLElement] objects, providing convenient methods for filtering and traversal.
 *
 * @property elements The underlying Jsoup [Elements] object.
 */
class HTMLElements internal constructor(
    private val elements: Elements
) : List<HTMLElement> by elements.map({ HTMLElement(it) }) {

    /**
     * Selects elements within this collection using a CSS selector.
     *
     * @param selector The CSS selector to apply.
     * @return A new [HTMLElements] collection containing the selected elements.
     */
    fun select(selector: String): HTMLElements = HTMLElements(elements.select(selector))

}