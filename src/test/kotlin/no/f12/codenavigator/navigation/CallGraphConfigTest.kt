package no.f12.codenavigator.navigation

import no.f12.codenavigator.OutputFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CallGraphConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "method" to "com.example.MyClass.myMethod",
            "maxdepth" to "5",
            "projectonly" to "true",
            "format" to "json",
            "llm" to "false",
        )

        val config = CallGraphConfig.parse(props)

        assertEquals("com.example.MyClass.myMethod", config.method)
        assertEquals(5, config.maxDepth)
        assertEquals(true, config.projectOnly)
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `throws when method is missing`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CallGraphConfig.parse(mapOf("maxdepth" to "3"))
        }

        assertEquals(true, exception.message?.contains("method"))
    }

    @Test
    fun `defaults maxdepth to 3 when not provided`() {
        val config = CallGraphConfig.parse(
            mapOf("method" to "com.example.MyClass.myMethod"),
        )

        assertEquals(3, config.maxDepth)
    }

    @Test
    fun `defaults projectOnly to false`() {
        val config = CallGraphConfig.parse(
            mapOf("method" to "com.example.MyClass.myMethod", "maxdepth" to "3")
        )

        assertEquals(false, config.projectOnly)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = CallGraphConfig.parse(
            mapOf("method" to "com.example.MyClass.myMethod", "maxdepth" to "3")
        )

        assertEquals(OutputFormat.TEXT, config.format)
    }

    @Test
    fun `parses LLM format`() {
        val config = CallGraphConfig.parse(
            mapOf("method" to "com.example.MyClass.myMethod", "maxdepth" to "3", "llm" to "true")
        )

        assertEquals(OutputFormat.LLM, config.format)
    }
}
