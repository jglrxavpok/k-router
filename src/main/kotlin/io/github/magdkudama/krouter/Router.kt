package io.github.magdkudama.krouter

import java.lang.Exception

interface RouteResponse

class InvalidRouteResponse(val path: String, val method: Method, val message: String): RouteResponse

open class Router(val matcher: RouteMatcher) {

    fun route(path: String, method: Method, context: Any): RouteResponse {
        return try {
            val result = matcher.match(path, method)
            if(result.route.handler == null) {
                throw RouteNotFoundException(path)
            }
            result.route.handler.invoke(context, result.pathParams)
        } catch (e: Exception) {
            invalidRoute(path, method, e.message!!)
        }
    }

    private fun invalidRoute(path: String, method: Method, message: String): RouteResponse {
        return InvalidRouteResponse(path, method, message)
    }
}