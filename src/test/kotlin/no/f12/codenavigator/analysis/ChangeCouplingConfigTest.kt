package no.f12.codenavigator.analysis

import no.f12.codenavigator.OutputFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.LocalDate

class ChangeCouplingConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "after" to "2024-06-01",
            "min-shared-revs" to "10",
            "min-coupling" to "50",
            "max-changeset-size" to "20",
            "no-follow" to "",
            "format" to "json",
        )

        val config = ChangeCouplingConfig.parse(props)

        assertEquals(LocalDate.of(2024, 6, 1), config.after)
        assertEquals(10, config.minSharedRevs)
        assertEquals(50, config.minCoupling)
        assertEquals(20, config.maxChangesetSize)
        assertEquals(false, config.followRenames)
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `defaults after to approximately one year ago when absent`() {
        val config = ChangeCouplingConfig.parse(emptyMap())

        val expectedApprox = LocalDate.now().minusYears(1)
        assertTrue(config.after.isEqual(expectedApprox) || config.after.isAfter(expectedApprox.minusDays(1)))
    }

    @Test
    fun `defaults minSharedRevs to 5 when absent`() {
        val config = ChangeCouplingConfig.parse(emptyMap())

        assertEquals(5, config.minSharedRevs)
    }

    @Test
    fun `defaults minCoupling to 30 when absent`() {
        val config = ChangeCouplingConfig.parse(emptyMap())

        assertEquals(30, config.minCoupling)
    }

    @Test
    fun `defaults maxChangesetSize to 30 when absent`() {
        val config = ChangeCouplingConfig.parse(emptyMap())

        assertEquals(30, config.maxChangesetSize)
    }

    @Test
    fun `defaults followRenames to true when no-follow absent`() {
        val config = ChangeCouplingConfig.parse(emptyMap())

        assertEquals(true, config.followRenames)
    }

    @Test
    fun `sets followRenames to false when no-follow present`() {
        val config = ChangeCouplingConfig.parse(mapOf("no-follow" to null))

        assertEquals(false, config.followRenames)
    }

    @Test
    fun `parses LLM format`() {
        val config = ChangeCouplingConfig.parse(mapOf("llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = ChangeCouplingConfig.parse(emptyMap())

        assertEquals(OutputFormat.TEXT, config.format)
    }
}
