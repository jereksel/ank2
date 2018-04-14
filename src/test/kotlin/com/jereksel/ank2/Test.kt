package com.jereksel.ank2

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.jetbrains.kotlin.cli.jvm.main

class Test: StringSpec({

   "basic test" {

       val objectDeclaration = """
            package abc

            import arrow.optics.optics
            @optics data class Project(val name: String?)
            """

       val code = """
            val p = abc.Project("arrow")
            val modify = abc.projectName()
            modify.modify(p, {"ARROW"})
           """

//       main()

//       val a  = abc.personAge()
//
//       val person = Person(33)

//       a.modify(person, {50})
//
//       println(person)

       val ret = KotlinCompiler().compile(objectDeclaration, code)

//       ret shouldBe 10

       ret.toString() shouldBe "Project(name=ARROW)"

   }



})
