package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthorAnalysisBuilderTest {

    @Test
    fun `empty commits returns empty list`() {
        val result = AuthorAnalysisBuilder.build(emptyList())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `single commit single file single author`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(FileChange(10, 5, "src/Foo.kt")))
        )

        val result = AuthorAnalysisBuilder.build(commits)

        assertEquals(1, result.size)
        assertEquals(ModuleAuthors("src/Foo.kt", authors = 1, revisions = 1), result[0])
    }

    @Test
    fun `multiple authors on same file counts distinct authors`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(FileChange(1, 0, "src/Foo.kt"))),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Bob", listOf(FileChange(1, 0, "src/Foo.kt"))),
            GitCommit("c", LocalDate.of(2024, 1, 3), "Alice", listOf(FileChange(1, 0, "src/Foo.kt"))),
        )

        val result = AuthorAnalysisBuilder.build(commits)

        assertEquals(1, result.size)
        assertEquals(ModuleAuthors("src/Foo.kt", authors = 2, revisions = 3), result[0])
    }

    @Test
    fun `sorted by author count descending`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(
                FileChange(1, 0, "src/Solo.kt"),
                FileChange(1, 0, "src/Team.kt"),
            )),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Bob", listOf(FileChange(1, 0, "src/Team.kt"))),
            GitCommit("c", LocalDate.of(2024, 1, 3), "Carol", listOf(FileChange(1, 0, "src/Team.kt"))),
        )

        val result = AuthorAnalysisBuilder.build(commits)

        assertEquals("src/Team.kt", result[0].file)
        assertEquals(3, result[0].authors)
        assertEquals("src/Solo.kt", result[1].file)
        assertEquals(1, result[1].authors)
    }

    @Test
    fun `min-revs filters out low-revision files`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Alice", listOf(
                FileChange(1, 0, "src/Rare.kt"),
                FileChange(1, 0, "src/Common.kt"),
            )),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Bob", listOf(FileChange(1, 0, "src/Common.kt"))),
        )

        val result = AuthorAnalysisBuilder.build(commits, minRevs = 2)

        assertEquals(1, result.size)
        assertEquals("src/Common.kt", result[0].file)
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

        val result = AuthorAnalysisBuilder.build(commits, top = 2)

        assertEquals(2, result.size)
    }
}
