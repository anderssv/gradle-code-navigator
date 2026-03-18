package no.f12.codenavigator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CallTreeBuilderTest {

    @Test
    fun `single method with no callers produces tree with empty children`() {
        val graph = CallGraph(emptyMap())
        val target = MethodRef("com.example.Service", "doWork")

        val result = CallTreeBuilder.build(graph, listOf(target), maxDepth = 3, CallDirection.CALLERS)

        assertEquals(1, result.size)
        assertEquals("com.example.Service.doWork", result[0].method.qualifiedName)
        assertTrue(result[0].children.isEmpty())
    }

    @Test
    fun `single method with one direct caller produces one child`() {
        val caller = MethodRef("com.example.Controller", "handleRequest")
        val target = MethodRef("com.example.Service", "doWork")
        val graph = CallGraph(
            mapOf(caller to setOf(target)),
            sourceFiles = mapOf("com.example.Controller" to "Controller.kt"),
        )

        val result = CallTreeBuilder.build(graph, listOf(target), maxDepth = 3, CallDirection.CALLERS)

        assertEquals(1, result.size)
        val root = result[0]
        assertEquals(1, root.children.size)
        assertEquals("handleRequest", root.children[0].method.methodName)
        assertEquals("Controller.kt", root.children[0].sourceFile)
    }

    @Test
    fun `transitive callers produce nested children up to depth`() {
        val target = MethodRef("com.example.C", "end")
        val middle = MethodRef("com.example.B", "middle")
        val top = MethodRef("com.example.A", "start")
        val graph = CallGraph(
            mapOf(
                top to setOf(middle),
                middle to setOf(target),
            ),
            sourceFiles = mapOf(
                "com.example.A" to "A.kt",
                "com.example.B" to "B.kt",
            ),
        )

        val result = CallTreeBuilder.build(graph, listOf(target), maxDepth = 3, CallDirection.CALLERS)

        val root = result[0]
        assertEquals("end", root.method.methodName)
        assertEquals(1, root.children.size)
        assertEquals("middle", root.children[0].method.methodName)
        assertEquals(1, root.children[0].children.size)
        assertEquals("start", root.children[0].children[0].method.methodName)
    }

    @Test
    fun `depth limit stops recursion`() {
        val target = MethodRef("com.example.C", "end")
        val middle = MethodRef("com.example.B", "middle")
        val top = MethodRef("com.example.A", "start")
        val graph = CallGraph(
            mapOf(
                top to setOf(middle),
                middle to setOf(target),
            ),
        )

        val result = CallTreeBuilder.build(graph, listOf(target), maxDepth = 1, CallDirection.CALLERS)

        val root = result[0]
        assertEquals(1, root.children.size)
        assertEquals("middle", root.children[0].method.methodName)
        assertTrue(root.children[0].children.isEmpty(), "Should not recurse past depth 1")
    }

    @Test
    fun `cycle detection prevents infinite recursion`() {
        val a = MethodRef("com.example.A", "callB")
        val b = MethodRef("com.example.B", "callA")
        val graph = CallGraph(
            mapOf(
                a to setOf(b),
                b to setOf(a),
            ),
        )

        val result = CallTreeBuilder.build(graph, listOf(b), maxDepth = 10, CallDirection.CALLERS)

        val root = result[0]
        assertEquals("callA", root.method.methodName)
        assertEquals(1, root.children.size)
        assertEquals("callB", root.children[0].method.methodName)
        assertEquals(1, root.children[0].children.size, "Cycle should show visited node but no further children")
        assertTrue(root.children[0].children[0].children.isEmpty())
    }

    @Test
    fun `filter removes non-matching methods from children`() {
        val target = MethodRef("com.example.Service", "doWork")
        val projectCaller = MethodRef("com.example.Controller", "handle")
        val externalCaller = MethodRef("org.springframework.Framework", "invoke")
        val graph = CallGraph(
            mapOf(
                projectCaller to setOf(target),
                externalCaller to setOf(target),
            ),
        )
        val projectClasses = setOf("com.example.Service", "com.example.Controller")

        val result = CallTreeBuilder.build(
            graph,
            listOf(target),
            maxDepth = 3,
            CallDirection.CALLERS,
            filter = { it.className in projectClasses },
        )

        assertEquals(1, result[0].children.size)
        assertEquals("handle", result[0].children[0].method.methodName)
    }

    @Test
    fun `multiple root methods each produce their own tree`() {
        val targetA = MethodRef("com.example.RepoA", "save")
        val targetB = MethodRef("com.example.RepoB", "save")
        val callerA = MethodRef("com.example.ServiceA", "persist")
        val callerB = MethodRef("com.example.ServiceB", "store")
        val graph = CallGraph(
            mapOf(
                callerA to setOf(targetA),
                callerB to setOf(targetB),
            ),
        )

        val result = CallTreeBuilder.build(graph, listOf(targetA, targetB), maxDepth = 3, CallDirection.CALLERS)

        assertEquals(2, result.size)
        assertEquals("com.example.RepoA", result[0].method.className)
        assertEquals("com.example.RepoB", result[1].method.className)
        assertEquals(1, result[0].children.size)
        assertEquals(1, result[1].children.size)
    }

    @Test
    fun `branching transitive callers produce correct tree`() {
        val buildMsg = MethodRef("com.example.UserService", "buildNotificationMessage")
        val sendDeactivation = MethodRef("com.example.UserService", "sendDeactivationNotification")
        val sendReset = MethodRef("com.example.UserService", "sendResetNotification")
        val deactivateUser = MethodRef("com.example.UserService", "deactivateUser")
        val resetPassword = MethodRef("com.example.UserService", "resetPassword")
        val handleDeactivate = MethodRef("com.example.UserRoute", "handleDeactivate")
        val handleReset = MethodRef("com.example.UserRoute", "handleReset")
        val graph = CallGraph(
            mapOf(
                sendDeactivation to setOf(buildMsg),
                sendReset to setOf(buildMsg),
                deactivateUser to setOf(sendDeactivation),
                resetPassword to setOf(sendReset),
                handleDeactivate to setOf(deactivateUser),
                handleReset to setOf(resetPassword),
            ),
        )

        val result = CallTreeBuilder.build(graph, listOf(buildMsg), maxDepth = 5, CallDirection.CALLERS)

        val root = result[0]
        assertEquals(2, root.children.size, "buildNotificationMessage should have 2 direct callers")

        val sendDeactivationNode = root.children.first { it.method.methodName == "sendDeactivationNotification" }
        assertEquals(1, sendDeactivationNode.children.size)
        assertEquals("deactivateUser", sendDeactivationNode.children[0].method.methodName)
        assertEquals(1, sendDeactivationNode.children[0].children.size)
        assertEquals("handleDeactivate", sendDeactivationNode.children[0].children[0].method.methodName)

        val sendResetNode = root.children.first { it.method.methodName == "sendResetNotification" }
        assertEquals(1, sendResetNode.children.size)
        assertEquals("resetPassword", sendResetNode.children[0].method.methodName)
        assertEquals(1, sendResetNode.children[0].children.size)
        assertEquals("handleReset", sendResetNode.children[0].children[0].method.methodName)
    }

    @Test
    fun `callees direction resolves children as callees`() {
        val a = MethodRef("com.example.A", "start")
        val b = MethodRef("com.example.B", "middle")
        val c = MethodRef("com.example.C", "end")
        val graph = CallGraph(
            mapOf(
                a to setOf(b),
                b to setOf(c),
            ),
            sourceFiles = mapOf(
                "com.example.B" to "B.kt",
                "com.example.C" to "C.kt",
            ),
        )

        val result = CallTreeBuilder.build(graph, listOf(a), maxDepth = 3, CallDirection.CALLEES)

        val root = result[0]
        assertEquals("start", root.method.methodName)
        assertEquals(1, root.children.size)
        assertEquals("middle", root.children[0].method.methodName)
        assertEquals("B.kt", root.children[0].sourceFile)
        assertEquals(1, root.children[0].children.size)
        assertEquals("end", root.children[0].children[0].method.methodName)
        assertEquals("C.kt", root.children[0].children[0].sourceFile)
    }

    @Test
    fun `root node has no source file`() {
        val target = MethodRef("com.example.Service", "doWork")
        val graph = CallGraph(
            emptyMap(),
            sourceFiles = mapOf("com.example.Service" to "Service.kt"),
        )

        val result = CallTreeBuilder.build(graph, listOf(target), maxDepth = 3, CallDirection.CALLERS)

        assertEquals(null, result[0].sourceFile, "Root node should not have source file annotation")
    }
}
