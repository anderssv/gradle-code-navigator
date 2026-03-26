package no.f12.codenavigator.analysis

import no.f12.codenavigator.config.OutputFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.LocalDate

class HotspotConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "after" to "2024-06-01",
            "min-revs" to "3",
            "top" to "10",
            "no-follow" to "",
            "format" to "json",
        )

        val config = HotspotConfig.parse(props)

        assertEquals(LocalDate.of(2024, 6, 1), config.after)
        assertEquals(3, config.minRevs)
        assertEquals(10, config.top)
        assertEquals(false, config.followRenames)
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `defaults after to approximately one year ago when absent`() {
        val config = HotspotConfig.parse(emptyMap())

        val expectedApprox = LocalDate.now().minusYears(1)
        assertTrue(config.after.isEqual(expectedApprox) || config.after.isAfter(expectedApprox.minusDays(1)))
    }

    @Test
    fun `defaults minRevs to 1 when absent`() {
        val config = HotspotConfig.parse(emptyMap())

        assertEquals(1, config.minRevs)
    }

    @Test
    fun `defaults top to 50 when absent`() {
        val config = HotspotConfig.parse(emptyMap())

        assertEquals(50, config.top)
    }

    @Test
    fun `defaults followRenames to true when no-follow absent`() {
        val config = HotspotConfig.parse(emptyMap())

        assertEquals(true, config.followRenames)
    }

    @Test
    fun `sets followRenames to false when no-follow present`() {
        val config = HotspotConfig.parse(mapOf("no-follow" to null))

        assertEquals(false, config.followRenames)
    }

    @Test
    fun `parses LLM format`() {
        val config = HotspotConfig.parse(mapOf("llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = HotspotConfig.parse(emptyMap())

        assertEquals(OutputFormat.TEXT, config.format)
    }
}
