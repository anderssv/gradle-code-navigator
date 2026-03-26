package no.f12.codenavigator.analysis

import no.f12.codenavigator.config.OutputFormat
import java.time.LocalDate

data class ChangeCouplingConfig(
    val after: LocalDate,
    val minSharedRevs: Int,
    val minCoupling: Int,
    val maxChangesetSize: Int,
    val followRenames: Boolean,
    val top: Int,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): ChangeCouplingConfig = ChangeCouplingConfig(
            after = properties["after"]?.let { LocalDate.parse(it) }
                ?: LocalDate.now().minusYears(1),
            minSharedRevs = properties["min-shared-revs"]?.toIntOrNull() ?: 5,
            minCoupling = properties["min-coupling"]?.toIntOrNull() ?: 30,
            maxChangesetSize = properties["max-changeset-size"]?.toIntOrNull() ?: 30,
            followRenames = !properties.containsKey("no-follow"),
            top = properties["top"]?.toIntOrNull() ?: 50,
            format = OutputFormat.from(
                format = properties["format"],
                llm = properties["llm"]?.toBoolean(),
            ),
        )
    }
}
