package com.jereksel.ank2

import arrow.core.Either
import arrow.core.fix
import arrow.core.monad
import arrow.typeclasses.binding
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class KotlinCompilerTest: StringSpec({

   "basic test" {

       //TODO: Remove package
       val objectDeclaration = """
            package abc

            import arrow.optics.optics
            @optics data class Project(val name: String?)
            """

       val code = """
            package abc

            val p = abc.Project("arrow")
            val modify = abc.projectName()
            modify.modify(p, { "ARROW" })
           """

       val ret = Either.monad<String>().binding {
           val jar = KotlinCompiler.performAnnotationProcessing(objectDeclaration).bind()
           KotlinCompiler.compile(code, listOf(jar)).bind()
       }.fix()

       ret.isRight() shouldBe true

       ret.get().toString() shouldBe "Project(name=ARROW)"

   }

    "self referencing test" {

        val objectDeclaration = """
            package com.example.domain

            import arrow.optics.optics

            @optics data class Street(val number: Int, val name: String)
            @optics data class Address(val city: String, val street: Street)
            @optics data class Company(val name: String, val address: Address)
            @optics data class Employee(val name: String, val company: Company?)
"""

        val ret = KotlinCompiler.performAnnotationProcessing(objectDeclaration)

        ret.isRight() shouldBe true

    }


})
