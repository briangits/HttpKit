package httpkit.json

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*** Provides methods for parsing JSON content using Gson.
 */
object JsonParser {

    private val gson = Gson()

    /**
     * Parses a JSON string into a Map<String, Any?> containing only Kotlin built-in types.
     *
     * @param json The JSON string to parse.
     * @return The parsed Map<String, Any?>.
     * @throws JSONParseException If an error occurs during parsing.
     */
    fun parseToMap(json: String): Map<String, Any?> {
        return try {
            val typeToken = object : TypeToken<Map<String, Any?>>() {}.type
            gson.fromJson(json, typeToken)
        } catch (e: Exception) {
            throw JSONParseException("Error parsing JSON content", e)
        }
    }

    /**
     * Parses a JSON string into a List<Any?> containing only Kotlin built-in types.
     *
     * @param json The JSON string to parse.
     * @return The parsed List<Any?>.
     * @throws JSONParseException If an error occurs during parsing.
     */
    fun parseToList(json: String): List<Any?> {
        return try {
            val typeToken = object : TypeToken<List<Any?>>() {}.type
            gson.fromJson(json, typeToken)
        } catch (e: Exception) {
            throw JSONParseException("Error parsing JSON content", e)
        }
    }
}