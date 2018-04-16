package com.jereksel.ank2

import arrow.core.Either
import arrow.core.fix
import arrow.core.getOrElse
import arrow.core.monad
import arrow.typeclasses.binding
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class Test: StringSpec({

   "basic test" {

       val objectDeclaration = """
            import arrow.optics.optics
            @optics data class Project(val name: String?)
            """

       val code = """
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

})
