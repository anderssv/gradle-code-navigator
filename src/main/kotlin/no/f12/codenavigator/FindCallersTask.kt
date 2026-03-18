package no.f12.codenavigator

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = "Produces console output only")
abstract class FindCallersTask : DefaultTask() {

    @TaskAction
    fun findCallers() {
        val methodPattern = project.findProperty("method")?.toString()
            ?: throw GradleException("Missing required property 'method'. Usage: ./gradlew cnavCallers -Pmethod=<regex>")
        val maxDepth = project.findProperty("maxdepth")?.toString()?.toIntOrNull()
            ?: throw GradleException("Missing required property 'maxdepth'. Usage: ./gradlew cnavCallers -Pmethod=<regex> -Pmaxdepth=3")
        val projectOnly = project.findProperty("projectonly")?.toString()?.toBoolean() ?: false
        val format = OutputFormat.from(project)

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val mainSourceSet = sourceSets.getByName("main")
        val classDirectories = mainSourceSet.output.classesDirs.files.toList()

        val cacheFile = File(project.layout.buildDirectory.asFile.get(), "cnav/call-graph.cache")
        val graph = CallGraphCache.getOrBuild(cacheFile, classDirectories)
        val methods = graph.findMethods(methodPattern)

        if (methods.isEmpty()) {
            logger.lifecycle("No methods found matching '$methodPattern'")
            return
        }

        val filter: ((MethodRef) -> Boolean)? =
            if (projectOnly) graph.projectClassFilter() else null

        val trees = CallTreeBuilder.build(graph, methods, maxDepth, CallDirection.CALLERS, filter)
        val output = when (format) {
            OutputFormat.JSON -> JsonFormatter.renderCallTrees(trees)
            OutputFormat.LLM -> LlmFormatter.renderCallTrees(trees, CallDirection.CALLERS)
            OutputFormat.TEXT -> CallTreeFormatter.renderTrees(trees, CallDirection.CALLERS)
        }
        logger.lifecycle(OutputWrapper.wrap(output, format))
    }
}
