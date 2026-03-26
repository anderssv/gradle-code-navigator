package no.f12.codenavigator

import no.f12.codenavigator.config.OutputFormat

object OutputWrapper {
    fun wrap(output: String, format: OutputFormat): String =
        when (format) {
            OutputFormat.TEXT -> output
            OutputFormat.JSON, OutputFormat.LLM -> "---CNAV_BEGIN---\n$output\n---CNAV_END---"
        }
}
