package com.jereksel.ank2

data class Snippet(
        val text: String,
        val language: Parser.Language,
        val ap: Boolean
) {
    val code = text.lines().drop(1).dropLast(1).joinToString("\n")
}