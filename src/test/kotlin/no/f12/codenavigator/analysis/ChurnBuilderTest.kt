package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ChurnBuilderTest {

    @Test
    fun `empty commits returns empty list`() {
        val result = ChurnBuilder.build(emptyList())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `single commit single file tracks added and deleted`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(FileChange(10, 5, "src/Foo.kt")))
        )

        val result = ChurnBuilder.build(commits)

        assertEquals(1, result.size)
        assertEquals(FileChurn("src/Foo.kt", added = 10, deleted = 5, commits = 1), result[0])
    }

    @Test
    fun `multiple commits to same file aggregates churn`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(FileChange(10, 5, "src/Foo.kt"))),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Bob", listOf(FileChange(3, 2, "src/Foo.kt"))),
        )

        val result = ChurnBuilder.build(commits)

        assertEquals(1, result.size)
        assertEquals(FileChurn("src/Foo.kt", added = 13, deleted = 7, commits = 2), result[0])
    }

    @Test
    fun `sorted by total churn descending`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(
                FileChange(1, 0, "src/Small.kt"),
                FileChange(100, 50, "src/Big.kt"),
                FileChange(10, 10, "src/Mid.kt"),
            )),
        )

        val result = ChurnBuilder.build(commits)

        assertEquals("src/Big.kt", result[0].file)
        assertEquals("src/Mid.kt", result[1].file)
        assertEquals("src/Small.kt", result[2].file)
    }

    @Test
    fun `top parameter limits results`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(
                FileChange(1, 0, "src/A.kt"),
                FileChange(1, 0, "src/B.kt"),
                FileChange(1, 0, "src/C.kt"),
            )),
        )

        val result = ChurnBuilder.build(commits, top = 2)

        assertEquals(2, result.size)
    }
}
