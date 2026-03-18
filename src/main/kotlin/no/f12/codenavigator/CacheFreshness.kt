package no.f12.codenavigator

import java.io.File

object CacheFreshness {

    fun isFresh(cacheFile: File, classDirectories: List<File>): Boolean {
        if (!cacheFile.exists()) return false

        val cacheLastModified = cacheFile.lastModified()

        return classDirectories
            .filter { it.exists() }
            .flatMap { dir ->
                dir.walkTopDown()
                    .filter { it.isFile && it.extension == "class" }
                    .toList()
            }
            .all { it.lastModified() <= cacheLastModified }
    }
}
