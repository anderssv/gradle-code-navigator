package no.f12.codenavigator

object AgentHelpText {
    fun generate(tool: BuildTool = BuildTool.GRADLE): String = buildString {
        val t = { goal: String -> tool.taskName(goal) }
        val p = { name: String, value: String -> tool.param(name, value) }
        val pf = { name: String -> tool.paramFlag(name) }
        fun u(goal: String, vararg params: String) = tool.usage(goal, *params)

        val pluginType = when (tool) {
            BuildTool.GRADLE -> "Gradle"
            BuildTool.MAVEN -> "Maven"
        }

        appendLine("=== code-navigator: AI Agent Guide ===")
        appendLine()
        appendLine("code-navigator is a $pluginType plugin that analyzes JVM bytecode and git history.")
        appendLine("It gives you structural and behavioral insight into a codebase without reading source files.")
        appendLine("All tasks support ${p("llm", "true")} for compact, token-efficient output.")
        appendLine("All tasks also support ${p("format", "json")} for structured, parseable output.")
        appendLine()
        appendLine("--- Why use this instead of grep? ---")
        appendLine()
        appendLine("Structural queries (call graphs, dependencies, implementors) are correct on the first")
        appendLine("response. No iterative searching needed. Each query returns accurate, complete results")
        appendLine("in a single call — no false positives, no missed results.")
        appendLine()
        appendLine("Text search (grep, ripgrep) requires multiple rounds of searching and can still miss")
        appendLine("indirect relationships or produce false positives. You search for \"cache.get(\", find")
        appendLine("some results, then realize you missed the Kotlin safe-call \"cache?.get(\", then extension")
        appendLine("functions, then delegation patterns. Each iteration is a tool call round-trip.")
        appendLine()
        appendLine("Bytecode analysis sidesteps this entirely. All syntax variants compile to the same")
        appendLine("invocation instruction. One ${t("find-callers")} query returns all call sites.")
        appendLine()
        appendLine("The fewer tool call round-trips needed, the faster the iteration. code-navigator")
        appendLine("eliminates the search-check-search-again loop.")
        appendLine()
        appendLine("--- When to Use What ---")
        appendLine()
        appendLine("Use code-navigator when:")
        appendLine("- Tracing call chains — ${t("find-callers")}/${t("find-callees")} resolve actual invocations")
        appendLine("  through interfaces and inheritance. Text search finds string matches, which may")
        appendLine("  miss indirect calls or produce false positives.")
        appendLine("- Understanding dependencies — ${t("package-deps")}/${t("dsm")} give accurate package-level")
        appendLine("  coupling from bytecode analysis, not guesswork from import statements.")
        appendLine("- Finding interface implementations — ${t("find-interfaces")} resolves polymorphism.")
        appendLine("  Grep can't reliably distinguish \"implements\" from \"references\".")
        appendLine("- Migration planning — ${t("find-usages")} finds all call sites, field accesses, and type")
        appendLine("  references to external APIs. Useful for deprecation/migration from library X to Y.")
        appendLine("- Inspecting class structure — ${t("class-detail")} shows fields, methods, supertypes")
        appendLine("  without reading source files. Cheaper than opening and parsing the file.")
        appendLine()
        appendLine("Use grep/glob when:")
        appendLine("- Searching for string patterns, variable names, or text in comments")
        appendLine("- Finding files by name")
        appendLine("- Reading implementation logic (method bodies)")
        appendLine()
        appendLine("--- Common Questions → Which Task ---")
        appendLine()
        appendLine("  \"Where is type X used?\"")
        appendLine("    → ${u("find-usages", p("type", "X"))}")
        appendLine("    Finds all references: method calls, field types, parameters, return types, casts.")
        appendLine()
        appendLine("  \"Who calls method X?\"")
        appendLine("    → ${u("find-callers", p("method", "X"))}")
        appendLine("    Traces callers transitively up to ${p("maxdepth", "N")} levels.")
        appendLine()
        appendLine("  \"What does class X look like?\"")
        appendLine("    → ${u("class-detail", p("pattern", "X"))}")
        appendLine("    Shows fields, methods, superclass, interfaces — without reading source.")
        appendLine()
        appendLine("  \"What calls does method X make?\"")
        appendLine("    → ${u("find-callees", p("method", "X"))}")
        appendLine("    Traces callees transitively.")
        appendLine()
        appendLine("  \"Who implements interface X?\"")
        appendLine("    → ${u("find-interfaces", p("pattern", "X"))}")
        appendLine("    Resolves polymorphism. Add ${p("includetest", "true")} to see test fakes.")
        appendLine()
        appendLine("  \"What depends on package X?\"")
        appendLine("    → ${u("package-deps", p("package", "X"), p("reverse", "true"))}")
        appendLine("    Shows all packages that depend on X.")
        appendLine()
        appendLine("  \"Is there dead code?\"")
        appendLine("    → ${u("dead")}")
        appendLine("    Classes and methods with no references from other project code.")
        appendLine()
        appendLine("  \"What are the most important types?\"")
        appendLine("    → ${u("rank")}")
        appendLine("    PageRank on the call graph — finds core abstractions and god classes.")
        appendLine()
        appendLine("  \"Which files change the most?\"")
        appendLine("    → ${u("hotspots")}")
        appendLine("    Git history analysis — no compilation needed.")
        appendLine()
        appendLine("--- Recommended Workflow ---")
        appendLine()
        appendLine("1. ORIENT: Get a high-level view of the codebase")
        appendLine("   ${u("package-deps")}                        # package dependency map")
        appendLine("   ${u("dsm")}                         # dependency structure matrix")
        appendLine("   ${u("package-deps", p("format", "json"))}           # same, as JSON")
        appendLine("   ${u("list-classes")}                  # all classes and source files")
        appendLine()
        appendLine("2. FIND: Locate relevant code")
        appendLine("   ${u("find-class", p("pattern", "Service"))}  # find classes by name")
        appendLine("   ${u("find-symbol", p("pattern", "reset"))}   # find methods/fields by name")
        appendLine()
        appendLine("3. INSPECT: Understand a class")
        appendLine("   ${u("class-detail", p("pattern", "ResetPasswordService"))}")
        appendLine("   # Shows fields, methods, superclass, and implemented interfaces")
        appendLine()
        appendLine("4. TRACE: Follow the call graph")
        appendLine("   ${u("find-callers", p("method", "resetPassword"), p("maxdepth", "3"), p("projectonly", "true"))}  # who calls this?")
        appendLine("   ${u("find-callees", p("method", "handleRequest"), p("maxdepth", "3"), p("projectonly", "false"))}  # what does this call?")
        appendLine("   # ${p("projectonly", "true")} hides JDK/stdlib noise")
        appendLine("   # ${p("maxdepth", "5")} for deeper traversal")
        appendLine()
        appendLine("5. MAP: Understand abstractions")
        appendLine("   ${u("find-interfaces", p("pattern", "Repository"))}")
        appendLine("   # Add ${p("includetest", "true")} to see test fakes and stubs")
        appendLine()
        appendLine("6. MIGRATE: Find external API usage for migration")
        appendLine("   ${u("find-usages", p("ownerClass", "kotlinx.datetime.LocalDate"), p("method", "getMonthNumber"))}")
        appendLine("   ${u("find-usages", p("type", "kotlinx.datetime.Instant"))}")
        appendLine("   # Find all call sites, field accesses, and type references to external types")
        appendLine()
        appendLine("7. ANALYZE: Git history behavioral analysis (no compilation needed)")
        appendLine("   ${u("hotspots")}                     # most-changed files")
        appendLine("   ${u("coupling")}                     # files that change together")
        appendLine("   ${u("code-age")}                          # time since last change per file")
        appendLine("   ${u("authors")}                      # distinct contributors per file")
        appendLine("   ${u("churn")}                        # lines added/deleted per file")
        appendLine("   # All git tasks accept ${p("after", "YYYY-MM-DD")} (default: 1 year ago)")
        appendLine()
        appendLine("8. RANK: Identify structurally important types")
        appendLine("   ${u("rank")}                         # top 50 most important types (PageRank)")
        appendLine("   ${u("rank", p("top", "20"))}                  # top 20")
        appendLine("   ${u("rank", p("projectonly", "false"))}        # include external types")
        appendLine()
        appendLine("9. DEAD CODE: Find unreferenced classes and methods")
        appendLine("   Kotlin noise (data class boilerplate, companion objects, coroutine inner classes) is auto-filtered.")
        appendLine("   ${u("dead")}                         # all potential dead code")
        appendLine("   ${u("dead", p("filter", "service"))}             # only show matches")
        appendLine("   ${u("dead", p("exclude", "\"Main|Test\""))}    # exclude entry points")
        appendLine("   ${u("dead", p("classes-only", "true"))}         # only unreferenced classes")
        appendLine()
        appendLine("10. COMPLEXITY: Measure class coupling (fan-in/fan-out)")
        appendLine("   ${u("complexity")}                        # all classes sorted by fan-out")
        appendLine("   ${u("complexity", p("classname", "Service"))}       # fan-in/out for matching classes")
        appendLine("   ${u("complexity", p("classname", "\".*Controller\""), p("detail", "true"))} # with call details")
        appendLine()
        appendLine("11. METRICS: Quick project health snapshot")
        appendLine("   ${u("metrics")}                       # summary: classes, packages, fan-in/out, cycles, dead code, hotspots")
        appendLine("   ${u("metrics", p("top", "10"))}                # top 10 hotspots")
        appendLine("   ${u("metrics", p("root-package", "com.example"))} # cycle detection scoped to package")
        appendLine()
        appendLine("--- Task Reference ---")
        appendLine()
        appendTaskReference(tool)
        appendLine()
        appendLine("--- Global Parameters ---")
        appendLine()
        appendGlobalParameters(tool)
        appendLine()
        appendLine("--- Tips for Optimal Results ---")
        appendLine()
        appendLine("- Always use ${p("llm", "true")}. It maximizes information per token with no decorative formatting.")
        appendLine("  Only omit ${p("llm", "true")} when you need to show output to a human user.")
        appendLine("- Use ${p("format", "json")} only when you need structured data for calculations or aggregations.")
        appendLine("- Use ${p("projectonly", "true")} to cut noise. External call chains are rarely useful.")
        appendLine("- Patterns are Java regex, case-insensitive. Use .* for wildcards.")
        appendLine("- ${t("package-deps")} is the fastest way to understand module/package structure.")
        appendLine("- ${t("class-detail")} is cheaper than reading a source file when you only need the API surface.")
        appendLine("- Chain tasks: find a class, inspect it, then trace its callers — each step narrows context.")
        appendLine("- Results are cached on disk. Repeated runs are fast.")
        appendLine("- Run ${u("help")} for full parameter documentation.")
        appendLine()
        appendLine("--- JSON Schemas ---")
        appendLine()
        appendLine("${t("list-classes")} / ${t("find-class")}:")
        appendLine("  [{\"className\": \"com.example.Foo\", \"sourceFile\": \"Foo.kt\", \"sourcePath\": \"com/example/Foo.kt\"}]")
        appendLine()
        appendLine("${t("find-symbol")}:")
        appendLine("  [{\"package\": \"com.example\", \"class\": \"Service\", \"symbol\": \"doWork\", \"kind\": \"method\", \"sourceFile\": \"Service.kt\"}]")
        appendLine()
        appendLine("${t("class-detail")}:")
        appendLine("  [{\"className\": \"...\", \"sourceFile\": \"...\", \"superClass\": \"...\",")
        appendLine("    \"interfaces\": [\"...\"], \"fields\": [{\"name\": \"...\", \"type\": \"...\"}],")
        appendLine("    \"methods\": [{\"name\": \"...\", \"parameters\": [\"...\"], \"returnType\": \"...\"}]}]")
        appendLine()
        appendLine("${t("find-callers")} / ${t("find-callees")}:")
        appendLine("  [{\"method\": \"com.example.Service.doWork\", \"sourceFile\": \"Service.kt\",")
        appendLine("    \"children\": [{\"method\": \"...\", \"sourceFile\": \"...\", \"children\": [...]}]}]")
        appendLine()
        appendLine("${t("find-interfaces")}:")
        appendLine("  [{\"interface\": \"com.example.Repository\",")
        appendLine("    \"implementors\": [{\"className\": \"com.example.SqlRepo\", \"sourceFile\": \"SqlRepo.kt\"}]}]")
        appendLine()
        appendLine("${t("package-deps")}:")
        appendLine("  [{\"package\": \"com.example.services\", \"dependencies\": [\"com.example.domain\"]}]")
        appendLine("  With ${p("reverse", "true")}: \"dependents\" replaces \"dependencies\"")
        appendLine()
        appendLine("${t("find-usages")}:")
        appendLine("  [{\"callerClass\": \"com.example.MyService\", \"callerMethod\": \"process\",")
        appendLine("    \"targetOwner\": \"kotlinx.datetime.LocalDate\", \"targetMethod\": \"getMonthNumber\",")
        appendLine("    \"kind\": \"method_call\", \"sourceFile\": \"MyService.kt\"}]")
        appendLine()
        appendLine("${t("rank")}:")
        appendLine("  [{\"className\": \"com.example.Core\", \"rank\": 0.42, \"inDegree\": 5, \"outDegree\": 2}]")
        appendLine()
        appendLine("${t("dead")}:")
        appendLine("  [{\"className\": \"com.example.Orphan\", \"kind\": \"class\", \"sourceFile\": \"Orphan.kt\"},")
        appendLine("   {\"className\": \"com.example.Service\", \"memberName\": \"unused\", \"kind\": \"method\", \"sourceFile\": \"Service.kt\"}]")
        appendLine()
        appendLine("${t("complexity")}:")
        appendLine("  [{\"className\": \"com.example.Service\", \"sourceFile\": \"Service.kt\",")
        appendLine("    \"fanOut\": 12, \"fanIn\": 5, \"distinctOutgoingClasses\": 4, \"distinctIncomingClasses\": 3,")
        appendLine("    \"outgoingByClass\": [{\"className\": \"com.example.Repo\", \"callCount\": 5}],")
        appendLine("    \"incomingByClass\": [{\"className\": \"com.example.Controller\", \"callCount\": 3}]}]")
        appendLine()
        appendLine("${t("cycles")}:")
        appendLine("  [{\"packages\": [\"api\", \"service\"],")
        appendLine("    \"edges\": [{\"from\": \"api\", \"to\": \"service\",")
        appendLine("      \"classEdges\": [{\"source\": \"api.Controller\", \"target\": \"service.Service\"}]}]}]")
        appendLine()
        appendLine("${t("metrics")}:")
        appendLine("  {\"totalClasses\": 42, \"packageCount\": 8, \"averageFanIn\": 3.2, \"averageFanOut\": 5.1,")
        appendLine("   \"cycleCount\": 1, \"deadClassCount\": 3, \"deadMethodCount\": 7,")
        appendLine("   \"topHotspots\": [{\"file\": \"Service.kt\", \"revisions\": 25, \"totalChurn\": 340}]}")
        appendLine()
        appendLine("--- Extracting Output ---")
        appendLine()
        val buildToolNote = when (tool) {
            BuildTool.GRADLE -> "When using ${p("llm", "true")} or ${p("format", "json")}, Gradle mixes its own output (task headers,\nwarnings, build status) into stdout. To handle this, output is wrapped with markers:"
            BuildTool.MAVEN -> "When using ${p("llm", "true")} or ${p("format", "json")}, Maven may mix its own output\ninto stdout. To handle this, output is wrapped with markers:"
        }
        appendLine(buildToolNote)
        appendLine()
        appendLine("  ---CNAV_BEGIN---")
        appendLine("  [{\"className\":\"...\"}]")
        appendLine("  ---CNAV_END---")
        appendLine()
        appendLine("Extract the JSON between markers using sed:")
        appendLine("  ${u("list-classes", p("format", "json"))} 2>/dev/null | sed -n '/---CNAV_BEGIN---/,/---CNAV_END---/{//!p;}'")
        appendLine()
        appendLine("Or define a shell helper to reduce noise:")
        val helperName = "cnav"
        val helperExample = when (tool) {
            BuildTool.GRADLE -> "  $helperName() { ./gradlew \"\$@\" 2>/dev/null | sed -n '/---CNAV_BEGIN---/,/---CNAV_END---/{//!p;}'; }"
            BuildTool.MAVEN -> "  $helperName() { mvn \"\$@\" 2>/dev/null | sed -n '/---CNAV_BEGIN---/,/---CNAV_END---/{//!p;}'; }"
        }
        appendLine(helperExample)
        appendLine()
        appendLine("--- jq Examples (using $helperName helper) ---")
        appendLine()
        appendLine("Extract class names:")
        appendLine("  $helperName ${t("list-classes")} ${p("format", "json")} | jq '.[].className'")
        appendLine()
        appendLine("Find source file for a class:")
        appendLine("  $helperName ${t("find-class")} ${p("pattern", "Service")} ${p("format", "json")} | jq '.[].sourcePath'")
        appendLine()
        appendLine("Get caller method names:")
        appendLine("  $helperName ${t("find-callers")} ${p("method", "save")} ${p("maxdepth", "3")} ${p("format", "json")} | jq '.[].children[].method'")
        appendLine()
        appendLine("List packages that depend on domain:")
        appendLine("  $helperName ${t("package-deps")} ${p("package", "domain")} ${p("reverse", "true")} ${p("format", "json")} | jq '.[].dependents[]'")
        appendLine()
        appendLine("Get interface implementor class names:")
        appendLine("  $helperName ${t("find-interfaces")} ${p("pattern", "Repository")} ${p("format", "json")} | jq '.[].implementors[].className'")
    }

    private val HELP_GOALS = setOf("help", "agent-help", "config-help")
    private val FORMAT_PARAM_NAMES = setOf("format", "llm")

    private fun StringBuilder.appendTaskReference(tool: BuildTool) {
        val compilationTasks = TaskRegistry.ALL_TASKS
            .filter { it.requiresCompilation && it.goal !in HELP_GOALS }
        val gitTasks = TaskRegistry.ALL_TASKS
            .filter { !it.requiresCompilation && it.goal !in HELP_GOALS }
        val helpTasks = TaskRegistry.ALL_TASKS
            .filter { it.goal in HELP_GOALS }

        for (task in compilationTasks) {
            appendTaskLine(task, tool)
        }
        appendLine()
        appendLine("  Git history (no compilation needed):")
        for (task in gitTasks) {
            appendTaskLine(task, tool)
        }
        appendLine()
        appendLine("  Help:")
        for (task in helpTasks) {
            appendLine("  ${padTo(task.taskName(tool), 22)}${task.description}")
        }
    }

    private fun StringBuilder.appendTaskLine(task: TaskDef, tool: BuildTool) {
        val taskName = task.taskName(tool)
        val nonFormatParams = task.params.filter { it.name !in FORMAT_PARAM_NAMES }
        val paramStr = nonFormatParams.joinToString(" ") { param ->
            val rendered = param.render(tool)
            if (param.defaultValue != null) "[$rendered]" else rendered
        }
        val line = if (paramStr.isEmpty()) {
            "  ${padTo(taskName, 22)}${task.description}"
        } else {
            "  ${padTo(taskName, 22)}${padTo(task.description, 36)}$paramStr"
        }
        appendLine(line)
    }

    private fun StringBuilder.appendGlobalParameters(tool: BuildTool) {
        appendLine("  ${padTo(TaskRegistry.LLM.render(tool), 28)}Compact, token-efficient output (all tasks)")
        appendLine("  ${padTo(TaskRegistry.FORMAT.render(tool), 28)}Machine-readable JSON output (all tasks)")
        appendLine()

        val tasksByParam = buildMap<String, MutableList<TaskDef>> {
            for (task in TaskRegistry.ALL_TASKS) {
                for (param in task.params) {
                    if (param.name !in FORMAT_PARAM_NAMES) {
                        getOrPut(param.name) { mutableListOf() }.add(task)
                    }
                }
            }
        }

        val allParams = TaskRegistry.ALL_TASKS
            .flatMap { it.params }
            .filter { it.name !in FORMAT_PARAM_NAMES }
            .distinctBy { it.name }

        for (param in allParams) {
            val tasks = tasksByParam[param.name] ?: continue
            val rendered = param.render(tool)
            val taskList = tasks.joinToString(", ") { it.taskName(tool) }
            val defaultSuffix = param.defaultValue?.let { ", default: $it" } ?: ""
            appendLine("  ${padTo(rendered, 28)}${param.description} ($taskList$defaultSuffix)")
        }
    }

    private fun padTo(s: String, width: Int): String =
        if (s.length >= width) "$s " else s.padEnd(width)
}
