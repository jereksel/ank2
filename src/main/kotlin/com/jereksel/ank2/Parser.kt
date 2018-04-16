package com.jereksel.ank2

import org.intellij.markdown.MarkdownElementTypes.CODE_FENCE
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

object Parser {

    enum class Language {
        KOTLIN,
        OTHER
    }

    fun parse(markdown: String): List<Snippet> {
        val snippets = mutableListOf<Snippet>()
        MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown).accept(object: RecursiveVisitor() {
            override fun visitNode(node: org.intellij.markdown.ast.ASTNode) {
                if (node.type == CODE_FENCE) {
                    val text = node.getTextInNode(markdown).toString()
                    //Drop '```"
                    val firstLine = text.lines().first().drop(3)
                    val args = firstLine.split(":")
                    val languageString = args.firstOrNull()
                    val language = when(languageString) {
                        "kotlin" -> Language.KOTLIN
                        else -> Language.OTHER
                    }
                    val ank = args.contains("ank")
                    val ap = args.contains("ap")
                    if (ank && language == Language.KOTLIN) {
                        snippets += Snippet(text, language, ap)
                    }
                }
                super.visitNode(node)
            }

        })
        return snippets
    }

}