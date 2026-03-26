package no.f12.codenavigator.navigation

data class PackageDependency(
    val sourcePackage: PackageName,
    val targetPackage: PackageName,
    val sourceClass: ClassName,
    val targetClass: ClassName,
)

data class DsmMatrix(
    val packages: List<PackageName>,
    val cells: Map<Pair<PackageName, PackageName>, Int>,
    val classDependencies: Map<Pair<PackageName, PackageName>, Set<Pair<ClassName, ClassName>>>,
) {
    fun findCyclicPairs(cycleFilter: Pair<PackageName, PackageName>? = null): List<Triple<PackageName, PackageName, Pair<Int, Int>>> {
        val allPairs = packages.flatMapIndexed { rowIdx, rowPkg ->
            packages.mapIndexedNotNull { colIdx, colPkg ->
                if (colIdx > rowIdx &&
                    cells.containsKey(rowPkg to colPkg) &&
                    cells.containsKey(colPkg to rowPkg)
                ) {
                    Triple(rowPkg, colPkg, cells[rowPkg to colPkg]!! to cells[colPkg to rowPkg]!!)
                } else {
                    null
                }
            }
        }
        if (cycleFilter == null) return allPairs
        return allPairs.filter { (a, b, _) ->
            (a == cycleFilter.first && b == cycleFilter.second) ||
                (a == cycleFilter.second && b == cycleFilter.first)
        }
    }
}

object DsmMatrixBuilder {

    fun build(dependencies: List<PackageDependency>, rootPrefix: String, depth: Int): DsmMatrix {
        if (dependencies.isEmpty()) return DsmMatrix(emptyList(), emptyMap(), emptyMap())

        val allTruncated = dependencies.map { dep ->
            Triple(truncate(dep.sourcePackage.value, rootPrefix, depth),
                   truncate(dep.targetPackage.value, rootPrefix, depth),
                   dep)
        }

        val crossPackage = allTruncated.filter { (src, tgt, _) -> src != tgt }

        val cells = mutableMapOf<Pair<PackageName, PackageName>, Int>()
        val classDeps = mutableMapOf<Pair<PackageName, PackageName>, MutableSet<Pair<ClassName, ClassName>>>()

        for ((src, tgt, dep) in crossPackage) {
            val key = PackageName(src) to PackageName(tgt)
            cells[key] = (cells[key] ?: 0) + 1
            classDeps.getOrPut(key) { mutableSetOf() }.add(dep.sourceClass to dep.targetClass)
        }

        val packages = (crossPackage.flatMap { listOf(it.first, it.second) }).distinct().sorted().map { PackageName(it) }

        return DsmMatrix(packages, cells, classDeps)
    }

    private fun truncate(pkg: String, rootPrefix: String, depth: Int): String {
        val stripped = if (rootPrefix.isNotEmpty() && pkg.startsWith("$rootPrefix.")) {
            pkg.removePrefix("$rootPrefix.")
        } else {
            pkg
        }
        val segments = stripped.split(".")
        return segments.take(depth).joinToString(".")
    }
}
