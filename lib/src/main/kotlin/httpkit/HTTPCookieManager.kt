package httpkit

/**
 * An interface for managing HTTP cookies.
 */
interface HTTPCookieManager {

    companion object {

        /**
         * A default cookie manager that stores cookies in memory.
         */
        val defaultManager: HTTPCookieManager by lazy {
            object : HTTPCookieManager {

                val store: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

                /**
                 * Sets cookies for a given site.
                 *
                 * @param site The site (domain) for which to set the cookies.
                 * @param cookies A map of cookie names to cookie values.
                 */
                override fun set(site: String, cookies: Map<String, String>) {

                    if (!store.containsKey(site)) store[site] = mutableMapOf()

                    store[site]?.putAll(cookies)

                }

                /**
                 * Gets cookies for a given site.
                 *
                 * @param site The site (domain) for which to retrieve cookies.
                 * @return A map of cookie names to cookie values for the given site.
                 */
                override fun get(site: String): Map<String, String> = store[site] ?: mutableMapOf()

                /**
                 * Removes cookies for a given site.
                 *
                 * @param site The site (domain) for which to remove cookies.
                 * @param cookies A list of cookie names to remove.
                 */
                override fun remove(site: String, cookies: List<String>) {

                    if (store.containsKey(site)) return

                    store[site] = store[site]?.filter { it.key in cookies }?.toMutableMap()
                        ?: mutableMapOf()

                }

            }

        }

        /**
         * A cookie manager that does not store or manage any cookies.
         */
        val noManager: HTTPCookieManager by lazy {
            object : HTTPCookieManager {
                /**
                 * Does nothing, as this manager does not store cookies.
                 *
                 * @param site Ignored.
                 * @param cookies Ignored.
                 */
                override fun set(site: String, cookies: Map<String, String>) {}

                /**
                 * Always returns an empty map, as this manager does not store cookies.
                 *
                 * @param site Ignored.
                 * @return An empty map.
                 */
                override fun get(site: String): Map<String, String> = emptyMap()

                /**
                 * Does nothing, as this manager does not store cookies.
                 *
                 * @param site Ignored.
                 * @param cookies Ignored.
                 */
                override fun remove(site: String, cookies: List<String>) {}

            }
        }

    }

    /**
     * Sets cookies for a given site.
     *
     * @param site The site (domain) for which to set the cookies.
     * @param cookies A map of cookie names to cookie values.
     */
    operator fun set(site: String, cookies: Map<String, String>)

    /**
     * Gets cookies for a given site.
     *
     * @param site The site (domain) for which to retrieve cookies.
     * @return A map of cookie names to cookie values for the given site.
     */
    operator fun get(site: String): Map<String, String>

    /**
     * Removes cookies for a given site.
     *
     * @param site The site (domain) for which to remove cookies.
     * @param cookies A list of cookie names to remove.
     */
    fun remove(site: String, cookies: List<String>)

}