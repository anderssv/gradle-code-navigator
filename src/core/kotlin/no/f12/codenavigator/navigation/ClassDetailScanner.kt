package no.f12.codenavigator.navigation

import java.io.File

object ClassDetailScanner {

    fun scan(classDirectories: List<File>, pattern: String): List<ClassDetail> {
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        return classDirectories
            .filter { it.exists() }
            .flatMap { dir ->
                dir.walkTopDown()
                    .filter { it.isFile && it.extension == "class" }
                    .filter { file ->
                        val info = ClassInfoExtractor.extract(file)
                        info.isUserDefinedClass && regex.containsMatchIn(info.className)
                    }
                    .map { ClassDetailExtractor.extract(it) }
                    .toList()
            }
            .sortedBy { it.className }
    }
}
