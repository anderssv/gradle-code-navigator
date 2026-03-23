package no.f12.codenavigator.analysis

import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeCouplingFormatterTest {

    @Test
    fun `formats empty list`() {
        val result = ChangeCouplingFormatter.format(emptyList())

        assertEquals("No coupling found.", result)
    }

    @Test
    fun `formats coupling pairs as table`() {
        val pairs = listOf(
            CoupledPair("src/Foo.kt", "src/Bar.kt", 85, 10, 12),
        )

        val result = ChangeCouplingFormatter.format(pairs)

        val lines = result.lines()
        assertEquals(2, lines.size)
        assert(lines[0].contains("Entity"))
        assert(lines[0].contains("Coupled"))
        assert(lines[0].contains("Degree"))
        assert(lines[0].contains("Shared"))
        assert(lines[1].contains("src/Foo.kt"))
        assert(lines[1].contains("src/Bar.kt"))
        assert(lines[1].contains("85%"))
        assert(lines[1].contains("10"))
    }
}
