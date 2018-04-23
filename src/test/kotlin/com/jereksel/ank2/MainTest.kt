package com.jereksel.ank2

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class MainTest: StringSpec({

    "Basic test" {

        val markdown = javaClass.getResourceAsStream("/MainTest.md").bufferedReader().readText()

        val result = Main.process(markdown)

        result shouldBe ""


    }


})