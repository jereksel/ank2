package com.jereksel.ank2

import arrow.data.k

object Main {

    @JvmStatic
    fun main(args: Array<String>) {



    }

    fun process(content: String): String {

        var result = content

        val blocks = Parser.parse(content)

        val toAp = blocks.filter { it.ap }
        val nonAp = blocks.filter { !it.ap }

        //FIXME
//        val jars = toAp.map { KotlinCompiler.performAnnotationProcessing(it.code) }.map { it.right().get() }

        val jars = toAp.map { snippet: Snippet ->
            val res = KotlinCompiler.performAnnotationProcessing(snippet.code)
            res.fold({throw RuntimeException("$snippet failed: $it")}, {it})
        }

        nonAp.forEach { snippet: Snippet ->

            val code = snippet.code

            //FIXME
            val compilationResult = KotlinCompiler.compile(code, jars).fold({throw RuntimeException("$snippet compilation failed: $it")}, {it})

            result = result.replace(snippet.text, "```kotlin\n$code\n//$compilationResult\n```")

        }

        return result

    }

}