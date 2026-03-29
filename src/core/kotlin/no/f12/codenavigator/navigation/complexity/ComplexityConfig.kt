package no.f12.codenavigator.navigation.complexity

import no.f12.codenavigator.ParamDef
import no.f12.codenavigator.TaskRegistry
import no.f12.codenavigator.config.OutputFormat

data class ComplexityConfig(
    val classPattern: String,
    val projectOnly: Boolean,
    val detail: Boolean,
    val collapseLambdas: Boolean,
    val top: Int,
    val prodOnly: Boolean,
    val testOnly: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): ComplexityConfig = ComplexityConfig(
            classPattern = properties["pattern"] ?: ".*",
            projectOnly = TaskRegistry.PROJECTONLY_ON.parse(properties["project-only"]),
            detail = TaskRegistry.DETAIL.parse(properties["detail"]),
            collapseLambdas = TaskRegistry.COLLAPSE_LAMBDAS.parse(properties["collapse-lambdas"]),
            top = TaskRegistry.TOP.parse(properties["top"]),
            prodOnly = TaskRegistry.PROD_ONLY.parse(properties["prod-only"]),
            testOnly = TaskRegistry.TEST_ONLY.parse(properties["test-only"]),
            format = ParamDef.parseFormat(properties),
        )
    }
}
