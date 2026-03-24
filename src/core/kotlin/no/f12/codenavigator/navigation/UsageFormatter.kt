package no.f12.codenavigator.navigation

object UsageFormatter {
    fun format(usages: List<UsageSite>): String {
        if (usages.isEmpty()) return "No usages found."

        return usages
            .sortedWith(compareBy({ it.callerClass }, { it.callerMethod }))
            .joinToString("\n") { u ->
                "${u.callerClass}.${u.callerMethod} → ${u.targetOwner}.${u.targetName} (${u.sourceFile}) [${u.kind.name.lowercase()}]"
            }
    }

    fun noResultsGuidance(ownerClass: String?, method: String?, type: String?): String {
        val target = buildString {
            if (ownerClass != null) {
                append(ownerClass)
                if (method != null) append(".$method")
            } else {
                append(type)
            }
        }
        return buildString {
            appendLine("No usages found for '$target'.")
            appendLine("Hint: Ensure the value is a fully-qualified class name (e.g., com.example.MyClass).")
            if (ownerClass != null) {
                appendLine("Hint: Try -Ptype=$ownerClass to also search type references, casts, and signatures.")
            }
        }.trimEnd()
    }
}
