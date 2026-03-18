package no.f12.codenavigator

object OutputWrapper {
    fun wrap(output: String, jsonFormat: Boolean): String =
        if (jsonFormat) "---CNAV_BEGIN---\n$output\n---CNAV_END---"
        else output
}
