package no.f12.codenavigator.analysis

object CodeAgeFormatter {

    fun format(ages: List<FileAge>): String {
        if (ages.isEmpty()) return "No files found."

        val fileWidth = maxOf("File".length, ages.maxOf { it.file.length })
        val ageWidth = maxOf("Age (months)".length, ages.maxOf { it.ageMonths.toString().length })

        return buildString {
            appendLine("%-${fileWidth}s  %${ageWidth}s  Last Changed".format("File", "Age (months)"))
            ages.forEachIndexed { index, a ->
                if (index > 0) appendLine()
                append("%-${fileWidth}s  %${ageWidth}d  %s".format(a.file, a.ageMonths, a.lastChangeDate))
            }
        }
    }
}
