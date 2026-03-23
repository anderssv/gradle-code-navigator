package no.f12.codenavigator.analysis

data class ModuleAuthors(
    val file: String,
    val authors: Int,
    val revisions: Int,
)

object AuthorAnalysisBuilder {

    fun build(
        commits: List<GitCommit>,
        minRevs: Int = 1,
        top: Int = 50,
    ): List<ModuleAuthors> {
        val fileAuthors = mutableMapOf<String, MutableSet<String>>()
        val fileRevisions = mutableMapOf<String, Int>()

        for (commit in commits) {
            for (file in commit.files) {
                fileAuthors.getOrPut(file.path) { mutableSetOf() }.add(commit.author)
                fileRevisions[file.path] = (fileRevisions[file.path] ?: 0) + 1
            }
        }

        return fileAuthors
            .filter { (file, _) -> (fileRevisions[file] ?: 0) >= minRevs }
            .map { (file, authors) -> ModuleAuthors(file, authors.size, fileRevisions[file] ?: 0) }
            .sortedByDescending { it.authors }
            .take(top)
    }
}
