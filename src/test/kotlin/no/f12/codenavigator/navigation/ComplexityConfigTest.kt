package no.f12.codenavigator.navigation

import no.f12.codenavigator.OutputFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ComplexityConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "class" to "AdminRoutes",
            "projectonly" to "false",
            "detail" to "true",
            "format" to "json",
        )

        val config = ComplexityConfig.parse(props)

        assertEquals("AdminRoutes", config.classPattern)
        assertEquals(false, config.projectOnly)
        assertEquals(true, config.detail)
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `requires class property - throws on missing`() {
        assertFailsWith<IllegalArgumentException> {
            ComplexityConfig.parse(emptyMap())
        }
    }

    @Test
    fun `defaults projectOnly to true when absent`() {
        val config = ComplexityConfig.parse(mapOf("class" to "Foo"))

        assertEquals(true, config.projectOnly)
    }

    @Test
    fun `defaults detail to false when absent`() {
        val config = ComplexityConfig.parse(mapOf("class" to "Foo"))

        assertEquals(false, config.detail)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = ComplexityConfig.parse(mapOf("class" to "Foo"))

        assertEquals(OutputFormat.TEXT, config.format)
    }

    @Test
    fun `parses LLM format`() {
        val config = ComplexityConfig.parse(mapOf("class" to "Foo", "llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }
}
