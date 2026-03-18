package no.f12.codenavigator

import kotlin.test.Test
import kotlin.test.assertEquals

class LlmFormatterTest {

    @Test
    fun `formats class list as one line per class`() {
        val classes = listOf(
            ClassInfo("com.example.Foo", "Foo.kt", "com/example/Foo.kt", true),
            ClassInfo("com.example.Bar", "Bar.kt", "com/example/Bar.kt", true),
        )

        val result = LlmFormatter.formatClasses(classes)

        assertEquals("com.example.Bar Bar.kt\ncom.example.Foo Foo.kt", result)
    }

    @Test
    fun `empty class list returns empty string`() {
        assertEquals("", LlmFormatter.formatClasses(emptyList()))
    }

    @Test
    fun `formats symbols compactly`() {
        val symbols = listOf(
            SymbolInfo("com.example", "Service", "doWork", SymbolKind.METHOD, "Service.kt"),
            SymbolInfo("com.example", "Service", "name", SymbolKind.FIELD, "Service.kt"),
        )

        val result = LlmFormatter.formatSymbols(symbols)

        assertEquals("com.example.Service.doWork method Service.kt\ncom.example.Service.name field Service.kt", result)
    }

    @Test
    fun `formats class details compactly`() {
        val details = listOf(
            ClassDetail(
                className = "com.example.UserService",
                sourceFile = "UserService.kt",
                superClass = null,
                interfaces = listOf("UserOperations"),
                fields = listOf(FieldDetail("repo", "UserRepository")),
                methods = listOf(MethodDetail("findById", listOf("long"), "User")),
            )
        )

        val result = LlmFormatter.formatClassDetails(details)

        assertEquals(
            "com.example.UserService UserService.kt implements:UserOperations fields:repo:UserRepository methods:findById(long):User",
            result
        )
    }

    @Test
    fun `formats call trees compactly`() {
        val trees = listOf(
            CallTreeNode(
                method = MethodRef("com.example.Service", "doWork"),
                sourceFile = "Service.kt",
                children = listOf(
                    CallTreeNode(
                        method = MethodRef("com.example.Controller", "handle"),
                        sourceFile = "Controller.kt",
                        children = emptyList(),
                    )
                ),
            )
        )

        val result = LlmFormatter.renderCallTrees(trees, CallDirection.CALLERS)

        assertEquals("com.example.Service.doWork Service.kt\n  ← com.example.Controller.handle Controller.kt", result)
    }

    @Test
    fun `formats interfaces compactly`() {
        val registry = InterfaceRegistry(mapOf(
            "com.example.Repository" to listOf(
                ImplementorInfo("com.example.SqlRepo", "SqlRepo.kt"),
                ImplementorInfo("com.example.MemRepo", "MemRepo.kt"),
            )
        ))

        val result = LlmFormatter.formatInterfaces(registry, listOf("com.example.Repository"))

        assertEquals("com.example.Repository: com.example.MemRepo(MemRepo.kt),com.example.SqlRepo(SqlRepo.kt)", result)
    }

    @Test
    fun `formats package deps compactly`() {
        val deps = PackageDependencies(mapOf(
            "com.example.api" to listOf("com.example.service", "com.example.model"),
        ))

        val result = LlmFormatter.formatPackageDeps(deps, listOf("com.example.api"), false)

        assertEquals("com.example.api -> com.example.service,com.example.model", result)
    }

    @Test
    fun `formats reverse package deps`() {
        val deps = PackageDependencies(mapOf(
            "com.example.api" to listOf("com.example.model"),
            "com.example.service" to listOf("com.example.model"),
        ))

        val result = LlmFormatter.formatPackageDeps(deps, listOf("com.example.model"), true)

        assertEquals("com.example.model <- com.example.api,com.example.service", result)
    }
}
