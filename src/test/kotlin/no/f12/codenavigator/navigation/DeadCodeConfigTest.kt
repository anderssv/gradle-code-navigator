package no.f12.codenavigator.navigation

import no.f12.codenavigator.config.OutputFormat
import no.f12.codenavigator.navigation.deadcode.DeadCodeConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeadCodeConfigTest {

    @Test
    fun `parses all properties from map`() {
        val props = mapOf(
            "filter" to "Service",
            "exclude" to "Test",
            "format" to "json",
        )

        val config = DeadCodeConfig.parse(props)

        assertTrue(config.filter!!.containsMatchIn("MyService"))
        assertTrue(config.exclude!!.containsMatchIn("MyTest"))
        assertEquals(OutputFormat.JSON, config.format)
    }

    @Test
    fun `defaults filter to null when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertNull(config.filter)
    }

    @Test
    fun `defaults exclude to null when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertNull(config.exclude)
    }

    @Test
    fun `defaults to TEXT format`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertEquals(OutputFormat.TEXT, config.format)
    }

    @Test
    fun `parses LLM format`() {
        val config = DeadCodeConfig.parse(mapOf("llm" to "true"))

        assertEquals(OutputFormat.LLM, config.format)
    }

    @Test
    fun `filter regex is case-insensitive`() {
        val config = DeadCodeConfig.parse(mapOf("filter" to "service"))

        assertTrue(config.filter!!.containsMatchIn("MyService"))
    }

    @Test
    fun `exclude regex is case-insensitive`() {
        val config = DeadCodeConfig.parse(mapOf("exclude" to "test"))

        assertTrue(config.exclude!!.containsMatchIn("MyTest"))
    }

    @Test
    fun `defaults classesOnly to false when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertFalse(config.classesOnly)
    }

    @Test
    fun `parses classesOnly as true`() {
        val config = DeadCodeConfig.parse(mapOf("classes-only" to "true"))

        assertTrue(config.classesOnly)
    }

    @Test
    fun `parses classesOnly as false`() {
        val config = DeadCodeConfig.parse(mapOf("classes-only" to "false"))

        assertFalse(config.classesOnly)
    }

    @Test
    fun `parses exclude-annotated from comma-separated string`() {
        val config = DeadCodeConfig.parse(mapOf("exclude-annotated" to "RestController,Scheduled,Component"))

        assertEquals(listOf("RestController", "Scheduled", "Component"), config.excludeAnnotated)
    }

    @Test
    fun `defaults exclude-annotated to empty list when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertTrue(config.excludeAnnotated.isEmpty())
    }

    @Test
    fun `trims whitespace from exclude-annotated values`() {
        val config = DeadCodeConfig.parse(mapOf("exclude-annotated" to " RestController , Scheduled "))

        assertEquals(listOf("RestController", "Scheduled"), config.excludeAnnotated)
    }

    @Test
    fun `defaults prodOnly to false when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertFalse(config.prodOnly)
    }

    @Test
    fun `parses prodOnly as true`() {
        val config = DeadCodeConfig.parse(mapOf("prod-only" to "true"))

        assertTrue(config.prodOnly)
    }

    @Test
    fun `parses prodOnly as false`() {
        val config = DeadCodeConfig.parse(mapOf("prod-only" to "false"))

        assertFalse(config.prodOnly)
    }

    @Test
    fun `framework=spring adds spring annotations to excludeAnnotated`() {
        val config = DeadCodeConfig.parse(mapOf("framework" to "spring"))

        assertTrue(config.excludeAnnotated.contains("org.springframework.stereotype.Controller"))
        assertTrue(config.excludeAnnotated.contains("org.springframework.stereotype.Component"))
        assertTrue(config.excludeAnnotated.contains("org.springframework.stereotype.Service"))
    }

    @Test
    fun `framework merges with explicit exclude-annotated`() {
        val config = DeadCodeConfig.parse(mapOf(
            "framework" to "spring",
            "exclude-annotated" to "MyCustomAnnotation",
        ))

        assertTrue(config.excludeAnnotated.contains("org.springframework.stereotype.Controller"))
        assertTrue(config.excludeAnnotated.contains("MyCustomAnnotation"))
    }

    @Test
    fun `framework defaults to empty when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertTrue(config.excludeAnnotated.isEmpty())
    }

    @Test
    fun `defaults modifierAnnotated to empty list when absent`() {
        val config = DeadCodeConfig.parse(emptyMap())

        assertTrue(config.modifierAnnotated.isEmpty())
    }

    @Test
    fun `framework=spring populates modifierAnnotated with spring modifier annotations`() {
        val config = DeadCodeConfig.parse(mapOf("framework" to "spring"))

        assertTrue(config.modifierAnnotated.contains("org.springframework.transaction.annotation.Transactional"))
        assertTrue(config.modifierAnnotated.contains("org.springframework.cache.annotation.Cacheable"))
    }

    @Test
    fun `framework=spring excludeAnnotated does not contain modifier annotations`() {
        val config = DeadCodeConfig.parse(mapOf("framework" to "spring"))

        assertFalse(config.excludeAnnotated.contains("org.springframework.transaction.annotation.Transactional"))
        assertFalse(config.excludeAnnotated.contains("org.springframework.cache.annotation.Cacheable"))
    }

    @Test
    fun `framework=quarkus populates both excludeAnnotated and modifierAnnotated`() {
        val config = DeadCodeConfig.parse(mapOf("framework" to "quarkus"))

        assertTrue(config.excludeAnnotated.contains("jakarta.ws.rs.GET"), "Entry-point annotation should be in excludeAnnotated")
        assertTrue(config.modifierAnnotated.contains("org.eclipse.microprofile.faulttolerance.CircuitBreaker"), "Modifier annotation should be in modifierAnnotated")
        assertFalse(config.excludeAnnotated.contains("org.eclipse.microprofile.faulttolerance.CircuitBreaker"), "Modifier should NOT be in excludeAnnotated")
    }
}
