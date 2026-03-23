package no.f12.codenavigator.analysis

import no.f12.codenavigator.OutputFormat
import java.time.LocalDate

data class CodeAgeConfig(
    val after: LocalDate,
    val top: Int,
    val followRenames: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): CodeAgeConfig = CodeAgeConfig(
            after = properties["after"]?.let { LocalDate.parse(it) }
                ?: LocalDate.now().minusYears(1),
            top = properties["top"]?.toIntOrNull() ?: 50,
            followRenames = !properties.containsKey("no-follow"),
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
