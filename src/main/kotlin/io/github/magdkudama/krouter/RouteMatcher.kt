package io.github.magdkudama.krouter

import java.util.regex.Pattern

data class RouteMatch(val route: Route, val pathParams: Map<String, String> = emptyMap())

class RouteMatcher(vararg routes: Route) {
    private val dynamicRoutes: MutableList<Route> = mutableListOf()
    private val dynamicRoutesPattern: Pattern
    private val dynamicRoutesRegexGroupPositions: MutableMap<Int, List<Int>> = mutableMapOf()
    private val staticRoutes = mutableMapOf<String, MutableMap<Int, Route>>()

    init {
        val patterns = mutableListOf<String>()

        for (route in routes) {
            if (!route.dynamic) {
                if (route.path !in staticRoutes) staticRoutes[route.path] = mutableMapOf()

                for (method in route.methodKeys) {
                    staticRoutes[route.path]?.put(method, route)
                }
            } else {
                dynamicRoutes.add(route)
                var path = route.path

                route.requirements.forEach { pair -> path = path.replace("{${pair.first}}", "(${pair.second})") }
                patterns.add(path)
            }
        }

        dynamicRoutesPattern = Pattern.compile("^(${patterns.joinToString("|")})$")

        var i = 2
        for (route in dynamicRoutes) {
            val positions = mutableListOf<Int>()
            route.requirements.forEach { _ ->
                positions.add(i)
                i++
            }
            dynamicRoutesRegexGroupPositions[dynamicRoutes.indexOf(route)] = positions
        }
    }

    @Throws(RouteNotFoundException::class)
    fun match(path: String, method: Method): RouteMatch {
        if (path in staticRoutes) {
            val methods = staticRoutes.getValue(path)
            if (method.key in methods) {
                return RouteMatch(methods.getValue(method.key))
            } else {
                throw RouteNotFoundException("Route '$path' has not been found")
            }
        }

        val matcher = dynamicRoutesPattern.matcher(path)
        while (matcher.find()) {
            for ((routeId, groups) in dynamicRoutesRegexGroupPositions) {
                val route = dynamicRoutes[routeId]

                if (method.key !in route.methodKeys) continue

                var allOk = true
                val pathParams = mutableMapOf<String, String>()
                for (group in groups) {
                    if (matcher.group(group) == null) {
                        allOk = false
                        break
                    } else pathParams[route.requirements[groups.indexOf(group)].first] = matcher.group(group)
                }

                if (allOk) {
                    return RouteMatch(route, pathParams)
                }
            }
        }

        throw RouteNotFoundException("Route '$path' has not been found")
    }
}
