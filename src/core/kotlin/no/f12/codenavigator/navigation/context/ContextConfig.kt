package no.f12.codenavigator.navigation.context

import no.f12.codenavigator.ParamDef
import no.f12.codenavigator.TaskRegistry
import no.f12.codenavigator.config.OutputFormat
import no.f12.codenavigator.navigation.SourceSet
import no.f12.codenavigator.navigation.callgraph.CallGraph
import no.f12.codenavigator.navigation.callgraph.MethodRef

data class ContextConfig(
    val pattern: String,
    val maxDepth: Int,
    val projectOnly: Boolean,
    val filterSynthetic: Boolean,
    val prodOnly: Boolean,
    val testOnly: Boolean,
    val format: OutputFormat,
) {
    fun buildFilter(graph: CallGraph): ((MethodRef) -> Boolean)? {
        val filters = buildList {
            if (projectOnly) add(graph.projectClassFilter())
            if (filterSynthetic) add { ref: MethodRef -> !ref.isGenerated() }
            if (prodOnly) add { ref: MethodRef -> graph.sourceSetOf(ref.className) == SourceSet.MAIN }
            if (testOnly) add { ref: MethodRef -> graph.sourceSetOf(ref.className) == SourceSet.TEST }
        }
        return if (filters.isEmpty()) null else { ref -> filters.all { it(ref) } }
    }

    companion object {
        fun parse(properties: Map<String, String?>): ContextConfig = ContextConfig(
            pattern = properties["pattern"]
                ?: throw IllegalArgumentException("Missing required property 'pattern'"),
            maxDepth = TaskRegistry.CONTEXT_MAXDEPTH.parse(properties["maxdepth"]),
            projectOnly = TaskRegistry.PROJECTONLY_ON.parse(properties["project-only"]),
            filterSynthetic = TaskRegistry.FILTER_SYNTHETIC.parse(properties["filter-synthetic"]),
            prodOnly = TaskRegistry.PROD_ONLY.parse(properties["prod-only"]),
            testOnly = TaskRegistry.TEST_ONLY.parse(properties["test-only"]),
            format = ParamDef.parseFormat(properties),
        )
    }
}
