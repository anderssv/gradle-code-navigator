package no.f12.codenavigator.analysis

import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorAnalysisFormatterTest {

    @Test
    fun `formats empty list`() {
        val result = AuthorAnalysisFormatter.format(emptyList())

        assertEquals("No files found.", result)
    }

    @Test
    fun `formats module authors as table`() {
        val modules = listOf(
            ModuleAuthors("src/Team.kt", 5, 20),
            ModuleAuthors("src/Solo.kt", 1, 3),
        )

        val result = AuthorAnalysisFormatter.format(modules)

        val lines = result.lines()
        assertEquals(3, lines.size)
        assert(lines[0].contains("File"))
        assert(lines[0].contains("Authors"))
        assert(lines[0].contains("Revisions"))
        assert(lines[1].contains("src/Team.kt"))
        assert(lines[1].contains("5"))
        assert(lines[1].contains("20"))
        assert(lines[2].contains("src/Solo.kt"))
    }
}
