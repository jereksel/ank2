package com.jereksel.ank2

import org.jetbrains.kotlin.cli.common.CLITool
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.cli.jvm.compiler.CompileEnvironmentException
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.util.*
import javax.script.ScriptEngineManager

class KotlinCompiler {
    fun compile(objectDeclaration: String, code: String): Any? {

        val classPathJars = System.getProperty("java.class.path").split(":")

        val annotationProcessingJar = classPathJars.first { it.contains("/kotlin-annotation-processing/") }

        val tempFile = File.createTempFile("sfdajksdfajlk", ".kt")

//        val tempFileKts = File.createTempFile("sfdajksdfajlk", ".kts")

        val sourceDir = createTempDir()
//        val src = File(sourceDir, "myfile.kt")
//        src.writeText(objectDeclaration)
        val classesDir = createTempDir()
        val stubsDir = createTempDir()
        val outputDir = createTempDir()

        val jar = File.createTempFile("asdsd", ".jar")

        val javacArgument = mapOf("kapt.kotlin.generated" to outputDir.absolutePath)

        tempFile.writeText(objectDeclaration)

        val args = arrayOf(
                "-Xplugin=$annotationProcessingJar",
                "-classpath", classPathJars.joinToString(":"),
//                "-classpath", *classPathJars.toTypedArray(),
//                *classPathJars.flatMap { listOf("-classpath", it) }.toTypedArray(),
                "-P", "plugin:org.jetbrains.kotlin.kapt3:sources=${sourceDir.absolutePath}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:apoptions=${encodeList(javacArgument)}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:classes=${classesDir.absolutePath}",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:stubs=${stubsDir.absolutePath}",
//                "-P", "plugin:org.jetbrains.kotlin.kapt3:aptMode=stubsAndApt",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:aptOnly=true",
                "-P", "plugin:org.jetbrains.kotlin.kapt3:verbose=true",
                *classPathJars.flatMap { listOf("-P", "plugin:org.jetbrains.kotlin.kapt3:apclasspath=$it") }.toTypedArray(),
                tempFile.absolutePath
        )

        try {
            CLITool.doMainNoExit(K2JVMCompiler(), args)
        } catch (e: CompileEnvironmentException) {
            e.printStackTrace()
        }

        val compileArgs = arrayOf(
                "-classpath", classPathJars.joinToString(":"),
                tempFile.absolutePath,
                *outputDir.listFiles().map { it.absolutePath }.toTypedArray(),
                "-d", jar.absolutePath,
                "-module-name", "ank_kapt_files"
        )

        println("Compiling")

        try {
            CLITool.doMainNoExit(K2JVMCompiler(), compileArgs)
        } catch (e: CompileEnvironmentException) {
            e.printStackTrace()
        }

        jar.setExecutable(true)

        val seManager = ScriptEngineManager()

        val engine = seManager.getEngineByExtension("kts")

        ((engine as KotlinJsr223JvmLocalScriptEngine).templateClasspath as ArrayList<File>).add(jar)

        println("JAR: $jar")
        println("Output: $outputDir")

//        outputDir.listFiles().forEach { engine.eval(it.readText()) }

        engine.eval(objectDeclaration)

//        outputDir.listFiles().forEach { engine.eval(it.readText()) }

        return engine.eval(code)
    }

    // https://kotlinlang.org/docs/reference/kapt.html
    fun encodeList(options: Map<String, String>): String {
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