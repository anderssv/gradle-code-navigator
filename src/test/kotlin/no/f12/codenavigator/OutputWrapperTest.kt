package no.f12.codenavigator

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputWrapperTest {

    @Test
    fun `non-JSON output is returned as-is`() {
        val output = "some table output"

        val result = OutputWrapper.wrap(output, jsonFormat = false)

        assertEquals("some table output", result)
    }
    @Test
    fun `JSON output is wrapped with begin and end markers on separate lines`() {
        val output = "[{\"className\":\"Foo\"}]"

        val result = OutputWrapper.wrap(output, jsonFormat = true)

        assertEquals("---CNAV_BEGIN---\n[{\"className\":\"Foo\"}]\n---CNAV_END---", result)
    }
}
