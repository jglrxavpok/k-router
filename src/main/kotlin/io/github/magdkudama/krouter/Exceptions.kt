package io.github.magdkudama.krouter

class InvalidRouteDefinitionException : IllegalArgumentException {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}

class RouteNotFoundException(message: String) : RuntimeException(message)
