package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class GitLogParserTest {

    @Test
    fun `parse empty input returns empty list`() {
        val result = GitLogParser.parse("")

        assertEquals(emptyList(), result)
    }

    @Test
    fun `parse single commit with no file changes`() {
        val input = "--abc123--2024-01-15--Anders Sveen"

        val result = GitLogParser.parse(input)

        assertEquals(1, result.size)
        assertEquals("abc123", result[0].hash)
        assertEquals(LocalDate.of(2024, 1, 15), result[0].date)
        assertEquals("Anders Sveen", result[0].author)
        assertEquals(emptyList(), result[0].files)
    }

    @Test
    fun `parse single commit with one file change`() {
        val input = """
            --abc123--2024-01-15--Anders Sveen
            10	5	src/main/Foo.kt
        """.trimIndent()

        val result = GitLogParser.parse(input)

        assertEquals(1, result.size)
        assertEquals(1, result[0].files.size)
        assertEquals(FileChange(10, 5, "src/main/Foo.kt"), result[0].files[0])
    }

    @Test
    fun `parse single commit with multiple file changes`() {
        val input = """
            --abc123--2024-01-15--Anders Sveen
            10	5	src/main/Foo.kt
            3	0	src/main/Bar.kt
            0	20	src/test/OldTest.kt
        """.trimIndent()

        val result = GitLogParser.parse(input)

        assertEquals(1, result.size)
        assertEquals(3, result[0].files.size)
        assertEquals(FileChange(10, 5, "src/main/Foo.kt"), result[0].files[0])
        assertEquals(FileChange(3, 0, "src/main/Bar.kt"), result[0].files[1])
        assertEquals(FileChange(0, 20, "src/test/OldTest.kt"), result[0].files[2])
    }

    @Test
    fun `parse multiple commits`() {
        val input = """
            --abc123--2024-01-15--Anders Sveen
            10	5	src/main/Foo.kt
            --def456--2024-01-16--Jane Doe
            3	0	src/main/Bar.kt
        """.trimIndent()

        val result = GitLogParser.parse(input)

        assertEquals(2, result.size)
        assertEquals("abc123", result[0].hash)
        assertEquals("Anders Sveen", result[0].author)
        assertEquals(1, result[0].files.size)
        assertEquals("def456", result[1].hash)
        assertEquals("Jane Doe", result[1].author)
        assertEquals(1, result[1].files.size)
    }

    @Test
    fun `parse handles binary file changes`() {
        val input = """
            --abc123--2024-01-15--Anders Sveen
            -	-	images/logo.png
            10	5	src/main/Foo.kt
        """.trimIndent()

        val result = GitLogParser.parse(input)

        assertEquals(1, result.size)
        assertEquals(2, result[0].files.size)
        assertEquals(FileChange(0, 0, "images/logo.png"), result[0].files[0])
        assertEquals(FileChange(10, 5, "src/main/Foo.kt"), result[0].files[1])
    }

    @Test
    fun `parse skips blank lines between commits`() {
        val input = "--abc123--2024-01-15--Anders Sveen\n10\t5\tsrc/Foo.kt\n\n--def456--2024-01-16--Jane Doe\n3\t0\tsrc/Bar.kt"

        val result = GitLogParser.parse(input)

        assertEquals(2, result.size)
        assertEquals("abc123", result[0].hash)
        assertEquals("def456", result[1].hash)
    }

    @Test
    fun `parse handles author names with spaces`() {
        val input = "--abc123--2024-01-15--John Michael Smith Jr"

        val result = GitLogParser.parse(input)

        assertEquals(1, result.size)
        assertEquals("John Michael Smith Jr", result[0].author)
    }
}
