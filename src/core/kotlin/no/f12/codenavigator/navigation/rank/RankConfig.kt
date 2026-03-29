package no.f12.codenavigator.navigation.rank

import no.f12.codenavigator.ParamDef
import no.f12.codenavigator.TaskRegistry
import no.f12.codenavigator.config.OutputFormat

data class RankConfig(
    val top: Int,
    val projectOnly: Boolean,
    val collapseLambdas: Boolean,
    val prodOnly: Boolean,
    val testOnly: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): RankConfig = RankConfig(
            top = TaskRegistry.TOP.parse(properties["top"]),
            projectOnly = TaskRegistry.PROJECTONLY_ON.parse(properties["project-only"]),
            collapseLambdas = TaskRegistry.COLLAPSE_LAMBDAS.parse(properties["collapse-lambdas"]),
            prodOnly = TaskRegistry.PROD_ONLY.parse(properties["prod-only"]),
            testOnly = TaskRegistry.TEST_ONLY.parse(properties["test-only"]),
            format = ParamDef.parseFormat(properties),
        )
    }
}
