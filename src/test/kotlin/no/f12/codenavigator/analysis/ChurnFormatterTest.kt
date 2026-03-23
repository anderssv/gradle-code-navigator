package no.f12.codenavigator.analysis

import kotlin.test.Test
import kotlin.test.assertEquals

class ChurnFormatterTest {

    @Test
    fun `formats empty list`() {
        val result = ChurnFormatter.format(emptyList())

        assertEquals("No churn data found.", result)
    }

    @Test
    fun `formats single file churn as table with header`() {
        val churn = listOf(FileChurn("src/main/Foo.kt", 100, 50, 10))

        val result = ChurnFormatter.format(churn)

        val lines = result.lines()
        assertEquals(2, lines.size)
        assert(lines[0].contains("File")) { "Header should contain 'File'" }
        assert(lines[0].contains("Added")) { "Header should contain 'Added'" }
        assert(lines[0].contains("Deleted")) { "Header should contain 'Deleted'" }
        assert(lines[0].contains("Net")) { "Header should contain 'Net'" }
        assert(lines[0].contains("Commits")) { "Header should contain 'Commits'" }
        assert(lines[1].contains("src/main/Foo.kt")) { "Row should contain file path" }
        assert(lines[1].contains("100")) { "Row should contain added count" }
        assert(lines[1].contains("50")) { "Row should contain deleted count" }
        assert(lines[1].contains("10")) { "Row should contain commit count" }
    }

    @Test
    fun `formats multiple files with aligned columns`() {
        val churn = listOf(
            FileChurn("src/main/BigFile.kt", 500, 200, 25),
            FileChurn("src/main/Foo.kt", 10, 5, 3),
            FileChurn("src/test/BarTest.kt", 3, 1, 1),
        )

        val result = ChurnFormatter.format(churn)

        val lines = result.lines()
        assertEquals(4, lines.size)
        assert(lines[0].contains("File"))
        assert(lines[1].contains("src/main/BigFile.kt"))
        assert(lines[2].contains("src/main/Foo.kt"))
        assert(lines[3].contains("src/test/BarTest.kt"))
    }
}
