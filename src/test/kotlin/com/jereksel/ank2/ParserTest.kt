package com.jereksel.ank2

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ParserTest: StringSpec({

    "basic test" {

        val md = """
            |```
            |function test() {
            |  console.log("notice the blank line before this function?");
            |}
            |```
            |```ruby
            |require 'redcarpet'
            |markdown = Redcarpet.new("Hello World!")
            |puts markdown.to_html
            |```
            """.trimMargin()

        val expected = listOf(
                Snippet(
                     """
                    |```
                    |function test() {
                    |  console.log("notice the blank line before this function?");
                    |}
                    |```
                """.trimMargin()
                ),
                Snippet(
                    """
                    |```ruby
                    |require 'redcarpet'
                    |markdown = Redcarpet.new("Hello World!")
                    |puts markdown.to_html
                    |```
        """.trimMargin()
                )
        )

        val actual = Parser.parse(md)

        actual shouldBe expected

    }


})