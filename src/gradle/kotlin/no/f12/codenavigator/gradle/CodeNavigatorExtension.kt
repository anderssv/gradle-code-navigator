package no.f12.codenavigator.gradle

open class CodeNavigatorExtension {
    var rootPackage: String = ""

    fun resolveRootPackage(projectProperty: Any?): String =
        projectProperty?.toString() ?: rootPackage
}
