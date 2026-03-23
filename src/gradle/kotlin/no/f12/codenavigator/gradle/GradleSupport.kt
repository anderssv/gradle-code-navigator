package no.f12.codenavigator.gradle

import no.f12.codenavigator.OutputFormat
import org.gradle.api.Project

fun Project.outputFormat(): OutputFormat = OutputFormat.from(
    format = findProperty("format")?.toString(),
    llm = findProperty("llm")?.toString()?.toBoolean(),
)
