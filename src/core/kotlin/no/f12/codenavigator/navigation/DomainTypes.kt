package no.f12.codenavigator.navigation

@JvmInline
value class ClassName(val value: String) : Comparable<ClassName> {
    fun packageName(): PackageName =
        PackageName(value.substringBeforeLast('.', ""))

    override fun compareTo(other: ClassName): Int = value.compareTo(other.value)

    override fun toString(): String = value
}

@JvmInline
value class PackageName(val value: String) {
    override fun toString(): String = value
}
