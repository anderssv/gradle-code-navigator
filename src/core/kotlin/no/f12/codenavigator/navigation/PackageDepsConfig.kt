package no.f12.codenavigator.navigation

import no.f12.codenavigator.config.OutputFormat

data class PackageDepsConfig(
    val packagePattern: String?,
    val projectOnly: Boolean,
    val reverse: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): PackageDepsConfig = PackageDepsConfig(
            packagePattern = properties["package"],
            projectOnly = properties["projectonly"]?.toBoolean() ?: false,
            reverse = properties["reverse"]?.toBoolean() ?: false,
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
