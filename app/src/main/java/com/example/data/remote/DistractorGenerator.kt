package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object DistractorGenerator {
    private const val TAG = "DistractorGen"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateDistractors(
        question: String,
        correctAnswer: String,
        category: String = "General Knowledge"
    ): List<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If a real API key is available, attempt Gemini generation
        if (!apiKey.isNullOrBlank() && !apiKey.contains("MY_GEMINI_API_KEY")) {
            try {
                val aiDistractors = callGeminiForDistractors(question, correctAnswer, category, apiKey)
                if (aiDistractors.size >= 3) {
                    return@withContext aiDistractors.take(3)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API distractor generation failed, falling back: ${e.message}")
            }
        }

        // Robust intelligent fallback generator
        generateSmartFallbackDistractors(question, correctAnswer, category)
    }

    private fun callGeminiForDistractors(
        question: String,
        correctAnswer: String,
        category: String,
        apiKey: String
    ): List<String> {
        val prompt = """
            Given the question: "$question"
            Category: "$category"
            Correct Answer: "$correctAnswer"

            Generate exactly 3 plausible, distinct incorrect multiple choice options (distractors) that sound credible and match the format and domain of the correct answer.
            Return ONLY a raw JSON array of 3 strings, for example:
            ["Option A", "Option B", "Option C"]
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(jsonRequest.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: return emptyList()

        val rootObj = JSONObject(responseBody)
        val candidates = rootObj.optJSONArray("candidates") ?: return emptyList()
        val firstCandidate = candidates.optJSONObject(0) ?: return emptyList()
        val content = firstCandidate.optJSONObject("content") ?: return emptyList()
        val parts = content.optJSONArray("parts") ?: return emptyList()
        val textPart = parts.optJSONObject(0)?.optString("text") ?: return emptyList()

        val cleanJson = textPart.trim().removeSurrounding("```json", "```").trim()
        val jsonArray = JSONArray(cleanJson)
        val results = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val distractor = jsonArray.optString(i).trim()
            if (distractor.isNotEmpty() && !distractor.equals(correctAnswer.trim(), ignoreCase = true)) {
                results.add(distractor)
            }
        }
        return results
    }

    fun generateSmartFallbackDistractors(
        question: String,
        correctAnswer: String,
        category: String
    ): List<String> {
        val trimmedAnswer = correctAnswer.trim()

        // 1. Check if answer is a pure integer / year / number
        val intValue = trimmedAnswer.toIntOrNull()
        if (intValue != null) {
            val offsets = listOf(-2, +3, -5, +7, -10, +12, +1, -1).shuffled()
            val distractors = mutableListOf<String>()
            for (offset in offsets) {
                val candidate = intValue + offset
                val candidateStr = if (candidate > 0) candidate.toString() else (intValue + kotlin.math.abs(offset) + 2).toString()
                if (candidateStr != trimmedAnswer && candidateStr !in distractors) {
                    distractors.add(candidateStr)
                    if (distractors.size == 3) return distractors
                }
            }
        }

        // 2. Check if answer is a decimal number
        val doubleValue = trimmedAnswer.toDoubleOrNull()
        if (doubleValue != null) {
            val scale = if (doubleValue > 10) 1.5 else 0.5
            return listOf(
                "%.1f".format(doubleValue + scale),
                "%.1f".format(doubleValue - (scale / 2)),
                "%.1f".format(doubleValue * 1.4)
            )
        }

        // 3. Check domain dictionary
        val lowerQ = question.lowercase()
        val lowerA = trimmedAnswer.lowercase()

        // Planet / Solar system
        if (lowerQ.contains("planet") || lowerQ.contains("solar") || lowerQ.contains("moon")) {
            val planets = listOf("Mars", "Venus", "Jupiter", "Saturn", "Mercury", "Neptune", "Uranus")
            val filtered = planets.filter { !it.equals(trimmedAnswer, ignoreCase = true) }.shuffled()
            if (filtered.size >= 3) return filtered.take(3)
        }

        // Countries / Continents / Cities
        if (lowerQ.contains("country") || lowerQ.contains("capital") || lowerQ.contains("city") || lowerQ.contains("river") || lowerQ.contains("nation")) {
            val places = listOf("France", "Brazil", "Japan", "Canada", "Egypt", "Australia", "Germany", "India", "Norway", "Chile")
            val filtered = places.filter { !it.equals(trimmedAnswer, ignoreCase = true) }.shuffled()
            if (filtered.size >= 3) return filtered.take(3)
        }

        // Programming / Computer languages
        if (lowerQ.contains("programming") || lowerQ.contains("code") || lowerQ.contains("language") || lowerQ.contains("software")) {
            val langs = listOf("Python", "Kotlin", "TypeScript", "Rust", "Java", "C++", "Go", "Swift")
            val filtered = langs.filter { !it.equals(trimmedAnswer, ignoreCase = true) }.shuffled()
            if (filtered.size >= 3) return filtered.take(3)
        }

        // Scientists / Historical Figures / Authors
        if (lowerQ.contains("who") || lowerQ.contains("invented") || lowerQ.contains("discovered") || lowerQ.contains("wrote") || lowerQ.contains("painter")) {
            val people = listOf("Albert Einstein", "Isaac Newton", "Marie Curie", "Nikola Tesla", "Leonardo da Vinci", "Galileo Galilei", "Charles Darwin")
            val filtered = people.filter { !it.equals(trimmedAnswer, ignoreCase = true) }.shuffled()
            if (filtered.size >= 3) return filtered.take(3)
        }

        // Science elements / molecules
        if (lowerQ.contains("element") || lowerQ.contains("chemical") || lowerQ.contains("acid") || lowerQ.contains("atom")) {
            val elements = listOf("Hydrogen", "Helium", "Carbon", "Oxygen", "Nitrogen", "Iron", "Gold", "Sodium")
            val filtered = elements.filter { !it.equals(trimmedAnswer, ignoreCase = true) }.shuffled()
            if (filtered.size >= 3) return filtered.take(3)
        }

        // True / False
        if (lowerA == "true" || lowerA == "yes") {
            return listOf("False", "Partially True", "Uncertain")
        }
        if (lowerA == "false" || lowerA == "no") {
            return listOf("True", "Partially False", "Cannot be determined")
        }

        // 4. Category-based pool distractor generation
        val categoryPool = when (category.lowercase()) {
            "science & tech" -> listOf("Quantum Tunneling", "Electromagnetism", "Cellular Respiration", "Polymers", "Superconductivity", "Photosynthesis", "Dark Matter", "Nuclear Fission")
            "history & geography" -> listOf("Roman Empire", "Ottoman Dynasty", "Mesopotamia", "Byzantine Period", "Hanseatic League", "Silk Road", "Babylon", "Pax Romana")
            "arts & literature" -> listOf("Renaissance", "Surrealism", "Modernism", "Baroque", "Impressionism", "Romanticism", "Classicism", "Gothic")
            "pop culture & cinema" -> listOf("The Dark Knight", "Inception", "Pulp Fiction", "The Matrix", "Interstellar", "Blade Runner", "Gladiator")
            else -> listOf("Apex Variation", "Primary Factor", "Secondary Catalyst", "Inverse Paradigm", "Standard Baseline", "Empirical Delta")
        }

        val poolFiltered = categoryPool.filter { !it.equals(trimmedAnswer, ignoreCase = true) }.shuffled()
        if (poolFiltered.size >= 3) {
            return poolFiltered.take(3)
        }

        // 5. General fallback variations
        return listOf(
            "$trimmedAnswer (Alternative)",
            "Pseudo-${trimmedAnswer.lowercase().replaceFirstChar { it.uppercase() }}",
            "Non-${trimmedAnswer.lowercase()}"
        )
    }
}
