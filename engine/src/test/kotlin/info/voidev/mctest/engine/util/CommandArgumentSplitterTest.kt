package info.voidev.mctest.engine.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class CommandArgumentSplitterTest {

    private val splitter = CommandArgumentSplitter

    @Test
    fun `empty input`() {
        assertEquals(emptyList<String>(), splitter.split(""))
    }

    @Test
    fun `blank input`() {
        assertEquals(emptyList<String>(), splitter.split("   "))
    }

    @ParameterizedTest
    @MethodSource("singleArgProvider")
    fun `single argument`(inp: Pair<List<String>, String>) {
        assertEquals(inp.first, splitter.split(inp.second))
    }

    @ParameterizedTest
    @MethodSource("twoArgsProvider")
    fun `two arguments`(inp: Pair<List<String>, String>) {
        assertEquals(inp.first, splitter.split(inp.second))
    }

    @Test
    fun `escaped quotes`() {
        assertEquals(listOf("foo", "a b\" c", "bar"), splitter.split("""foo "a b"" c" bar"""))
    }

    @Test
    fun `unclosed quote should fail`() {
        assertThrows<IllegalArgumentException> {
            splitter.split("foo \"bar")
        }
    }

    companion object {
        private fun cartesianProduct(
            fst: List<Pair<List<String>, String>>,
            snd: List<Pair<List<String>, String>>,
            sep: String = " ",
        ) =
            fst.stream().flatMap { (fstSplit, fstSrc) ->
                snd.stream().map { (sndSplit, sndSrc) ->
                    (fstSplit + sndSplit) to (fstSrc + sep + sndSrc)
                }
            }

        @JvmStatic
        fun unquotedSingleArgProvider() =
            listOf("foo", "-foo", "--foo", "foo=bar", "-foo=bar", "äöü")

        @JvmStatic
        fun quotedSingleArgProvider() =
            listOf("\"foo\"", "\"-foo\"", "\"with some  spaces\"", "\"äöü\"")

        @JvmStatic
        fun singleArgProvider(): List<Pair<List<String>, String>> =
            (unquotedSingleArgProvider() + quotedSingleArgProvider()).map {
                listOf(it.removeSurrounding("\"")) to it
            }

        @JvmStatic
        fun twoArgsProvider(): Stream<Pair<List<String>, String>> =
            Stream.concat(
                cartesianProduct(singleArgProvider(), singleArgProvider(), " "),
                cartesianProduct(singleArgProvider(), singleArgProvider(), "   ")
            )
    }
}
