package no.f12.codenavigator.navigation

object DsmFormatter {

    fun format(matrix: DsmMatrix): String {
        val packages = matrix.packages
        if (packages.isEmpty()) return "No inter-package dependencies found."

        return buildString {
            appendLine("=== Dependency Structure Matrix (DSM) ===")
            appendLine()
            appendLine("Legend:")
            packages.forEachIndexed { i, pkg ->
                appendLine("  ${(i + 1).toString().padStart(3)}: ${pkg.value}")
            }
            appendLine()

            appendLine("Reading: row depends on column. Cell value = number of dependency references.")
            appendLine("         Cells below the diagonal indicate forward dependencies (good).")
            appendLine("         Cells above the diagonal indicate backward/cyclic dependencies (review these).")
            appendLine()

            val colWidth = maxOf(packages.size.toString().length, 4)
            val labelWidth = packages.maxOf { it.value.length }.coerceAtLeast(10)

            append("".padEnd(labelWidth + 6))
            packages.forEachIndexed { i, _ ->
                append((i + 1).toString().padStart(colWidth))
            }
            appendLine()

            val totalWidth = labelWidth + 6 + packages.size * colWidth
            appendLine("-".repeat(totalWidth))

            packages.forEachIndexed { rowIdx, rowPkg ->
                append("${(rowIdx + 1).toString().padStart(3)}. ${rowPkg.value.padEnd(labelWidth)}")
                packages.forEachIndexed { colIdx, colPkg ->
                    val cell = when {
                        rowIdx == colIdx -> "."
                        else -> matrix.cells[rowPkg to colPkg]?.toString() ?: ""
                    }
                    append(cell.padStart(colWidth))
                }
                appendLine()
            }

            val cyclicPairs = matrix.findCyclicPairs()
            if (cyclicPairs.isNotEmpty()) {
                appendLine()
                appendLine("WARNING: Cyclic dependencies detected:")
                cyclicPairs.forEach { (a, b, counts) ->
                    appendLine("  ${a.value} <-> ${b.value}  (${counts.first} refs / ${counts.second} refs)")
                    val fwd = matrix.classDependencies[a to b]
                    val bwd = matrix.classDependencies[b to a]
                    fwd?.take(5)?.forEach { (src, tgt) -> appendLine("    ${a.value}.${src.value} -> ${b.value}.${tgt.value}") }
                    bwd?.take(5)?.forEach { (src, tgt) -> appendLine("    ${b.value}.${src.value} -> ${a.value}.${tgt.value}") }
                }
            }
        }.trimEnd()
    }

    fun formatCycles(matrix: DsmMatrix, cycleFilter: Pair<PackageName, PackageName>? = null): String {
        val cyclicPairs = matrix.findCyclicPairs(cycleFilter)
        if (cyclicPairs.isEmpty()) return "No cyclic dependencies found."

        return buildString {
            cyclicPairs.forEachIndexed { idx, (a, b, counts) ->
                if (idx > 0) appendLine()
                val fwdLabel = if (counts.first == 1) "ref" else "refs"
                val bwdLabel = if (counts.second == 1) "ref" else "refs"
                appendLine("CYCLE: ${a.value} <-> ${b.value} (${counts.first} $fwdLabel / ${counts.second} $bwdLabel)")
                appendLine("  ${a.value} -> ${b.value}:")
                val fwd = matrix.classDependencies[a to b]
                fwd?.sortedBy { "${it.first.value}-${it.second.value}" }?.forEach { (src, tgt) ->
                    appendLine("    ${a.value}.${src.value} -> ${b.value}.${tgt.value}")
                }
                appendLine("  ${b.value} -> ${a.value}:")
                val bwd = matrix.classDependencies[b to a]
                bwd?.sortedBy { "${it.first.value}-${it.second.value}" }?.forEach { (src, tgt) ->
                    appendLine("    ${b.value}.${src.value} -> ${a.value}.${tgt.value}")
                }
            }
        }.trimEnd()
    }
}
