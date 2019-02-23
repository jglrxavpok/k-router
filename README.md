# k-router

A simple (and fast?) HTTP router for the JVM, written in Kotlin.

#### How does it work?

This principle is not new, and I've known it for some time.

Read [this](http://nikic.github.io/2014/02/18/Fast-request-routing-using-regular-expressions.html) amazing blog post by [Nikita Popov](https://twitter.com/nikita_ppv) explaining how it works internally, and why it's a fast method.

Basically, for every request, instead of matching each route using a regular expression, it uses a single regular expressions for all possible routes, making it much faster (I'm claiming this without even writing a single test for it, so you can trust me on it).

#### Example usage

```kotlin
val blogRoute = Route("blog", "/blog", mapOf(), setOf(GET))
val blogPostRoute = Route("blog_post", "/blog/{id}/show", mapOf("id" to "\\d+"), setOf(GET))
val addPostRoute = Route("blog_post_add", "/blog/{id}/add", mapOf("id" to "\\d+"), setOf(POST))

val matcher = RouteMatcher(setOf(blogRoute, blogPostRoute, addPostRoute))
matcher.match("/blog/2/show", GET)

// Result will be route named blog_post, with parameters: [id: 2]
```

#### Why this library?

To be honest, it's just a playground for me to learn Kotlin. Not that I'm using many (or any!) of the amazing features the language provides, but feel free to suggest improvements.

And if you find it useful, please star the repository, or open an issue if you have any problems!

#### TODO:

- [ ] Write more tests
- [ ] Ensure all functionality expected is covered
- [ ] Write benchmarks
- [ ] Make code more Kotlin-friendly

Enjoy!
