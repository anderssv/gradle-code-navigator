package no.f12.codenavigator.navigation

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * A resolved annotation with its descriptor and collected parameters.
 */
data class ResolvedAnnotation(val descriptor: String, val parameters: Map<String, String>)

/**
 * Creates an [AnnotationVisitor] that collects annotation parameters and
 * automatically unwraps JVM repeatable-annotation containers.
 *
 * A repeatable container is detected when the annotation has exactly one
 * parameter named `value` that is an array of nested annotations all sharing
 * the same descriptor, and no other parameters are present.
 *
 * When a container is detected, [onAnnotations] receives one [ResolvedAnnotation]
 * per nested annotation (using the nested descriptor). Otherwise it receives
 * a single [ResolvedAnnotation] with the outer descriptor and stringified parameters.
 */
fun unwrappingAnnotationVisitor(
    outerDescriptor: String,
    onAnnotations: (List<ResolvedAnnotation>) -> Unit,
): AnnotationVisitor {
    val parameters = mutableMapOf<String, String>()
    var nestedAnnotations: MutableList<ResolvedAnnotation>? = null
    var hasNonValueParams = false

    return object : AnnotationVisitor(Opcodes.ASM9) {
        override fun visit(paramName: String?, value: Any?) {
            if (paramName != null && value != null) {
                parameters[paramName] = formatValue(value)
                if (paramName != "value") hasNonValueParams = true
            }
        }

        override fun visitEnum(paramName: String?, descriptor: String, value: String) {
            if (paramName != null) {
                val enumClass = typeSimpleName(descriptor)
                parameters[paramName] = "$enumClass.$value"
                if (paramName != "value") hasNonValueParams = true
            }
        }

        override fun visitArray(paramName: String?): AnnotationVisitor? {
            if (paramName == null) return null
            val elements = mutableListOf<String>()
            val structuredNested = if (paramName == "value") mutableListOf<ResolvedAnnotation>() else null

            return object : AnnotationVisitor(Opcodes.ASM9) {
                override fun visit(name: String?, value: Any?) {
                    if (value != null) {
                        elements.add(formatValue(value))
                    }
                }

                override fun visitEnum(name: String?, descriptor: String, value: String) {
                    val enumClass = typeSimpleName(descriptor)
                    elements.add("$enumClass.$value")
                }

                override fun visitAnnotation(name: String?, descriptor: String): AnnotationVisitor {
                    val nestedName = typeSimpleName(descriptor)
                    val nestedParams = mutableMapOf<String, String>()
                    return object : AnnotationVisitor(Opcodes.ASM9) {
                        override fun visit(n: String?, value: Any?) {
                            if (n != null && value != null) {
                                nestedParams[n] = formatValue(value)
                            }
                        }

                        override fun visitEnum(n: String?, descriptor: String, value: String) {
                            if (n != null) {
                                val enumClass = typeSimpleName(descriptor)
                                nestedParams[n] = "$enumClass.$value"
                            }
                        }

                        override fun visitEnd() {
                            val paramStr = if (nestedParams.isEmpty()) {
                                "@$nestedName"
                            } else {
                                val entries = nestedParams.entries.joinToString(", ") { "${it.key}=${it.value}" }
                                "@$nestedName($entries)"
                            }
                            elements.add(paramStr)
                            structuredNested?.add(ResolvedAnnotation(descriptor, nestedParams.toMap()))
                        }
                    }
                }

                override fun visitEnd() {
                    parameters[paramName] = when (elements.size) {
                        0 -> "[]"
                        1 -> elements.first()
                        else -> "[${elements.joinToString(", ")}]"
                    }
                    if (paramName == "value" && structuredNested != null && structuredNested.isNotEmpty()) {
                        nestedAnnotations = structuredNested
                    }
                }
            }
        }

        override fun visitAnnotation(paramName: String?, descriptor: String): AnnotationVisitor? {
            if (paramName == null) return null
            if (paramName != "value") hasNonValueParams = true
            val nestedName = typeSimpleName(descriptor)
            val nestedParams = mutableMapOf<String, String>()
            return object : AnnotationVisitor(Opcodes.ASM9) {
                override fun visit(name: String?, value: Any?) {
                    if (name != null && value != null) {
                        nestedParams[name] = formatValue(value)
                    }
                }

                override fun visitEnd() {
                    val paramStr = if (nestedParams.isEmpty()) {
                        "@$nestedName"
                    } else {
                        val entries = nestedParams.entries.joinToString(", ") { "${it.key}=${it.value}" }
                        "@$nestedName($entries)"
                    }
                    parameters[paramName] = paramStr
                }
            }
        }

        override fun visitEnd() {
            val nested = nestedAnnotations
            if (!hasNonValueParams && nested != null && nested.isNotEmpty() && isRepeatableContainer(outerDescriptor, nested)) {
                onAnnotations(nested)
            } else {
                onAnnotations(listOf(ResolvedAnnotation(outerDescriptor, parameters.toMap())))
            }
        }
    }
}

/**
 * A repeatable container has nested annotations in its `value` array that
 * all share the same descriptor, AND the container's simple name contains
 * the nested annotation's simple name (JVM repeatable containers follow
 * naming conventions like `RepeatedFoo` for `@Foo`, `Schedules` for `@Scheduled`).
 *
 * This distinguishes genuine repeatable containers from composite annotations
 * like `@And(value=[@Spec, @Spec])` where the outer name does not relate to the inner.
 */
private fun isRepeatableContainer(outerDescriptor: String, nested: List<ResolvedAnnotation>): Boolean {
    val distinctDescriptors = nested.map { it.descriptor }.distinct()
    if (distinctDescriptors.size != 1) return false
    val outerSimple = typeSimpleName(outerDescriptor).lowercase()
    val nestedSimple = typeSimpleName(distinctDescriptors.single()).lowercase()
    return outerSimple.contains(nestedSimple)
}

private fun typeSimpleName(descriptor: String): String =
    Type.getType(descriptor).className.substringAfterLast('.')

private fun formatValue(value: Any): String =
    when (value) {
        is Type -> value.className.substringAfterLast('.')
        else -> value.toString()
    }
