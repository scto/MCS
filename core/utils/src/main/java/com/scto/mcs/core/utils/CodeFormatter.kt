package com.scto.mcs.core.utils

import android.content.Context
import com.facebook.ktfmt.format.Formatter as KtFormatter
import com.facebook.ktfmt.format.FormattingOptions
import com.google.googlejavaformat.java.Formatter as JavaFormatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import com.pinterest.ktlint.rule.engine.api.Code as KtLintCode
import com.pinterest.ktlint.rule.engine.api.EditorConfigDefaults
import com.pinterest.ktlint.rule.engine.api.KtLintRuleEngine
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import com.puppycrawl.tools.checkstyle.Checker
import com.puppycrawl.tools.checkstyle.ConfigurationLoader
import com.puppycrawl.tools.checkstyle.PropertiesExpander
import com.puppycrawl.tools.checkstyle.api.AuditListener
import com.puppycrawl.tools.checkstyle.api.AuditEvent
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

sealed class FormattingResult {
    data class Success(val formattedCode: String) : FormattingResult()
    data class Error(val message: String, val line: Int = -1) : FormattingResult()
}

object CodeFormatter {

    /**
     * Hauptfunktion zur Formatierung mit optionalem Checkstyle/KtLint.
     */
    fun formatWithResult(
        context: Context,
        code: String,
        extension: String,
        indentSize: Int = 4,
        useStricterTools: Boolean = false
    ): FormattingResult {
        if (code.isBlank()) return FormattingResult.Success("")

        return try {
            val formatted = when (extension.lowercase()) {
                "java" -> {
                    if (useStricterTools) verifyJavaWithCheckstyle(context, code)
                    formatJava(code)
                }
                "kt", "kotlin" -> {
                    if (useStricterTools) formatKotlinWithLint(code) else formatKotlin(code)
                }
                "xml" -> formatXml(code, indentSize)
                else -> formatBraceLanguage(code, indentSize)
            }
            FormattingResult.Success(formatted)
        } catch (e: Exception) {
            val errorInfo = parseErrorInfo(e)
            FormattingResult.Error(errorInfo.first, errorInfo.second)
        }
    }

    private fun formatJava(code: String): String {
        return JavaFormatter(JavaFormatterOptions.builder().style(JavaFormatterOptions.Style.GOOGLE).build())
            .formatSource(code)
    }

    /**
     * Führt eine echte Checkstyle-Prüfung durch.
     */
    private fun verifyJavaWithCheckstyle(context: Context, code: String) {
        // 1. Konfigurationsdatei aus Assets laden (z.B. google_checks.xml)
        val configFile = AssetUtils.getAssetFile(context, "google_checks.xml")
        
        val checker = Checker()
        val config = ConfigurationLoader.loadConfiguration(
            configFile.absolutePath,
            PropertiesExpander(System.getProperties())
        )
        
        checker.setModuleClassLoader(Checker::class.java.classLoader)
        checker.configure(config)

        val errors = mutableListOf<String>()
        
        // Listener hinzufügen, um Fehler abzufangen
        checker.addListener(object : AuditListener {
            override fun addError(event: AuditEvent) {
                errors.add("Zeile ${event.line}: ${event.message}")
            }
            override fun auditStarted(event: AuditEvent) {}
            override fun auditFinished(event: AuditEvent) {}
            override fun fileStarted(event: AuditEvent) {}
            override fun fileFinished(event: AuditEvent) {}
            override fun addException(event: AuditEvent, throwable: Throwable) {}
        })

        // Temporäre Datei für die Prüfung erstellen
        val tempFile = File(context.cacheDir, "TempFile.java")
        tempFile.writeText(code)
        
        checker.process(listOf(tempFile))
        checker.destroy()

        if (errors.isNotEmpty()) {
            throw Exception("Checkstyle Verstoß: ${errors.first()}")
        }
    }

    private fun formatKotlin(code: String): String {
        return KtFormatter.format(FormattingOptions(blockIndent = 4), code)
    }

    private fun formatKotlinWithLint(code: String): String {
        val engine = KtLintRuleEngine(
            ruleProviders = StandardRuleSetProvider().getRuleProviders(),
            editorConfigDefaults = EditorConfigDefaults.empty_editor_config_defaults
        )
        return engine.format(KtLintCode.fromSnippet(code))
    }

    private fun parseErrorInfo(e: Exception): Pair<String, Int> {
        val msg = e.localizedMessage ?: "Unbekannter Fehler"
        val lineRegex = Regex("(?:line|Zeile|Zeile )\\s*(\\d+)|:(\\d+):")
        val match = lineRegex.find(msg)
        val line = match?.groupValues?.filter { it.isNotEmpty() }?.get(1)?.toIntOrNull() ?: -1
        return Pair(msg, line)
    }

    private fun formatXml(code: String, indentSize: Int): String {
        val db = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = db.parse(InputSource(StringReader(code)))
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indentSize.toString())
        val out = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(out))
        return out.toString().trim()
    }

    private fun formatBraceLanguage(code: String, indentSize: Int): String {
        val lines = code.split("\n")
        val result = StringBuilder()
        var indentLevel = 0
        val indentStr = " ".repeat(indentSize)
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) { result.append("\n"); continue }
            if (trimmed.startsWith("}") || trimmed.startsWith(")") || trimmed.startsWith("]")) indentLevel--
            repeat(maxOf(0, indentLevel)) { result.append(indentStr) }
            result.append(trimmed).append("\n")
            if (trimmed.endsWith("{") || trimmed.endsWith("(") || trimmed.endsWith("[")) indentLevel++
        }
        return result.toString().trim()
    }
}