package no.f12.codenavigator

object LlmFormatter {

    fun formatClasses(classes: List<ClassInfo>): String =
        classes.sortedBy { it.className }.joinToString("\n") { "${it.className} ${it.sourceFileName}" }

    fun formatSymbols(symbols: List<SymbolInfo>): String =
        symbols.sortedWith(compareBy({ it.packageName }, { it.className }, { it.symbolName }))
            .joinToString("\n") { "${it.packageName}.${it.className}.${it.symbolName} ${it.kind.name.lowercase()} ${it.sourceFile}" }

    fun formatClassDetails(details: List<ClassDetail>): String =
        details.sortedBy { it.className }.joinToString("\n") { d ->
            buildString {
                append("${d.className} ${d.sourceFile}")
                if (d.superClass != null) append(" extends:${d.superClass}")
                if (d.interfaces.isNotEmpty()) append(" implements:${d.interfaces.joinToString(",")}")
                if (d.fields.isNotEmpty()) append(" fields:${d.fields.joinToString(",") { "${it.name}:${it.type}" }}")
                if (d.methods.isNotEmpty()) append(" methods:${d.methods.joinToString(",") { "${it.name}(${it.parameterTypes.joinToString(",")}):${it.returnType}" }}")
            }
        }

    fun formatInterfaces(registry: InterfaceRegistry, interfaceNames: List<String>): String =
        interfaceNames.sorted().joinToString("\n") { name ->
            val impls = registry.implementorsOf(name).sortedBy { it.className }
            "$name: ${impls.joinToString(",") { "${it.className}(${it.sourceFile})" }}"
        }

    fun renderCallTrees(trees: List<CallTreeNode>, direction: CallDirection): String = buildString {
        trees.forEachIndexed { index, tree ->
            if (index > 0) appendLine()
            append("${tree.method.qualifiedName} ${tree.sourceFile ?: "<unknown>"}")
            if (tree.children.isNotEmpty()) {
                renderChildren(tree.children, direction, 1)
            }
        }
    }.trimEnd()

    fun formatPackageDeps(deps: PackageDependencies, packageNames: List<String>, reverse: Boolean): String {
        val arrow = if (reverse) "<-" else "->"
        return packageNames.sorted().joinToString("\n") { pkg ->
            val related = if (reverse) deps.dependentsOf(pkg) else deps.dependenciesOf(pkg)
            "$pkg $arrow ${related.joinToString(",")}"
        }
    }

    private fun StringBuilder.renderChildren(children: List<CallTreeNode>, direction: CallDirection, depth: Int) {
        val indent = "  ".repeat(depth)
        for (node in children) {
            appendLine()
            append("$indent${direction.arrow} ${node.method.qualifiedName} ${node.sourceFile ?: "<unknown>"}")
            if (node.children.isNotEmpty()) {
                renderChildren(node.children, direction, depth + 1)
            }
        }
    }
}
