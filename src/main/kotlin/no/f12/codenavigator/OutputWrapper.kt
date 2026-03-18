package no.f12.codenavigator

import org.gradle.api.Project

enum class OutputFormat {
    TEXT, JSON, LLM;

    companion object {
        fun from(project: Project): OutputFormat = when {
            project.findProperty("llm")?.toString()?.toBoolean() == true -> LLM
            project.findProperty("format")?.toString() == "json" -> JSON
            else -> TEXT
        }
    }
}

object OutputWrapper {
    fun wrap(output: String, format: OutputFormat): String =
        when (format) {
            OutputFormat.TEXT -> output
            OutputFormat.JSON, OutputFormat.LLM -> "---CNAV_BEGIN---\n$output\n---CNAV_END---"
        }
}
