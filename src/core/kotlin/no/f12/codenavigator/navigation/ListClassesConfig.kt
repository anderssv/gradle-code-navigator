package no.f12.codenavigator.navigation

import no.f12.codenavigator.OutputFormat

data class ListClassesConfig(
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): ListClassesConfig = ListClassesConfig(
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
