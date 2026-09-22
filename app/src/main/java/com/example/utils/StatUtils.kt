package com.example.utils

/**
 * Item stats live in the database as one string: "Síla:+5,Odolnost vůči zimě:Dobrá".
 * These helpers turn that into structured pairs and merge the stats of several items the
 * way a character sheet would: numeric bonuses add up instead of overwriting each other.
 */
object StatUtils {

    data class Stat(val name: String, val value: String)

    private val LEADING_NUMBER = Regex("""^[+-]?\d+(\.\d+)?""")
    private val DECIMAL_COMMA = Regex("""(\d),(\d)""")

    /**
     * Commas separate one stat from the next, so a decimal comma typed into a value
     * ("Váha:+0,5") would tear it in half. Rewriting a comma that sits between two digits
     * into a dot keeps the value in one piece. Call this before storing user input.
     */
    fun sanitize(raw: String): String =
        DECIMAL_COMMA.replace(raw) { "${it.groupValues[1]}.${it.groupValues[2]}" }

    /** Splits a raw stat string into name/value pairs and drops malformed fragments. */
    fun parse(raw: String): List<Stat> = raw.split(',').mapNotNull { fragment ->
        val separator = fragment.indexOf(':')
        if (separator <= 0) return@mapNotNull null
        val name = fragment.substring(0, separator).trim()
        val value = fragment.substring(separator + 1).trim()
        if (name.isEmpty() || value.isEmpty()) null else Stat(name, value)
    }

    /**
     * Merges the stats of every equipped item. Two pieces granting "Ochrana:+3" become
     * "Ochrana:+6" rather than the second silently replacing the first, and values that are
     * not numbers ("Odolnost vůči zimě:Dobrá") are collected and joined with " / ".
     */
    fun merge(rawStats: List<String>): List<Stat> {
        val order = LinkedHashSet<String>()
        val totals = HashMap<String, Double>()
        val units = HashMap<String, String>()
        val signed = HashMap<String, Boolean>()
        val textual = HashMap<String, MutableList<String>>()

        rawStats.forEach { raw ->
            parse(raw).forEach { (name, value) ->
                order += name
                val match = LEADING_NUMBER.find(value)
                val number = match?.value?.toDoubleOrNull()
                if (number == null) {
                    val values = textual.getOrPut(name) { mutableListOf() }
                    if (value !in values) values += value
                } else {
                    totals[name] = (totals[name] ?: 0.0) + number
                    if (name !in units) {
                        units[name] = value.substring(match.value.length).trim()
                        signed[name] = value.startsWith('+') || value.startsWith('-')
                    }
                }
            }
        }

        return order.map { name ->
            val parts = mutableListOf<String>()
            totals[name]?.let { parts += format(it, units[name].orEmpty(), signed[name] == true) }
            textual[name]?.let { parts += it }
            Stat(name, parts.joinToString(" / "))
        }
    }

    private fun format(total: Double, unit: String, withSign: Boolean): String {
        val number = if (total % 1.0 == 0.0) total.toLong().toString() else total.toString()
        val signedNumber = if (withSign && total > 0) "+$number" else number
        return if (unit.isEmpty()) signedNumber else "$signedNumber $unit"
    }
}

/** Accepts both "0.5" and the "0,5" the Czech number keyboard produces. */
fun String.toWeightOrNull(): Double? = trim().replace(',', '.').toDoubleOrNull()
