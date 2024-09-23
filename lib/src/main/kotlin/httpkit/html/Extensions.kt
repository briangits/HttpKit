package httpkit.html

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

internal fun Element.toHTMLElement(): HTMLElement = HTMLElement(this)

internal fun Elements.toHTMLElements(): HTMLElements = HTMLElements(this)