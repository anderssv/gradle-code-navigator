package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeCouplingBuilderTest {

    @Test
    fun `empty commits returns empty list`() {
        val result = ChangeCouplingBuilder.build(emptyList())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `single file change has no coupling`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(FileChange(1, 0, "src/Foo.kt")))
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 1)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `two files changing in same commit creates coupling`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(
                FileChange(1, 0, "src/Foo.kt"),
                FileChange(1, 0, "src/Bar.kt"),
            ))
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 1)

        assertEquals(1, result.size)
        assertEquals("src/Bar.kt", result[0].entity)
        assertEquals("src/Foo.kt", result[0].coupled)
        assertEquals(1, result[0].sharedRevs)
        assertEquals(100, result[0].degree)
    }

    @Test
    fun `degree calculation shared divided by avg individual times 100`() {
        // Foo changes in 4 commits, Bar changes in 2 commits, they share 2 commits
        // avgRevs = (4+2)/2 = 3, degree = 2/3*100 = 66
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(
                FileChange(1, 0, "src/Bar.kt"),
                FileChange(1, 0, "src/Foo.kt"),
            )),
            GitCommit("b", LocalDate.of(2024, 1, 2), "A", listOf(
                FileChange(1, 0, "src/Bar.kt"),
                FileChange(1, 0, "src/Foo.kt"),
            )),
            GitCommit("c", LocalDate.of(2024, 1, 3), "A", listOf(FileChange(1, 0, "src/Foo.kt"))),
            GitCommit("d", LocalDate.of(2024, 1, 4), "A", listOf(FileChange(1, 0, "src/Foo.kt"))),
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 1)

        assertEquals(1, result.size)
        assertEquals(2, result[0].sharedRevs)
        assertEquals(3, result[0].avgRevs)
        assertEquals(66, result[0].degree) // integer division: 200/3 = 66
    }

    @Test
    fun `min shared revisions filters low coupling`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(
                FileChange(1, 0, "src/Foo.kt"),
                FileChange(1, 0, "src/Bar.kt"),
            )),
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 2, minCoupling = 1)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `min coupling percentage filters weak coupling`() {
        // Foo changes 10 times, Bar changes 2 times, they share 1 commit
        // avgRevs = (10+2)/2 = 6, degree = 1/6*100 = 16
        val commits = mutableListOf<GitCommit>()
        commits.add(GitCommit("shared", LocalDate.of(2024, 1, 1), "A", listOf(
            FileChange(1, 0, "src/Foo.kt"),
            FileChange(1, 0, "src/Bar.kt"),
        )))
        commits.add(GitCommit("bar2", LocalDate.of(2024, 1, 2), "A", listOf(FileChange(1, 0, "src/Bar.kt"))))
        for (i in 2..9) {
            commits.add(GitCommit("foo$i", LocalDate.of(2024, 1, i + 1), "A", listOf(FileChange(1, 0, "src/Foo.kt"))))
        }

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 30)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `max changeset size filters large commits`() {
        val manyFiles = (1..31).map { FileChange(1, 0, "src/File$it.kt") }
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", manyFiles)
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 1, maxChangesetSize = 30)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `results sorted by degree descending`() {
        // Create two pairs: one with 100% coupling and one with ~50%
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(
                FileChange(1, 0, "src/A.kt"),
                FileChange(1, 0, "src/B.kt"),
                FileChange(1, 0, "src/C.kt"),
            )),
            GitCommit("b", LocalDate.of(2024, 1, 2), "A", listOf(
                FileChange(1, 0, "src/A.kt"),
                FileChange(1, 0, "src/B.kt"),
            )),
            // C only appears once, so A-C and B-C have degree 50 (1 shared / avg 1.5 = 66 for A-C)
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 1)

        assert(result.size >= 2) { "Expected at least 2 pairs, got ${result.size}" }
        assert(result[0].degree >= result[1].degree) { "Expected sorted by degree descending" }
    }

    @Test
    fun `top limits number of results`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "A", listOf(
                FileChange(1, 0, "src/A.kt"),
                FileChange(1, 0, "src/B.kt"),
                FileChange(1, 0, "src/C.kt"),
                FileChange(1, 0, "src/D.kt"),
            )),
        )

        val result = ChangeCouplingBuilder.build(commits, minSharedRevs = 1, minCoupling = 1, top = 2)

        assertEquals(2, result.size)
    }
}
