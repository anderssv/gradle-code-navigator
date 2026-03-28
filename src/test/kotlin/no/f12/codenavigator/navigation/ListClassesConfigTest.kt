package no.f12.codenavigator.navigation

import no.f12.codenavigator.config.OutputFormat
import no.f12.codenavigator.navigation.classinfo.ListClassesConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class ListClassesConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "format" to "json",
            "llm" to "false",
        )

        val config = ListClassesConfig.parse(props)

        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = ListClassesConfig.parse(emptyMap())

        assertEquals(OutputFormat.TEXT, config.format)
    }

    @Test
    fun `parses JSON format`() {
        val config = ListClassesConfig.parse(mapOf("format" to "json"))

        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `parses LLM format`() {
        val config = ListClassesConfig.parse(mapOf("llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }
}
