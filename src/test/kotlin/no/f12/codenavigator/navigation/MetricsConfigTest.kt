package no.f12.codenavigator.navigation

import no.f12.codenavigator.config.OutputFormat
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetricsConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "after" to "2024-06-01",
            "top" to "10",
            "no-follow" to "",
            "root-package" to "com.example",
            "format" to "json",
        )

        val config = MetricsConfig.parse(props)

        assertEquals(LocalDate.of(2024, 6, 1), config.after)
        assertEquals(10, config.top)
        assertEquals(false, config.followRenames)
        assertEquals(PackageName("com.example"), config.rootPackage)
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = MetricsConfig.parse(emptyMap())

        assertEquals(OutputFormat.TEXT, config.format)
    }

    @Test
    fun `parses LLM format`() {
        val config = MetricsConfig.parse(mapOf("llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }

    @Test
    fun `defaults after to approximately one year ago`() {
        val config = MetricsConfig.parse(emptyMap())

        val expectedApprox = LocalDate.now().minusYears(1)
        assertTrue(config.after.isEqual(expectedApprox) || config.after.isAfter(expectedApprox.minusDays(1)))
    }

    @Test
    fun `defaults followRenames to true when no-follow absent`() {
        val config = MetricsConfig.parse(emptyMap())

        assertEquals(true, config.followRenames)
    }

    @Test
    fun `sets followRenames to false when no-follow present`() {
        val config = MetricsConfig.parse(mapOf("no-follow" to null))

        assertEquals(false, config.followRenames)
    }

    @Test
    fun `defaults top to 5`() {
        val config = MetricsConfig.parse(emptyMap())

        assertEquals(5, config.top)
    }

    @Test
    fun `defaults rootPackage to empty string`() {
        val config = MetricsConfig.parse(emptyMap())

        assertEquals(PackageName(""), config.rootPackage)
    }
}
