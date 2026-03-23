package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeAgeFormatterTest {

    @Test
    fun `formats empty list`() {
        val result = CodeAgeFormatter.format(emptyList())

        assertEquals("No files found.", result)
    }

    @Test
    fun `formats file ages as table`() {
        val ages = listOf(
            FileAge("src/Old.kt", 12, LocalDate.of(2023, 1, 1)),
            FileAge("src/New.kt", 1, LocalDate.of(2023, 12, 1)),
        )

        val result = CodeAgeFormatter.format(ages)

        val lines = result.lines()
        assertEquals(3, lines.size)
        assert(lines[0].contains("File"))
        assert(lines[0].contains("Age (months)"))
        assert(lines[0].contains("Last Changed"))
        assert(lines[1].contains("src/Old.kt"))
        assert(lines[1].contains("12"))
        assert(lines[1].contains("2023-01-01"))
        assert(lines[2].contains("src/New.kt"))
    }
}
