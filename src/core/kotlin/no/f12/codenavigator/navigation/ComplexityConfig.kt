package no.f12.codenavigator.navigation

import no.f12.codenavigator.config.OutputFormat

data class ComplexityConfig(
    val classPattern: String,
    val projectOnly: Boolean,
    val detail: Boolean,
    val collapseLambdas: Boolean,
    val top: Int,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): ComplexityConfig = ComplexityConfig(
            classPattern = properties["classname"] ?: ".*",
            projectOnly = properties["projectonly"]?.toBoolean() ?: true,
            detail = properties["detail"]?.toBoolean() ?: false,
            collapseLambdas = properties["collapse-lambdas"]?.toBoolean() ?: true,
            top = properties["top"]?.toIntOrNull() ?: 50,
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
