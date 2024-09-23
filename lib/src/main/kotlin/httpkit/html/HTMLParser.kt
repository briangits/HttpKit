package httpkit.html

import org.jsoup.Jsoup
import java.lang.IllegalArgumentException

/**
 * Provides methods for parsing HTML content.
 */
object HTMLParser {

    /**
     * Parses an HTML string into an [HTMLDocument].
     *
     * @param html The HTML content to parse.
     * @return The parsed [HTMLDocument].
     * @throws IllegalArgumentException If the provided HTML is empty or null.
     */
    fun parse(html: String): HTMLDocument {

        require(html.isNotBlank()) { "HTML content cannot be empty" }

        return HTMLDocument(Jsoup.parse(html))

    }

}