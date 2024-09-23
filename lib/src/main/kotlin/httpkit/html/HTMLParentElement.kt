package httpkit.html

/**
 * Represents an HTML element that can contain child elements.
 */
interface HTMLParentElement {

    /**
     * The child elements of this element.
     */
    val children: HTMLElements

    /**
     * Finds the first child element matching the given CSS selector, or null if none is found.
     *
     * @param selector The CSS selector to apply.
     * @return The first matching child element, or null if none is found.
     */
    fun findFirst(selector: String): HTMLElement? = children.select(selector).firstOrNull()

    /**
     * Returns the child element at the specified index, or null if the index is out of bounds.
     *
     * @param index The index of the child element to retrieve.
     * @return The child element at the given index, or null if the index is invalid.
     */
    fun childAt(index: Int): HTMLElement? = children.elementAtOrNull(index)

    /**
     * Finds the first child element with the given ID, or null if none is found.
     *
     * @param id The ID to search for.
     * @return The first matching child element, or null if none is found.*/
    fun findElementById(id: String): HTMLElement? =
        findFirst("[id=$id]")

    /**
     * Finds all child elements with the given tag name.
     *
     * @param name The tag name to search for.
     * @return A list of matching child elements.
     */
    fun findElementsByTag(name: String): List<HTMLElement> =
        children.filter { it.tag.equals(name, ignoreCase = true) }

    /**
     * Finds all child elements that have an attribute with the given name.
     *
     * @param name The name of the attribute to search for.
     * @return A list of matching child elements.
     */
    fun findElementsWithAttribute(name: String): List<HTMLElement> =
        children.filter { it.attributes.containsKey(name) }

    /**
     * Finds all child elements that have an attribute with the given name and value.
     *
     * @param name The name of the attribute to search for.* @param value The value of the attribute to search for.
     * @return A list of matching child elements.
     */
    fun findElementsWithAttribute(name: String, value: String): List<HTMLElement> =
        children.filter { it.attributes[name] == value }

    /**
     * Selects child elements matching the given CSS selector.
     *
     * @param selector TheCSS selector to apply.
     * @return A list of matching child elements.
     */
    fun select(selector: String): List<HTMLElement> = children.select(selector)

}