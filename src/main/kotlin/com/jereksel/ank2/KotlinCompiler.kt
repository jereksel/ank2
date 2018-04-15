package com.jereksel.ank2

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

object KotlinCompiler {

    fun performAnnotationProcessing(code: String): Either<String, File> {

        val classPathJars = System.getProperty("java.class.path").split(File.pathSeparatorChar)

        val annotationProcessingJar = classPathJars.first { it.contains("${File.separator}kotlin-annotation-processing${File.separator}") }

        val tempFile = File.createTempFile("ank2source", ".kt")

        val sourceDir = createTempDir()
        val classesDir = createTempDir()
        val stubsDir = createTempDir()
        val outputDir = createTempDir()

        val jar = File.createTempFile("ank2jar", ".jar")

        val javacArgument = mapOf("kapt.kotlin.generated" to outputDir.absolutePath)

        tempFile.writeText(code)

        val args = arrayOf(
                "-Xplugin=$annotationProcessingJar",
                "-classpath", classPathJars.joinToString(File.pathSeparator),
                "-P", "plugin:org.jetbrains.kotlin.kapt3:sources=${sourceDir.absolutePath}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:apoptions=${encodeList(javacArgument)}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:classes=${classesDir.absolutePath}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:stubs=${stubsDir.absolutePath}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:aptOnly=true",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:verbose=true",
                *classPathJars.flatMap { listOf("-P", "plugin:org.jetbrains.kotlin.kapt3:apclasspath=$it") }.toTypedArray(),
                tempFile.absolutePath
        )

        ByteArrayOutputStream().let { baos ->

            PrintStream(baos, true, UTF_8.displayName())
                    .use { annotationProcessingPrintStream ->

                        val ret = K2JVMCompiler().exec(annotationProcessingPrintStream, *args)

                        if (ret != ExitCode.OK) {
                            return String(baos.toByteArray()).left()
                        }

                    }

        }

        val compileArgs = arrayOf(
                "-classpath", classPathJars.joinToString(File.pathSeparator),
                tempFile.absolutePath,
                *outputDir.listFiles().map { it.absolutePath }.toTypedArray(),
                "-d", jar.absolutePath,
                "-module-name", "ank_kapt_files",
                "-no-stdlib"
        )

        ByteArrayOutputStream().let { baos ->

            PrintStream(baos, true, UTF_8.displayName())
                    .use { annotationProcessingPrintStream ->

                        val ret = K2JVMCompiler().exec(annotationProcessingPrintStream, *compileArgs)

                        if (ret != ExitCode.OK) {
                            return String(baos.toByteArray()).left()
                        }

                    }

        }

        return jar.right()

    }

    fun compile(code: String, jars: List<File>): Either<String, Any?> {

        val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine

        ((engine as KotlinJsr223JvmLocalScriptEngine).templateClasspath as ArrayList<File>).addAll(jars)

        return engine.eval(code).right()
    }

    // https://kotlinlang.org/docs/reference/kapt.html
    private fun encodeList(options: Map<String, String>): String {
        val os = ByteArrayOutputStream()
        val oos = ObjectOutputStream(os)

        oos.writeInt(options.size)
        for ((key, value) in options.entries) {
            oos.writeUTF(key)
            oos.writeUTF(value)
        }

        oos.flush()
        return Base64.getEncoder().encodeToString(os.toByteArray())
    }
}