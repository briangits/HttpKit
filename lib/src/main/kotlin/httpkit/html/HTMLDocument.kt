package httpkit.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Represents an HTML document, providing access to its element sand metadata.
 *
 * @property document The underlying Jsoup [Document] object.
 */
class HTMLDocument internal constructor(private val document: Document) : HTMLParentElement {

    /**
     * The title of the HTML document.
     */
    val title: String? = document.title()

    override val children: HTMLElements = HTMLElements(document.children())

}