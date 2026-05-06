package com.mcs.core.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Utility-Klasse für JSON-Operationen.
 * Nutzt Kotlinx Serialization für Typsicherheit und Performance.
 */
object JsonUtils {

    val jsonConfig = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    /** Konvertiert ein Objekt in einen JSON-String */
    inline fun <reified T> toJson(data: T): String {
        return try {
            jsonConfig.encodeToString(data)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /** Konvertiert einen JSON-String zurück in ein Objekt */
    inline fun <reified T> fromJson(json: String): T? {
        return try {
            jsonConfig.decodeFromString<T>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Formatiert einen unschönen JSON-String (Pretty Print) */
    fun prettify(json: String): String {
        return try {
            val element = jsonConfig.parseToJsonElement(json)
            jsonConfig.encodeToString(element)
        } catch (e: Exception) {
            json
        }
    }

    /** Extrahiert einen einfachen String-Wert anhand eines Keys aus der ersten Ebene */
    fun getStringValue(json: String, key: String): String? {
        return try {
            val element = jsonConfig.parseToJsonElement(json).jsonObject
            element[key]?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    /** Überprüft, ob ein String ein gültiges JSON ist */
    fun isValidJson(json: String): Boolean {
        return try {
            jsonConfig.parseToJsonElement(json)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Speichert ein Objekt direkt als JSON-Datei */
    inline fun <reified T> saveToFile(data: T, filePath: String) {
        val json = toJson(data)
        FileUtils.save(filePath, json)
    }

    /** Lädt ein Objekt direkt aus einer JSON-Datei */
    inline fun <reified T> loadFromFile(filePath: String): T? {
        val content = FileUtils.load(filePath)
        return if (content.isNotEmpty()) fromJson<T>(content) else null
    }
}