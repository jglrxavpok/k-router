package io.github.magdkudama.krouter

import io.github.magdkudama.krouter.Method.*
import org.junit.Test
import org.junit.jupiter.api.Assertions
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RouteMatcherTest {
    @Test
    fun `static matching with single route`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route", "/foo", mapOf(), setOf(GET))
            )
        )

        assertEquals("route", matcher.match("/foo", GET).name)
    }

    @Test
    fun `static matching with multiple routes with same path but different method`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo", mapOf(), setOf(GET)),
                Route("route2", "/foo", mapOf(), setOf(POST))
            )
        )

        assertEquals("route1", matcher.match("/foo", GET).name)
    }

    @Test
    fun `one of the routes is chosen when two routes match`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo", mapOf(), setOf(GET)),
                Route("route2", "/foo", mapOf(), setOf(GET))
            )
        )

        assert(listOf("route1", "route2").contains(matcher.match("/foo", GET).name))
    }

    @Test
    fun `GET and HEAD methods are equivalent`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo", mapOf(), setOf(POST)),
                Route("route2", "/foo", mapOf(), setOf(HEAD))
            )
        )

        assertEquals("route2", matcher.match("/foo", HEAD).name)
    }


    @Test
    fun `dynamic route parameters`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo/{id}/bar", mapOf(), setOf(GET)),
                Route("route2", "/foo", mapOf(), setOf(HEAD))
            )
        )

        assertEquals("3", matcher.match("/foo/3/bar", GET).pathParams!!["id"])
    }

    @Test
    fun `dynamic route parameters with requirements (found and not found)`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo/{id}/bar", mapOf("id" to "\\d+"), setOf(GET)),
                Route("route2", "/foo", mapOf(), setOf(HEAD))
            )
        )

        assertEquals("3", matcher.match("/foo/3/bar", GET).pathParams!!["id"])

        val thrown = Assertions.assertThrows(
            RouteNotFoundException::class.java
        ) { matcher.match("/foo/abcd/bar", GET) }

        assertTrue(thrown.message!!.contains("Route '/foo/abcd/bar' has not been found"))
    }

    @Test
    fun `multiple dynamic routes with different requirements but same path`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo/{id}/bar", mapOf("id" to "\\d+"), setOf(GET)),
                Route("route2", "/foo/{id}/bar", mapOf("id" to "\\w+"), setOf(GET))
            )
        )

        assertEquals("route1", matcher.match("/foo/3/bar", GET).name)
        assertEquals("route2", matcher.match("/foo/abcd/bar", GET).name)
    }

    @Test
    fun `dynamic route parameters are populated`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo/{id1}/{id2}/bar", mapOf(), setOf(GET))
            )
        )

        val match = matcher.match("/foo/3/4/bar", GET)
        assertEquals("route1", match.name)
        assertNotNull(match.pathParams)

        assertEquals("3", match.pathParams!!["id1"])
        assertEquals("4", match.pathParams!!["id2"])
    }

    @Test
    fun `dynamic route with multiple parameters`() {
        val matcher = RouteMatcher(
            setOf(
                Route("route1", "/foo/{foo}/bar/{bar}", mapOf("foo" to "\\d+", "bar" to "[a-z]+"), setOf(GET))
            )
        )

        assertEquals("route1", matcher.match("/foo/3/bar/test", GET).name)

        val thrown = Assertions.assertThrows(
            RouteNotFoundException::class.java
        ) { matcher.match("/foo/3/bar/3", GET) }

        assertTrue(thrown.message!!.contains("Route '/foo/3/bar/3' has not been found"))
    }
}
