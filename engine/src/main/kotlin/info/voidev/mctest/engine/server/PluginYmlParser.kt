package info.voidev.mctest.engine.server

import info.voidev.mctest.api.AssertionFailedException

/**
 * A simplistic parser for the testee plugin.yml.
 * Does not implement a full YAML parser,
 * just scans line-by-line for the required keys.
 */
class PluginYmlParser {

    fun parseFromClasspath(): PluginYml {
        val istream = javaClass.classLoader.getResourceAsStream("plugin.yml")
            ?: throw AssertionFailedException("plugin.yml is not on the classpath")

        var main: String? = null
        var name: String? = null
        var apiVersion: String? = null

        istream.use {
            istream.reader().useLines { lines ->
                lines.forEach { line ->
                    when {
                        line.startsWith("main:") -> {
                            main = line.substring("main:".length).trim()
                        }
                        line.startsWith("name:") -> {
                            name = line.substring("name:".length).trim()
                        }
                        line.startsWith("api-version:") -> {
                            apiVersion = line.substring("api-version:".length).trim()
                        }
                    }
                }
            }
        }

        if (main == null || name == null) {
            throw AssertionFailedException("Invalid plugin.yml")
        }

        return PluginYml(name!!, main!!, apiVersion)
    }

}
