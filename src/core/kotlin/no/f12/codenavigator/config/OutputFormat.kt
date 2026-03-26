package no.f12.codenavigator.config

enum class OutputFormat {
    TEXT, JSON, LLM;

    companion object {
        fun from(format: String?, llm: Boolean?): OutputFormat = when {
            llm == true -> LLM
            format == "llm" -> LLM
            format == "json" -> JSON
            else -> TEXT
        }
    }
}
