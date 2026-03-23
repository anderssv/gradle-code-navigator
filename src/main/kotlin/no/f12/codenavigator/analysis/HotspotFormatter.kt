package no.f12.codenavigator.analysis

object HotspotFormatter {

    fun format(hotspots: List<Hotspot>): String {
        if (hotspots.isEmpty()) return "No hotspots found."

        val fileWidth = maxOf("File".length, hotspots.maxOf { it.file.length })
        val revWidth = maxOf("Revisions".length, hotspots.maxOf { it.revisions.toString().length })
        val churnWidth = maxOf("Churn".length, hotspots.maxOf { it.totalChurn.toString().length })

        return buildString {
            appendLine("%-${fileWidth}s  %${revWidth}s  %${churnWidth}s".format("File", "Revisions", "Churn"))
            hotspots.forEachIndexed { index, h ->
                if (index > 0) appendLine()
                append("%-${fileWidth}s  %${revWidth}d  %${churnWidth}d".format(h.file, h.revisions, h.totalChurn))
            }
        }
    }
}
