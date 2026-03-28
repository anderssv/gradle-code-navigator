package no.f12.codenavigator.navigation

import no.f12.codenavigator.navigation.callgraph.UsageFormatter
import no.f12.codenavigator.navigation.callgraph.UsageKind
import no.f12.codenavigator.navigation.callgraph.UsageSite
import no.f12.codenavigator.JsonFormatter
import no.f12.codenavigator.LlmFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsageFormatterTest {

    // [TEST] JSON formats single method call usage
    @Test
    fun `JSON formats single method call usage`() {
        val usages = listOf(
            UsageSite(
                callerClass = ClassName("com.example.Caller"),
                callerMethod = "doWork",
                sourceFile = "Caller.kt",
                targetOwner = ClassName("com.example.Target"),
                targetName = "process",
                targetDescriptor = "()V",
                kind = UsageKind.METHOD_CALL,
            ),
        )

        val json = JsonFormatter.formatUsages(usages)

        assertTrue(json.contains("\"callerClass\":\"com.example.Caller\""))
        assertTrue(json.contains("\"callerMethod\":\"doWork\""))
        assertTrue(json.contains("\"targetOwner\":\"com.example.Target\""))
        assertTrue(json.contains("\"targetMethod\":\"process\""))
        assertTrue(json.contains("\"targetDescriptor\":\"()V\""))
        assertTrue(json.contains("\"sourceFile\":\"Caller.kt\""))
        assertTrue(json.contains("\"kind\":\"method_call\""))
    }

    // [TEST] JSON formats empty usages as empty array
    @Test
    fun `JSON formats empty usages as empty array`() {
        val json = JsonFormatter.formatUsages(emptyList())

        assertEquals("[]", json)
    }

    // [TEST] LLM formats single method call usage
    @Test
    fun `LLM formats single method call usage`() {
        val usages = listOf(
            UsageSite(
                callerClass = ClassName("com.example.Caller"),
                callerMethod = "doWork",
                sourceFile = "Caller.kt",
                targetOwner = ClassName("com.example.Target"),
                targetName = "process",
                targetDescriptor = "()V",
                kind = UsageKind.METHOD_CALL,
            ),
        )

        val result = LlmFormatter.formatUsages(usages)

        assertEquals("com.example.Caller.doWork -> com.example.Target.process()V method_call Caller.kt", result)
    }

    // [TEST] LLM formats multiple usages
    @Test
    fun `LLM formats multiple usages on separate lines`() {
        val usages = listOf(
            UsageSite(ClassName("com.example.A"), "fromA", "A.kt", ClassName("com.example.Target"), "process", "()V", UsageKind.METHOD_CALL),
            UsageSite(ClassName("com.example.B"), "fromB", "B.kt", ClassName("com.example.Target"), "name", "Ljava/lang/String;", UsageKind.FIELD_ACCESS),
        )

        val result = LlmFormatter.formatUsages(usages)

        val lines = result.lines()
        assertEquals(2, lines.size)
        assertTrue(lines[0].contains("com.example.A.fromA"))
        assertTrue(lines[1].contains("com.example.B.fromB"))
    }

    // [TEST] TEXT formats usages as table
    @Test
    fun `TEXT formats usages as readable list`() {
        val usages = listOf(
            UsageSite(ClassName("com.example.Caller"), "doWork", "Caller.kt", ClassName("com.example.Target"), "process", "()V", UsageKind.METHOD_CALL),
        )

        val result = UsageFormatter.format(usages)

        assertTrue(result.contains("com.example.Caller.doWork"))
        assertTrue(result.contains("com.example.Target.process"))
        assertTrue(result.contains("Caller.kt"))
    }

    // [TEST] TEXT formats empty usages with message
    @Test
    fun `TEXT shows message when no usages found`() {
        val result = UsageFormatter.format(emptyList())

        assertEquals("No usages found.", result)
    }

    // [TEST] JSON sorts usages by caller class then method
    @Test
    fun `JSON sorts usages by caller class then method`() {
        val usages = listOf(
            UsageSite(ClassName("com.example.Z"), "z", "Z.kt", ClassName("com.example.Target"), "process", "()V", UsageKind.METHOD_CALL),
            UsageSite(ClassName("com.example.A"), "a", "A.kt", ClassName("com.example.Target"), "process", "()V", UsageKind.METHOD_CALL),
        )

        val json = JsonFormatter.formatUsages(usages)

        val aIndex = json.indexOf("com.example.A")
        val zIndex = json.indexOf("com.example.Z")
        assertTrue(aIndex < zIndex, "Expected A before Z in sorted output")
    }

    // [TEST] noResultsGuidance with ownerClass param suggests trying type
    // [TEST] noResultsGuidance with type param suggests checking FQN
    // [TEST] noResultsGuidance includes ownerClass.method in the message

    @Test
    fun `noResultsGuidance includes ownerClass and method in target`() {
        val guidance = UsageFormatter.noResultsGuidance(ownerClass = "com.example.Target", method = "process", field = null, type = null)

        assertTrue(guidance.contains("com.example.Target.process"), "Should include owner.method")
    }

    @Test
    fun `noResultsGuidance with type suggests checking FQN`() {
        val guidance = UsageFormatter.noResultsGuidance(ownerClass = null, method = null, field = null, type = "ContextKt")

        assertTrue(guidance.contains("ContextKt"), "Should include the target")
        assertTrue(guidance.contains("fully-qualified"), "Should suggest checking FQN")
    }

    @Test
    fun `noResultsGuidance with ownerClass suggests trying type`() {
        val guidance = UsageFormatter.noResultsGuidance(ownerClass = "com.example.Target", method = null, field = null, type = null)

        assertTrue(guidance.contains("com.example.Target"), "Should include the target")
        assertTrue(guidance.contains("type"), "Should suggest trying -Ptype")
    }

    @Test
    fun `noResultsGuidance with method suggests trying field`() {
        val guidance = UsageFormatter.noResultsGuidance(ownerClass = "com.example.Target", method = "accountNumber", field = null, type = null)

        assertTrue(guidance.contains("-Pfield=accountNumber"), "Should suggest trying -Pfield")
    }

    @Test
    fun `noResultsGuidance with field includes field in target`() {
        val guidance = UsageFormatter.noResultsGuidance(ownerClass = "com.example.Target", method = null, field = "accountNumber", type = null)

        assertTrue(guidance.contains("com.example.Target.accountNumber"), "Should include owner.field")
    }
}
