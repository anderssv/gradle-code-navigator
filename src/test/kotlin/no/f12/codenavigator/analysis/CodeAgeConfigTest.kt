package no.f12.codenavigator.analysis

import no.f12.codenavigator.config.OutputFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.LocalDate

class CodeAgeConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "after" to "2024-06-01",
            "top" to "10",
            "no-follow" to "",
            "format" to "json",
        )

        val config = CodeAgeConfig.parse(props)

        assertEquals(LocalDate.of(2024, 6, 1), config.after)
        assertEquals(10, config.top)
        assertEquals(false, config.followRenames)
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `defaults after to approximately one year ago when absent`() {
        val config = CodeAgeConfig.parse(emptyMap())

        val expectedApprox = LocalDate.now().minusYears(1)
        assertTrue(config.after.isEqual(expectedApprox) || config.after.isAfter(expectedApprox.minusDays(1)))
    }

    @Test
    fun `defaults top to 50 when absent`() {
        val config = CodeAgeConfig.parse(emptyMap())

        assertEquals(50, config.top)
    }

    @Test
    fun `defaults followRenames to true when no-follow absent`() {
        val config = CodeAgeConfig.parse(emptyMap())

        assertEquals(true, config.followRenames)
    }

    @Test
    fun `sets followRenames to false when no-follow present`() {
        val config = CodeAgeConfig.parse(mapOf("no-follow" to null))

        assertEquals(false, config.followRenames)
    }

    @Test
    fun `parses LLM format`() {
        val config = CodeAgeConfig.parse(mapOf("llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = CodeAgeConfig.parse(emptyMap())

        assertEquals(OutputFormat.TEXT, config.format)
    }
}
