package no.f12.codenavigator

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = "Produces console output only")
abstract class FindClassTask : DefaultTask() {

    @TaskAction
    fun findClass() {
        val pattern = project.findProperty("pattern")?.toString()
            ?: throw GradleException("Missing required property 'pattern'. Usage: ./gradlew cnavFindClass -Ppattern=<regex>")
        val format = OutputFormat.from(project)

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val mainSourceSet = sourceSets.getByName("main")
        val classDirectories = mainSourceSet.output.classesDirs.files.toList()
        val cacheFile = File(project.layout.buildDirectory.asFile.get(), "cnav/class-index.cache")

        val allClasses = ClassIndexCache.getOrScan(cacheFile, classDirectories)
        val matches = ClassFilter.filter(allClasses, pattern)
        val output = when (format) {
            OutputFormat.JSON -> JsonFormatter.formatClasses(matches)
            OutputFormat.LLM -> LlmFormatter.formatClasses(matches)
            OutputFormat.TEXT -> TableFormatter.format(matches)
        }

        logger.lifecycle(OutputWrapper.wrap(output, format))
    }
}
