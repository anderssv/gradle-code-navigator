package no.f12.codenavigator.analysis

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeAgeBuilderTest {

    @Test
    fun `empty commits returns empty list`() {
        val result = CodeAgeBuilder.build(emptyList(), LocalDate.of(2024, 6, 1))

        assertEquals(emptyList(), result)
    }

    @Test
    fun `single file calculates age from last change to now`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(FileChange(10, 5, "src/Foo.kt")))
        )

        val result = CodeAgeBuilder.build(commits, LocalDate.of(2024, 7, 1))

        assertEquals(1, result.size)
        assertEquals("src/Foo.kt", result[0].file)
        assertEquals(LocalDate.of(2024, 1, 1), result[0].lastChangeDate)
        assertEquals(6, result[0].ageMonths)
    }

    @Test
    fun `multiple commits to same file uses most recent date`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(FileChange(1, 0, "src/Foo.kt"))),
            GitCommit("b", LocalDate.of(2024, 3, 15), "A", listOf(FileChange(1, 0, "src/Foo.kt"))),
            GitCommit("c", LocalDate.of(2024, 2, 1), "A", listOf(FileChange(1, 0, "src/Foo.kt"))),
        )

        val result = CodeAgeBuilder.build(commits, LocalDate.of(2024, 7, 1))

        assertEquals(1, result.size)
        assertEquals(LocalDate.of(2024, 3, 15), result[0].lastChangeDate)
        assertEquals(3, result[0].ageMonths)
    }

    @Test
    fun `results sorted by age descending oldest first`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(FileChange(1, 0, "src/Old.kt"))),
            GitCommit("b", LocalDate.of(2024, 5, 1), "A", listOf(FileChange(1, 0, "src/New.kt"))),
            GitCommit("c", LocalDate.of(2024, 3, 1), "A", listOf(FileChange(1, 0, "src/Mid.kt"))),
        )

        val result = CodeAgeBuilder.build(commits, LocalDate.of(2024, 7, 1))

        assertEquals("src/Old.kt", result[0].file)
        assertEquals("src/Mid.kt", result[1].file)
        assertEquals("src/New.kt", result[2].file)
    }

    @Test
    fun `top parameter limits results`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(
                FileChange(1, 0, "src/A.kt"),
                FileChange(1, 0, "src/B.kt"),
                FileChange(1, 0, "src/C.kt"),
            )),
        )

        val result = CodeAgeBuilder.build(commits, LocalDate.of(2024, 7, 1), top = 2)

        assertEquals(2, result.size)
    }
}
