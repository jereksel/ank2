package com.jereksel.ank2

import arrow.syntax.collections.prependTo
import org.intellij.markdown.MarkdownElementTypes.CODE_FENCE
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.ast.visitors.Visitor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

object Parser {

    fun parse(markdown: String): List<Snippet> {
        val snippets = mutableListOf<Snippet>()
        MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown).accept(object: RecursiveVisitor() {
            override fun visitNode(node: org.intellij.markdown.ast.ASTNode) {
                if (node.type == CODE_FENCE) {
                    snippets += Snippet(node.getTextInNode(markdown).toString())
                }
                super.visitNode(node)
            }

        })
        return snippets
    }

}