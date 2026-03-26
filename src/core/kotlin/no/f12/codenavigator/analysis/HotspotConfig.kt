package no.f12.codenavigator.analysis

import no.f12.codenavigator.config.OutputFormat
import java.time.LocalDate

data class HotspotConfig(
    val after: LocalDate,
    val minRevs: Int,
    val top: Int,
    val followRenames: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): HotspotConfig = HotspotConfig(
            after = properties["after"]?.let { LocalDate.parse(it) }
                ?: LocalDate.now().minusYears(1),
            minRevs = properties["min-revs"]?.toIntOrNull() ?: 1,
            top = properties["top"]?.toIntOrNull() ?: 50,
            followRenames = !properties.containsKey("no-follow"),
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
