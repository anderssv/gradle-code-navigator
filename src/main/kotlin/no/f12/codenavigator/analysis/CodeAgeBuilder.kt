package no.f12.codenavigator.analysis

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class FileAge(
    val file: String,
    val ageMonths: Long,
    val lastChangeDate: LocalDate,
)

object CodeAgeBuilder {

    fun build(
        commits: List<GitCommit>,
        now: LocalDate,
        top: Int = 50,
    ): List<FileAge> {
        val lastChanged = mutableMapOf<String, LocalDate>()

        for (commit in commits) {
            for (file in commit.files) {
                val existing = lastChanged[file.path]
                if (existing == null || commit.date.isAfter(existing)) {
                    lastChanged[file.path] = commit.date
                }
            }
        }

        return lastChanged
            .map { (file, date) ->
                val months = ChronoUnit.MONTHS.between(date, now)
                FileAge(file, months, date)
            }
            .sortedByDescending { it.ageMonths }
            .take(top)
    }
}
