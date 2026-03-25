package no.f12.codenavigator.navigation

object InterfaceFormatter {

    fun format(registry: InterfaceRegistry, interfaceNames: List<ClassName>): String = buildString {
        interfaceNames.forEachIndexed { index, ifaceName ->
            if (index > 0) appendLine()
            val implementors = registry.implementorsOf(ifaceName)
            appendLine("=== ${ifaceName.value} (${implementors.size} implementors) ===")
            implementors.forEach { impl ->
                appendLine("  ${impl.className.value} (${impl.sourceFile})")
            }
        }
    }.trimEnd()
}
