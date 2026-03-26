package no.f12.codenavigator.navigation

object CyclesFormatter {

    fun format(details: List<CycleDetail>): String {
        if (details.isEmpty()) return "No dependency cycles found."

        return details.joinToString("\n\n") { detail ->
            buildString {
                append("CYCLE: ${detail.packages.joinToString(", ") { it.value }}")
                for (edge in detail.edges) {
                    append("\n  ${edge.from.value} -> ${edge.to.value}:")
                    for ((src, tgt) in edge.classEdges.sortedBy { "${it.first.value}-${it.second.value}" }) {
                        append("\n    ${src.value} -> ${tgt.value}")
                    }
                }
            }
        }
    }
}
