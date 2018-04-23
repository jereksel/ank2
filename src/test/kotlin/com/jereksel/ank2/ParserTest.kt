package com.jereksel.ank2

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ParserTest: StringSpec({

    "Non kotlin code is not included" {

        val md = javaClass.getResourceAsStream("/NonKotlin.md").bufferedReader().readText()

        val actual = Parser.parse(md)

        actual shouldBe emptyList<Snippet>()

    }

    "Kotlin code is included" {

        val md = javaClass.getResourceAsStream("/Kotlin.md").bufferedReader().readText()

        val actual = Parser.parse(md)

        actual shouldBe listOf(
                Snippet(
                        """|```kotlin:ank:ap
                           |package com.example.domain
                           |
                           |@optics
                           |data class Street(val number: Int, val name: String)
                           |@optics
                           |data class Address(val city: String, val street: Street)
                           |@optics
                           |data class Company(val name: String, val address: Address)
                           |@optics
                           |data class Employee(val name: String, val company: Company?)
                           |```""".trimMargin(),
                        Parser.Language.KOTLIN, true),
                Snippet(
                        """|```kotlin:ank
                           |import com.example.domain.*
                           |import com.example.domain.syntax.*
                           |
                           |val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))
                           |
                           |employee.setter().company.address.street.name.modify(String::toUpperCase)
                           |```""".trimMargin(),
                        Parser.Language.KOTLIN, false)
        )

    }


})