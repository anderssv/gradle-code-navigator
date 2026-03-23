package no.f12.codenavigator.analysis

import java.io.File
import java.time.LocalDate

object GitLogRunner {

    fun run(projectDir: File, after: LocalDate): List<GitCommit> {
        val command = buildCommand(after)
        val process = ProcessBuilder(command)
            .directory(projectDir)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("git log failed (exit code $exitCode): $output")
        }

        return GitLogParser.parse(output)
    }

    internal fun buildCommand(after: LocalDate): List<String> =
        listOf(
            "git", "log",
            "--all",
            "--numstat",
            "--date=short",
            "--pretty=format:--%h--%ad--%aN",
            "--no-renames",
            "--after=$after",
        )
}
