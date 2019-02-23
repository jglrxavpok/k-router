package io.github.magdkudama.krouter

import io.github.magdkudama.krouter.Method.GET
import org.junit.Test
import org.junit.jupiter.api.Assertions
import kotlin.test.assertTrue

class RouteTest {
    @Test
    fun `capturing group in requirements throws exception`() {
        val thrown = Assertions.assertThrows(
            InvalidRouteDefinitionException::class.java
        ) { Route("route", "/foo/{id}", mapOf("id" to "(\\d+)"), setOf(GET)) }

        assertTrue(thrown.message!!.contains("Route 'route' should not be capturing groups in the requirements map"))
    }

    @Test
    fun `non capturing group in requirements is valid`() {
        Assertions.assertDoesNotThrow { Route("route", "/foo/{id}", mapOf("id" to "(?:\\d+)"), setOf(GET)) }
    }

    @Test
    fun `invalid regex in requirement throws exception`() {
        val thrown = Assertions.assertThrows(
            InvalidRouteDefinitionException::class.java
        ) { Route("route", "/foo/{id}", mapOf("id" to "[a-z"), setOf(GET)) }

        assertTrue(thrown.message!!.contains("Requirement for route 'route', match 'id' ('[a-z') is invalid"))
    }
}
