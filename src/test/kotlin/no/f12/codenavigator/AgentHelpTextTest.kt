package no.f12.codenavigator

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class AgentHelpTextTest {

    @Test
    fun `Gradle agent help text contains all task names with Gradle names`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        val helpGoals = setOf("help", "agent-help", "config-help")
        for (task in TaskRegistry.ALL_TASKS.filter { it.goal !in helpGoals }) {
            assertTrue(
                text.contains(task.taskName(BuildTool.GRADLE)),
                "Should contain ${task.taskName(BuildTool.GRADLE)} (goal: ${task.goal})",
            )
        }
    }

    @Test
    fun `Maven agent help text contains all task names with Maven names`() {
        val text = AgentHelpText.generate(BuildTool.MAVEN)

        val helpGoals = setOf("help", "agent-help", "config-help")
        for (task in TaskRegistry.ALL_TASKS.filter { it.goal !in helpGoals }) {
            assertTrue(
                text.contains(task.taskName(BuildTool.MAVEN)),
                "Should contain ${task.taskName(BuildTool.MAVEN)} (goal: ${task.goal})",
            )
        }
    }

    @Test
    fun `Gradle agent help text uses -P parameters and gradlew command`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains(TaskRegistry.LLM.render(BuildTool.GRADLE)))
        assertTrue(text.contains(TaskRegistry.FORMAT.render(BuildTool.GRADLE)))
        assertTrue(text.contains("./gradlew"))
    }

    @Test
    fun `Maven agent help text uses -D parameters and mvn command`() {
        val text = AgentHelpText.generate(BuildTool.MAVEN)

        assertTrue(text.contains(TaskRegistry.LLM.render(BuildTool.MAVEN)))
        assertTrue(text.contains(TaskRegistry.FORMAT.render(BuildTool.MAVEN)))
        assertTrue(text.contains("mvn"))
        assertFalse(text.contains("./gradlew"), "Maven agent help should not contain ./gradlew")
        assertFalse(
            text.contains(TaskRegistry.LLM.render(BuildTool.GRADLE)),
            "Maven agent help should not contain -P params",
        )
    }

    @Test
    fun `agent help text includes workflow guidance`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("ORIENT"))
        assertTrue(text.contains("FIND"))
        assertTrue(text.contains("INSPECT"))
        assertTrue(text.contains("TRACE"))
        assertTrue(text.contains("MAP"))
        assertTrue(text.contains("ANALYZE"))
    }

    @Test
    fun `agent help text includes performance tips`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("Tips for Optimal Results"))
        assertTrue(text.contains("cached"))
    }

    @Test
    fun `Gradle agent help text includes all parameter renders`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        val paramsToCheck = listOf(
            TaskRegistry.PATTERN,
            TaskRegistry.METHOD,
            TaskRegistry.MAXDEPTH,
            TaskRegistry.PROJECTONLY,
            TaskRegistry.TOP,
            TaskRegistry.AFTER,
            TaskRegistry.MIN_REVS,
            TaskRegistry.NO_FOLLOW,
        )
        for (param in paramsToCheck) {
            assertTrue(
                text.contains(param.render(BuildTool.GRADLE)),
                "Should contain ${param.render(BuildTool.GRADLE)} (param: ${param.name})",
            )
        }
    }

    @Test
    fun `Maven agent help text includes all parameter renders`() {
        val text = AgentHelpText.generate(BuildTool.MAVEN)

        val paramsToCheck = listOf(
            TaskRegistry.PATTERN,
            TaskRegistry.METHOD,
            TaskRegistry.MAXDEPTH,
            TaskRegistry.PROJECTONLY,
            TaskRegistry.TOP,
            TaskRegistry.AFTER,
            TaskRegistry.MIN_REVS,
            TaskRegistry.NO_FOLLOW,
        )
        for (param in paramsToCheck) {
            assertTrue(
                text.contains(param.render(BuildTool.MAVEN)),
                "Should contain ${param.render(BuildTool.MAVEN)} (param: ${param.name})",
            )
        }
    }

    @Test
    fun `agent help text documents JSON schemas for each task`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("className"), "Should document className field")
        assertTrue(text.contains("sourceFile"), "Should document sourceFile field")
        assertTrue(text.contains("sourcePath"), "Should document sourcePath field")
        assertTrue(text.contains("\"method\""), "Should document method field in call trees")
        assertTrue(text.contains("\"children\""), "Should document children field in call trees")
        assertTrue(text.contains("\"interface\""), "Should document interface field")
        assertTrue(text.contains("\"implementors\""), "Should document implementors field")
        assertTrue(text.contains("\"dependencies\""), "Should document dependencies field")
        assertTrue(text.contains("\"dependents\""), "Should document dependents field")
    }

    @Test
    fun `agent help text includes jq examples`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("jq"), "Should mention jq")
        assertTrue(text.contains("| jq"), "Should show pipe to jq")
    }

    @Test
    fun `Gradle jq examples use gradlew command`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        val listClassesTask = TaskRegistry.LIST_CLASSES.taskName(BuildTool.GRADLE)
        assertTrue(text.contains("./gradlew $listClassesTask"))
    }

    @Test
    fun `Maven jq examples use mvn command`() {
        val text = AgentHelpText.generate(BuildTool.MAVEN)

        val listClassesTask = TaskRegistry.LIST_CLASSES.taskName(BuildTool.MAVEN)
        assertTrue(text.contains("mvn $listClassesTask"))
    }

    @Test
    fun `agent help text documents JSON schema for find-usages`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("\"targetOwner\""), "Should document targetOwner field")
        assertTrue(text.contains("\"targetMethod\""), "Should document targetMethod field")
    }

    @Test
    fun `agent help text mentions migration workflows`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("migrat"), "Should mention migration use case")
    }

    @Test
    fun `default parameter is GRADLE for backward compatibility`() {
        val defaultText = AgentHelpText.generate()
        val gradleText = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(defaultText == gradleText, "Default should produce same output as explicit GRADLE")
    }

    @Test
    fun `agent help text emphasizes one-shot accuracy over iterative grep`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("single"), "Should mention getting results in a single call")
        assertTrue(text.contains("tool call"), "Should frame benefit in terms of tool call round-trips")
        assertTrue(text.contains("correct on the first"), "Should emphasize correctness on first response")
        assertTrue(text.contains("No iterative"), "Should explicitly state no iterative searching needed")
    }

    @Test
    fun `agent help text includes when-to-use guidance`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("Use code-navigator when"), "Should have when-to-use-cnav section")
        assertTrue(text.contains("Use grep"), "Should have when-to-use-grep section")
    }

    @Test
    fun `Maven extracting output section uses mvn command`() {
        val text = AgentHelpText.generate(BuildTool.MAVEN)

        val listClassesTask = TaskRegistry.LIST_CLASSES.taskName(BuildTool.MAVEN)
        assertTrue(text.contains("mvn $listClassesTask"), "Maven extraction example should use mvn command")
        assertFalse(
            text.contains("./gradlew ${TaskRegistry.LIST_CLASSES.taskName(BuildTool.GRADLE)}"),
            "Maven should not contain Gradle extraction examples",
        )
    }

    @Test
    fun `agent help text includes metrics in task reference`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(
            text.contains(TaskRegistry.METRICS.taskName(BuildTool.GRADLE)),
            "Task reference should list metrics task",
        )
        assertTrue(text.contains("health snapshot"), "Should describe metrics purpose")
    }

    @Test
    fun `agent help text documents JSON schema for metrics`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        assertTrue(text.contains("\"totalClasses\""), "Should document totalClasses field")
        assertTrue(text.contains("\"packageCount\""), "Should document packageCount field")
        assertTrue(text.contains("\"averageFanIn\""), "Should document averageFanIn field")
        assertTrue(text.contains("\"cycleCount\""), "Should document cycleCount field")
    }

    @Test
    fun `task reference lists every non-help task from TaskRegistry`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)
        val taskReferenceSection = text.substringAfter("--- Task Reference ---")
            .substringBefore("--- Global Parameters ---")

        val helpGoals = setOf("help", "agent-help", "config-help")
        val expectedTasks = TaskRegistry.ALL_TASKS.filter { it.goal !in helpGoals }

        for (task in expectedTasks) {
            val taskName = task.taskName(BuildTool.GRADLE)
            assertTrue(
                taskReferenceSection.contains(taskName),
                "Task Reference should list $taskName (goal: ${task.goal})",
            )
        }
    }

    @Test
    fun `task reference shows all non-format params for each task`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)
        val taskReferenceSection = text.substringAfter("--- Task Reference ---")
            .substringBefore("--- Global Parameters ---")

        val globalParamNames = setOf("format", "llm")
        val helpGoals = setOf("help", "agent-help", "config-help")

        for (task in TaskRegistry.ALL_TASKS.filter { it.goal !in helpGoals }) {
            val taskName = task.taskName(BuildTool.GRADLE)
            val nonGlobalParams = task.params.filter { it.name !in globalParamNames }

            for (param in nonGlobalParams) {
                val rendered = param.render(BuildTool.GRADLE)
                assertTrue(
                    taskReferenceSection.contains(rendered),
                    "Task Reference should contain $rendered for $taskName (param: ${param.name})",
                )
            }
        }
    }

    @Test
    fun `every non-format param from TaskRegistry appears somewhere in agent help output`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)

        val globalParamNames = setOf("format", "llm")

        val allParams = TaskRegistry.ALL_TASKS
            .flatMap { it.params }
            .filter { it.name !in globalParamNames }
            .distinctBy { it.name }

        val missing = allParams.filter { param ->
            !text.contains(param.render(BuildTool.GRADLE))
        }

        assertEquals(
            emptyList(),
            missing.map { "${it.name} (${it.render(BuildTool.GRADLE)})" },
            "All TaskRegistry params should appear in agent help output",
        )
    }

    @Test
    fun `global parameters section includes params shared across tasks`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)
        val globalSection = text.substringAfter("--- Global Parameters ---")
            .substringBefore("--- Tips")

        val sharedParams = listOf(
            TaskRegistry.MAXDEPTH,
            TaskRegistry.PROJECTONLY,
            TaskRegistry.AFTER,
            TaskRegistry.TOP,
        )
        for (param in sharedParams) {
            assertTrue(
                globalSection.contains(param.render(BuildTool.GRADLE)),
                "Global section should list ${param.name}",
            )
        }
    }

    @Test
    fun `global parameters section shows default values from TaskRegistry`() {
        val text = AgentHelpText.generate(BuildTool.GRADLE)
        val globalSection = text.substringAfter("--- Global Parameters ---")
            .substringBefore("--- Tips")

        val paramsWithDefaults = listOf(TaskRegistry.MAXDEPTH, TaskRegistry.TOP)
        for (param in paramsWithDefaults) {
            assertTrue(
                globalSection.contains("default: ${param.defaultValue}"),
                "Global section should show default for ${param.name}: ${param.defaultValue}",
            )
        }
    }

    @Test
    fun `Maven task reference uses Maven task names and -D params`() {
        val text = AgentHelpText.generate(BuildTool.MAVEN)
        val taskReferenceSection = text.substringAfter("--- Task Reference ---")
            .substringBefore("--- Global Parameters ---")

        val callersTask = TaskRegistry.FIND_CALLERS.taskName(BuildTool.MAVEN)
        val methodParam = TaskRegistry.METHOD.render(BuildTool.MAVEN)
        val gradleMethodParam = TaskRegistry.METHOD.render(BuildTool.GRADLE)

        assertTrue(taskReferenceSection.contains(callersTask), "Should use Maven task names")
        assertTrue(taskReferenceSection.contains(methodParam), "Should use -D params")
        assertFalse(taskReferenceSection.contains(gradleMethodParam), "Should not use -P params")
    }
}
