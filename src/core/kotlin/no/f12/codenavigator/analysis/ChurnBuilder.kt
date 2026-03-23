package no.f12.codenavigator.analysis

data class FileChurn(
    val file: String,
    val added: Int,
    val deleted: Int,
    val commits: Int,
)

object ChurnBuilder {

    fun build(
        commits: List<GitCommit>,
        top: Int = 50,
    ): List<FileChurn> {
        val fileStats = mutableMapOf<String, MutableStats>()

        for (commit in commits) {
            for (file in commit.files) {
                val stats = fileStats.getOrPut(file.path) { MutableStats() }
                stats.added += file.added
                stats.deleted += file.deleted
                stats.commits++
            }
        }

        return fileStats
            .map { (file, stats) -> FileChurn(file, stats.added, stats.deleted, stats.commits) }
            .sortedByDescending { it.added + it.deleted }
            .take(top)
    }

    private class MutableStats(var added: Int = 0, var deleted: Int = 0, var commits: Int = 0)
}
