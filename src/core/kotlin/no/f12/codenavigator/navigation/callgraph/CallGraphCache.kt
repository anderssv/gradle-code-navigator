package no.f12.codenavigator.navigation.callgraph

import no.f12.codenavigator.CacheFreshness
import no.f12.codenavigator.navigation.ClassName
import no.f12.codenavigator.navigation.FileCache
import no.f12.codenavigator.navigation.ScanResult
import java.io.File

object CallGraphCache : FileCache<CallGraph>() {

    private const val EDGES_HEADER = "[EDGES]"
    private const val SOURCES_HEADER = "[SOURCES]"
    private const val LINES_HEADER = "[LINES]"

    override fun write(cacheFile: File, data: CallGraph) {
        CacheFreshness.atomicWrite(cacheFile) { file ->
            file.bufferedWriter().use { writer ->
                writer.write(EDGES_HEADER)
                writer.newLine()
                data.forEachEdge { caller, callee ->
                    writer.write(
                        listOf(
                            caller.className.toString(),
                            caller.methodName,
                            callee.className.toString(),
                            callee.methodName,
                        ).joinToString(FIELD_SEPARATOR),
                    )
                    writer.newLine()
                }
                writer.write(SOURCES_HEADER)
                writer.newLine()
                data.forEachSourceFile { className, sourceFile ->
                    writer.write(
                        listOf(className.toString(), sourceFile).joinToString(FIELD_SEPARATOR),
                    )
                    writer.newLine()
                }
                writer.write(LINES_HEADER)
                writer.newLine()
                data.forEachLineNumber { method, lineNumber ->
                    writer.write(
                        listOf(method.className.toString(), method.methodName, lineNumber.toString()).joinToString(FIELD_SEPARATOR),
                    )
                    writer.newLine()
                }
            }
        }
    }

    override fun read(cacheFile: File): CallGraph {
        val callerToCallees = mutableMapOf<MethodRef, MutableSet<MethodRef>>()
        val sourceFiles = mutableMapOf<ClassName, String>()
        val lineNumbers = mutableMapOf<MethodRef, Int>()

        var section = ""
        cacheFile.useLines { lines ->
            lines.filter { it.isNotBlank() }.forEach { line ->
                when {
                    line == EDGES_HEADER -> section = EDGES_HEADER
                    line == SOURCES_HEADER -> section = SOURCES_HEADER
                    line == LINES_HEADER -> section = LINES_HEADER
                    section == EDGES_HEADER -> {
                        val parts = line.split(FIELD_SEPARATOR)
                        val caller = MethodRef(ClassName(parts[0]), parts[1])
                        val callee = MethodRef(ClassName(parts[2]), parts[3])
                        callerToCallees.getOrPut(caller) { mutableSetOf() }.add(callee)
                    }
                    section == SOURCES_HEADER -> {
                        val parts = line.split(FIELD_SEPARATOR)
                        sourceFiles[ClassName(parts[0])] = parts[1]
                    }
                    section == LINES_HEADER -> {
                        val parts = line.split(FIELD_SEPARATOR)
                        val method = MethodRef(ClassName(parts[0]), parts[1])
                        lineNumbers[method] = parts[2].toInt()
                    }
                }
            }
        }

        return CallGraph(callerToCallees, sourceFiles, lineNumbers)
    }

    override fun build(classDirectories: List<File>): ScanResult<CallGraph> =
        CallGraphBuilder.build(classDirectories)
}
