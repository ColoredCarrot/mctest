package info.voidev.mctest.example

import info.voidev.mctest.api.Disabled
import info.voidev.mctest.api.MCTest
import org.assertj.core.api.Assertions.fail

@Disabled
class DisabledTest {
    @MCTest
    fun `test in @Disabled class should not be executed`() {
        fail<Nothing>("Test in @Disabled class executed")
    }
}
