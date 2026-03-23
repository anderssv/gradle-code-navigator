package no.f12.codenavigator.analysis

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class HotspotBuilderTest {

    @Test
    fun `empty commits returns empty list`() {
        val result = HotspotBuilder.build(emptyList())

        assertEquals(emptyList(), result)
    }

    @Test
    fun `single commit with one file returns one hotspot`() {
        val commits = listOf(
            GitCommit("abc", LocalDate.of(2024, 1, 1), "Author", listOf(FileChange(10, 5, "src/Foo.kt")))
        )

        val result = HotspotBuilder.build(commits)

        assertEquals(1, result.size)
        assertEquals(Hotspot("src/Foo.kt", revisions = 1, totalChurn = 15), result[0])
    }

    @Test
    fun `multiple commits touching same file aggregates revisions and churn`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(FileChange(10, 5, "src/Foo.kt"))),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Author", listOf(FileChange(3, 2, "src/Foo.kt"))),
            GitCommit("c", LocalDate.of(2024, 1, 3), "Author", listOf(FileChange(1, 0, "src/Foo.kt"))),
        )

        val result = HotspotBuilder.build(commits)

        assertEquals(1, result.size)
        assertEquals(Hotspot("src/Foo.kt", revisions = 3, totalChurn = 21), result[0])
    }

    @Test
    fun `results are sorted by revision count descending`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(
                FileChange(1, 0, "src/Rarely.kt"),
                FileChange(1, 0, "src/Often.kt"),
            )),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Author", listOf(FileChange(1, 0, "src/Often.kt"))),
            GitCommit("c", LocalDate.of(2024, 1, 3), "Author", listOf(FileChange(1, 0, "src/Often.kt"))),
        )

        val result = HotspotBuilder.build(commits)

        assertEquals(2, result.size)
        assertEquals("src/Often.kt", result[0].file)
        assertEquals(3, result[0].revisions)
        assertEquals("src/Rarely.kt", result[1].file)
        assertEquals(1, result[1].revisions)
    }

    @Test
    fun `top parameter limits results`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(
                FileChange(1, 0, "src/A.kt"),
                FileChange(1, 0, "src/B.kt"),
                FileChange(1, 0, "src/C.kt"),
            )),
        )

        val result = HotspotBuilder.build(commits, top = 2)

        assertEquals(2, result.size)
    }

    @Test
    fun `min-revs parameter filters out low-revision files`() {
        val commits = listOf(
            GitCommit("a", LocalDate.of(2024, 1, 1), "Author", listOf(
                FileChange(1, 0, "src/Rare.kt"),
                FileChange(1, 0, "src/Common.kt"),
            )),
            GitCommit("b", LocalDate.of(2024, 1, 2), "Author", listOf(
                FileChange(1, 0, "src/Common.kt"),
            )),
        )

        val result = HotspotBuilder.build(commits, minRevs = 2)

        assertEquals(1, result.size)
        assertEquals("src/Common.kt", result[0].file)
    }
}
