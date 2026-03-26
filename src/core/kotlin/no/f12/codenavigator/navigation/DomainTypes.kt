package no.f12.codenavigator.navigation

@JvmInline
value class ClassName(val value: String) : Comparable<ClassName> {
    fun packageName(): PackageName =
        PackageName(value.substringBeforeLast('.', ""))

    fun isGenerated(): Boolean = '$' in value

    override fun compareTo(other: ClassName): Int = value.compareTo(other.value)

    override fun toString(): String = value
}

@JvmInline
value class PackageName(val value: String) : Comparable<PackageName> {
    override fun compareTo(other: PackageName): Int = value.compareTo(other.value)

    override fun toString(): String = value
}
