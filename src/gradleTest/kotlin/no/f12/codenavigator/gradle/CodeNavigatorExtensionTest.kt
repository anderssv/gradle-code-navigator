package no.f12.codenavigator.gradle

import kotlin.test.Test
import kotlin.test.assertEquals

class CodeNavigatorExtensionTest {

    @Test
    fun `rootPackage defaults to empty string`() {
        val extension = CodeNavigatorExtension()

        assertEquals("", extension.rootPackage)
    }

    @Test
    fun `rootPackage returns -P flag value when present`() {
        val extension = CodeNavigatorExtension()
        extension.rootPackage = "com.configured"

        val result = extension.resolveRootPackage(projectProperty = "com.override")

        assertEquals("com.override", result)
    }

    @Test
    fun `resolveRootPackage returns extension value when -P flag is null`() {
        val extension = CodeNavigatorExtension()
        extension.rootPackage = "com.configured"

        val result = extension.resolveRootPackage(projectProperty = null)

        assertEquals("com.configured", result)
    }
}
