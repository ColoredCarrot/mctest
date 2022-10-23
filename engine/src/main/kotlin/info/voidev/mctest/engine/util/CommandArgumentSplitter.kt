package info.voidev.mctest.engine.util

object CommandArgumentSplitter {

    /**
     * Parses the given [string][s] as command line arguments.
     *
     * ### Rules
     * - Surrounding whitespace is ignored
     * - Arguments are seperated by a whitespace sequence
     * - Arguments can be quoted to allow them to contain whitespace
     * - Quotes can be escaped by doubling them (`""` represents one literal `"`)
     *
     * ### Examples
     * - `foo bar baz` &rarr; `[foo, bar, baz]`
     * - `foo "bar baz"` &rarr; `[foo, bar baz]`
     * - `foo "bar""baz"` &rarr; `[foo, bar"baz]`
     */
    fun split(s: String): List<String> {
        val trimmed = s.trim()

        if (trimmed.isEmpty()) {
            return emptyList()
        }

        val result = ArrayList<String>()
        val current = StringBuilder()

        var lastWasQuote = false
        var inQuotes = false
        for (c in trimmed) {
            if (c == '"') {
                if (!lastWasQuote) {
                    // Don't know how to handle this quote yet, because it depends on if there is a quote right after it
                    lastWasQuote = true
                    continue
                }

                // Two consecutive quotes are an escaped quote
                current.append('"')
                lastWasQuote = false
                continue
            }

            if (lastWasQuote) {
                // Last char was a single quote (not two consecutive ones)
                inQuotes = !inQuotes
            }
            lastWasQuote = false

            if (c.isWhitespace() && !inQuotes) {
                if (current.isNotEmpty()) {
                    result += current.toString()
                }
                current.clear()
                continue
            }

            current.append(c)
        }

        if (inQuotes && !lastWasQuote) {
            throw IllegalArgumentException("Unclosed quote in \"$s\"")
        }

        if (current.isNotEmpty()) {
            result += current.toString()
        }

        return result
    }

    fun join(args: List<String>): String {
        return args.joinToString(" ") { arg ->
            var res = arg.replace("\"", "\"\"")
            if (res.any { it.isWhitespace() }) {
                res = '"' + res + '"'
            }
            res
        }
    }
}
