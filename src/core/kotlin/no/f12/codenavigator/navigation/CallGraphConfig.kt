package no.f12.codenavigator.navigation

import no.f12.codenavigator.OutputFormat

data class CallGraphConfig(
    val method: String,
    val maxDepth: Int,
    val projectOnly: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): CallGraphConfig = CallGraphConfig(
            method = properties["method"]
                ?: throw IllegalArgumentException("Missing required property 'method'"),
            maxDepth = properties["maxdepth"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Missing required property 'maxdepth'"),
            projectOnly = properties["projectonly"]?.toBoolean() ?: false,
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
