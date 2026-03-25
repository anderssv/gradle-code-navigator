package no.f12.codenavigator.navigation

import no.f12.codenavigator.OutputFormat

data class ComplexityConfig(
    val classPattern: String,
    val projectOnly: Boolean,
    val detail: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): ComplexityConfig = ComplexityConfig(
            classPattern = properties["class"]
                ?: throw IllegalArgumentException("Missing required property 'class'"),
            projectOnly = properties["projectonly"]?.toBoolean() ?: true,
            detail = properties["detail"]?.toBoolean() ?: false,
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
