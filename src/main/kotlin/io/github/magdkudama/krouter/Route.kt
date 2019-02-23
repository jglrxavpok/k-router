package io.github.magdkudama.krouter

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Represents a HTTP method.
 *
 * The key is used to map verbs that are equivalent. For example, HEAD and GET act as the same verb, even though a HEAD's
 * response should not carry any body.
 */
enum class Method(val key: Int) {
    GET(1),
    HEAD(1),
    POST(2),
    PUT(3),
    PATCH(3),
    DELETE(4)
}

class Route(val name: String, val path: String, req: Map<String, String>, private val methods: Set<Method>) {
    companion object {
        @JvmStatic
        val PATTERN: Pattern = Pattern.compile("\\{(\\w+)}")
        @JvmStatic
        val ANY_PARAMETER: String = ".*"
    }

    val methodKeys: Set<Int> get() = methods.map { it.key }.toSet()
    val dynamic: Boolean
    val requirements: List<Pair<String, String>> = mutableListOf()

    init {
        if (!path.startsWith("/")) {
            throw InvalidRouteDefinitionException("Route '$name' path should start with '/'. In this case: '/$path'")
        }

        if (methods.isEmpty()) {
            throw InvalidRouteDefinitionException("Route '$name' should define valid methods")
        }

        val matcher = PATTERN.matcher(path)

        requirements as MutableList

        while (matcher.find()) {
            val match = matcher.group(1)
            val regex = req[match] ?: ANY_PARAMETER

            try {
                Pattern.compile(regex)
            } catch (ex: PatternSyntaxException) {
                throw InvalidRouteDefinitionException(
                    "Requirement for route '$name', match '$match' ('$regex') is invalid",
                    ex
                )
            }

            // This would fail with some complex expressions, but who cares?

            if (regex.matches(Regex("\\([^?:].*\\)"))) {
                throw InvalidRouteDefinitionException("Route '$name' should not be capturing groups in the requirements map")
            }

            requirements.add(Pair(match, regex))
        }

        dynamic = !requirements.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Route

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
