package no.f12.codenavigator

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputWrapperTest {

    @Test
    fun `text output is returned as-is`() {
        val output = "some table output"

        val result = OutputWrapper.wrap(output, OutputFormat.TEXT)

        assertEquals("some table output", result)
    }

    @Test
    fun `JSON output is wrapped with begin and end markers`() {
        val output = "[{\"className\":\"Foo\"}]"

        val result = OutputWrapper.wrap(output, OutputFormat.JSON)

        assertEquals("---CNAV_BEGIN---\n[{\"className\":\"Foo\"}]\n---CNAV_END---", result)
    }

    @Test
    fun `LLM output is wrapped with begin and end markers`() {
        val output = "com.example.Foo Foo.kt"

        val result = OutputWrapper.wrap(output, OutputFormat.LLM)

        assertEquals("---CNAV_BEGIN---\ncom.example.Foo Foo.kt\n---CNAV_END---", result)
    }
}
