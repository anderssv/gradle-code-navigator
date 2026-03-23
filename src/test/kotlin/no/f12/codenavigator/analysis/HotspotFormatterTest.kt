package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class HotspotFormatterTest {

    @Test
    fun `formats empty list`() {
        val result = HotspotFormatter.format(emptyList())

        assertEquals("No hotspots found.", result)
    }

    @Test
    fun `formats single hotspot as table`() {
        val hotspots = listOf(Hotspot("src/main/Foo.kt", 10, 150))

        val result = HotspotFormatter.format(hotspots)

        val lines = result.lines()
        assertEquals(2, lines.size)
        assert(lines[0].contains("File")) { "Header should contain 'File'" }
        assert(lines[0].contains("Revisions")) { "Header should contain 'Revisions'" }
        assert(lines[0].contains("Churn")) { "Header should contain 'Churn'" }
        assert(lines[1].contains("src/main/Foo.kt")) { "Row should contain file path" }
        assert(lines[1].contains("10")) { "Row should contain revision count" }
        assert(lines[1].contains("150")) { "Row should contain churn" }
    }

    @Test
    fun `formats multiple hotspots with aligned columns`() {
        val hotspots = listOf(
            Hotspot("src/main/BigFile.kt", 25, 1200),
            Hotspot("src/main/Foo.kt", 10, 150),
            Hotspot("src/test/BarTest.kt", 3, 30),
        )

        val result = HotspotFormatter.format(hotspots)

        val lines = result.lines()
        assertEquals(4, lines.size)
        assert(lines[0].contains("File"))
        assert(lines[1].contains("src/main/BigFile.kt"))
        assert(lines[2].contains("src/main/Foo.kt"))
        assert(lines[3].contains("src/test/BarTest.kt"))
    }
}
