package no.f12.codenavigator.navigation

import no.f12.codenavigator.config.OutputFormat
import java.time.LocalDate

data class MetricsConfig(
    val after: LocalDate,
    val top: Int,
    val followRenames: Boolean,
    val rootPackage: PackageName,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): MetricsConfig = MetricsConfig(
            after = properties["after"]?.let { LocalDate.parse(it) }
                ?: LocalDate.now().minusYears(1),
            top = properties["top"]?.toIntOrNull() ?: 5,
            followRenames = !properties.containsKey("no-follow"),
            rootPackage = PackageName(properties["root-package"] ?: ""),
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
