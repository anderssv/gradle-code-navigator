package no.f12.codenavigator.navigation

import no.f12.codenavigator.ParamDef
import no.f12.codenavigator.TaskRegistry
import no.f12.codenavigator.config.OutputFormat

data class DeadCodeConfig(
    val filter: Regex?,
    val exclude: Regex?,
    val classesOnly: Boolean,
    val excludeAnnotated: List<String>,
    val prodOnly: Boolean,
    val format: OutputFormat,
) {
    companion object {
        fun parse(properties: Map<String, String?>): DeadCodeConfig = DeadCodeConfig(
            filter = properties["filter"]?.let { Regex(it, RegexOption.IGNORE_CASE) },
            exclude = properties["exclude"]?.let { Regex(it, RegexOption.IGNORE_CASE) },
            classesOnly = TaskRegistry.CLASSES_ONLY.parse(properties["classes-only"]),
            excludeAnnotated = TaskRegistry.EXCLUDE_ANNOTATED.parse(properties["exclude-annotated"]),
            prodOnly = TaskRegistry.PROD_ONLY.parse(properties["prod-only"]),
            format = ParamDef.parseFormat(properties),
        )
    }
}
