package no.f12.codenavigator.analysis

object AuthorAnalysisFormatter {

    fun format(modules: List<ModuleAuthors>): String {
        if (modules.isEmpty()) return "No files found."

        val fileWidth = maxOf("File".length, modules.maxOf { it.file.length })
        val authorsWidth = maxOf("Authors".length, modules.maxOf { it.authors.toString().length })
        val revsWidth = maxOf("Revisions".length, modules.maxOf { it.revisions.toString().length })

        return buildString {
            appendLine("%-${fileWidth}s  %${authorsWidth}s  %${revsWidth}s".format("File", "Authors", "Revisions"))
            modules.forEachIndexed { index, m ->
                if (index > 0) appendLine()
                append("%-${fileWidth}s  %${authorsWidth}d  %${revsWidth}d".format(m.file, m.authors, m.revisions))
            }
        }
    }
}
