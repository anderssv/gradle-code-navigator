package no.f12.codenavigator.analysis

object ChurnFormatter {

    fun format(churn: List<FileChurn>): String {
        if (churn.isEmpty()) return "No churn data found."

        val fileWidth = maxOf("File".length, churn.maxOf { it.file.length })
        val addedWidth = maxOf("Added".length, churn.maxOf { it.added.toString().length })
        val deletedWidth = maxOf("Deleted".length, churn.maxOf { it.deleted.toString().length })
        val netWidth = maxOf("Net".length, churn.maxOf { (it.added - it.deleted).toString().length })
        val commitsWidth = maxOf("Commits".length, churn.maxOf { it.commits.toString().length })

        return buildString {
            appendLine(
                "%-${fileWidth}s  %${addedWidth}s  %${deletedWidth}s  %${netWidth}s  %${commitsWidth}s".format(
                    "File", "Added", "Deleted", "Net", "Commits",
                ),
            )
            churn.forEachIndexed { index, c ->
                if (index > 0) appendLine()
                append(
                    "%-${fileWidth}s  %${addedWidth}d  %${deletedWidth}d  %${netWidth}d  %${commitsWidth}d".format(
                        c.file, c.added, c.deleted, c.added - c.deleted, c.commits,
                    ),
                )
            }
        }
    }
}
