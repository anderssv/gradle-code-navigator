package no.f12.codenavigator.analysis

data class Hotspot(
    val file: String,
    val revisions: Int,
    val totalChurn: Int,
)

object HotspotBuilder {

    fun build(
        commits: List<GitCommit>,
        minRevs: Int = 1,
        top: Int = 50,
    ): List<Hotspot> {
        val fileStats = mutableMapOf<String, MutablePair>()

        for (commit in commits) {
            for (file in commit.files) {
                val stats = fileStats.getOrPut(file.path) { MutablePair(0, 0) }
                stats.revisions++
                stats.churn += file.added + file.deleted
            }
        }

        return fileStats
            .filter { (_, stats) -> stats.revisions >= minRevs }
            .map { (file, stats) -> Hotspot(file, stats.revisions, stats.churn) }
            .sortedByDescending { it.revisions }
            .take(top)
    }

    private class MutablePair(var revisions: Int, var churn: Int)
}
