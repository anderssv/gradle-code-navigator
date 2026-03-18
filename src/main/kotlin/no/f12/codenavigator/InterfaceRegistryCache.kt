package no.f12.codenavigator

import java.io.File

object InterfaceRegistryCache {

    private const val FIELD_SEPARATOR = "\t"

    fun write(cacheFile: File, registry: InterfaceRegistry) {
        cacheFile.parentFile?.mkdirs()
        cacheFile.bufferedWriter().use { writer ->
            registry.forEachEntry { interfaceName, implementors ->
                implementors.forEach { impl ->
                    writer.write(
                        listOf(
                            interfaceName,
                            impl.className,
                            impl.sourceFile,
                        ).joinToString(FIELD_SEPARATOR),
                    )
                    writer.newLine()
                }
            }
        }
    }

    fun read(cacheFile: File): InterfaceRegistry {
        val map = mutableMapOf<String, MutableList<ImplementorInfo>>()

        cacheFile.useLines { lines ->
            lines.filter { it.isNotBlank() }.forEach { line ->
                val parts = line.split(FIELD_SEPARATOR)
                val interfaceName = parts[0]
                val impl = ImplementorInfo(parts[1], parts[2])
                map.getOrPut(interfaceName) { mutableListOf() }.add(impl)
            }
        }

        return InterfaceRegistry(map)
    }

    fun isFresh(cacheFile: File, classDirectories: List<File>): Boolean =
        CacheFreshness.isFresh(cacheFile, classDirectories)

    fun getOrBuild(cacheFile: File, classDirectories: List<File>): InterfaceRegistry {
        if (isFresh(cacheFile, classDirectories)) {
            return read(cacheFile)
        }

        val registry = InterfaceRegistry.build(classDirectories)
        write(cacheFile, registry)
        return registry
    }
}
