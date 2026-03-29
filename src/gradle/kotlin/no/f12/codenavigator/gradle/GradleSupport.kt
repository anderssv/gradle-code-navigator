package no.f12.codenavigator.gradle

import no.f12.codenavigator.TaskDef
import no.f12.codenavigator.navigation.SourceSet
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

fun Project.codeNavigatorExtension(): CodeNavigatorExtension =
    extensions.getByType(CodeNavigatorExtension::class.java)

fun Project.buildPropertyMap(
    taskDef: TaskDef,
): Map<String, String?> {
    val propertyNames = taskDef.params.filter { !it.flag }.map { it.name }
    val flagNames = taskDef.params.filter { it.flag }.map { it.name }
    val raw = buildPropertyMap(propertyNames, flagNames)
    return taskDef.enhanceProperties(raw)
}

fun Project.taggedClassDirectories(): List<Pair<File, SourceSet>> {
    val sourceSets = extensions.getByType(SourceSetContainer::class.java)
    val result = mutableListOf<Pair<File, SourceSet>>()

    val mainSourceSet = sourceSets.getByName("main")
    mainSourceSet.output.classesDirs.files.forEach { dir ->
        result.add(dir to SourceSet.MAIN)
    }

    val testSourceSet = sourceSets.findByName("test")
    testSourceSet?.output?.classesDirs?.files
        ?.filter { it.exists() }
        ?.forEach { dir -> result.add(dir to SourceSet.TEST) }

    return result
}

private fun Project.buildPropertyMap(
    propertyNames: List<String>,
    flagNames: List<String>,
): Map<String, String?> {
    val map = mutableMapOf<String, String?>()
    for (name in propertyNames) {
        findProperty(name)?.let { map[name] = it.toString() }
    }
    for (name in flagNames) {
        if (hasProperty(name)) {
            map[name] = null
        }
    }
    return map
}
