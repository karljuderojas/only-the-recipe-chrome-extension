package com.onlytherecipe

import kotlin.math.abs
import kotlin.math.roundToInt

object RecipeConverter {

    private val NUM = """(\d+\s+\d+/\d+|\d+/\d+|\d+(?:\.\d+)?)"""

    // Imperial → metric patterns
    private val F_TEMP  = Regex("""(\d+)\s*°F\b""")
    private val IMP_VOL = Regex(
        """$NUM\s*(cups?|tbsps?\.?|tablespoons?|tsps?\.?|teaspoons?|fl\.?\s*oz\.?|fluid\s+ounces?|quarts?|qt\.?|pints?|pt\.?)(?![a-zA-Z])""",
        RegexOption.IGNORE_CASE
    )
    private val IMP_WT = Regex(
        """$NUM\s*(lbs?\.?|pounds?|oz\.?|ounces?)(?![a-zA-Z])""",
        RegexOption.IGNORE_CASE
    )

    // Metric → imperial patterns
    private val C_TEMP  = Regex("""(\d+)\s*°C\b""")
    private val MET_VOL = Regex(
        """$NUM\s*(ml\.?|millilitres?|milliliters?|L\.?|litres?|liters?)(?![a-zA-Z])""",
        RegexOption.IGNORE_CASE
    )
    private val MET_WT = Regex(
        """$NUM\s*(kg\.?|kilograms?|g\.?|grams?)(?![a-zA-Z])""",
        RegexOption.IGNORE_CASE
    )

    fun Recipe.withUnits(toMetric: Boolean): Recipe {
        val fn: (String) -> String = if (toMetric) ::toMetric else ::toImperial
        return copy(
            ingredients  = ingredients.map(fn),
            instructions = instructions.map(fn),
            description  = fn(description),
            authorNotes  = fn(authorNotes)
        )
    }

    private fun toMetric(text: String): String {
        var s = F_TEMP.replace(text) { m ->
            val f = m.groupValues[1].toDoubleOrNull() ?: return@replace m.value
            "${((f - 32) * 5.0 / 9.0).roundToInt()}°C"
        }
        // Volume before weight so "fl oz" is handled before plain "oz"
        s = IMP_VOL.replace(s) { m ->
            val qty = parseNum(m.groupValues[1]) ?: return@replace m.value
            val unit = m.groupValues[2].lowercase().replace(Regex("""\s+"""), "").trimEnd('.')
            val ml = qty * when {
                unit.startsWith("tsp") || unit.startsWith("teaspoon") -> 4.929
                unit.startsWith("tbsp") || unit.startsWith("tablespoon") -> 14.787
                unit.startsWith("cup") -> 236.588
                unit.startsWith("fl") || unit.startsWith("fluid") -> 29.574
                unit == "pt" || unit.startsWith("pint") -> 473.176
                unit == "qt" || unit.startsWith("quart") -> 946.353
                else -> return@replace m.value
            }
            if (ml >= 1000) "${fmtNum(ml / 1000)} L" else "${fmtNum(ml)} ml"
        }
        s = IMP_WT.replace(s) { m ->
            val qty = parseNum(m.groupValues[1]) ?: return@replace m.value
            val unit = m.groupValues[2].lowercase().trimEnd('.')
            val g = qty * when {
                unit.startsWith("lb") || unit.startsWith("pound") -> 453.592
                unit.startsWith("oz") || unit.startsWith("ounce") -> 28.3495
                else -> return@replace m.value
            }
            if (g >= 1000) "${fmtNum(g / 1000)} kg" else "${fmtNum(g)} g"
        }
        return s
    }

    private fun toImperial(text: String): String {
        var s = C_TEMP.replace(text) { m ->
            val c = m.groupValues[1].toDoubleOrNull() ?: return@replace m.value
            "${(c * 9.0 / 5.0 + 32).roundToInt()}°F"
        }
        s = MET_VOL.replace(s) { m ->
            val qty = parseNum(m.groupValues[1]) ?: return@replace m.value
            val unit = m.groupValues[2].lowercase().trimEnd('.')
            val ml = qty * if (unit == "l" || unit.startsWith("liter") || unit.startsWith("litre")) 1000.0 else 1.0
            when {
                ml < 7   -> "${fmtFrac(ml / 4.929)} tsp"
                ml < 60  -> "${fmtFrac(ml / 14.787)} tbsp"
                ml < 950 -> "${fmtFrac(ml / 236.588)} cup"
                else     -> "${fmtFrac(ml / 946.353)} qt"
            }
        }
        s = MET_WT.replace(s) { m ->
            val qty = parseNum(m.groupValues[1]) ?: return@replace m.value
            val unit = m.groupValues[2].lowercase().trimEnd('.')
            val g = qty * if (unit == "kg" || unit.startsWith("kilogram")) 1000.0 else 1.0
            if (g >= 340) "${fmtFrac(g / 453.592)} lb" else "${fmtFrac(g / 28.3495)} oz"
        }
        return s
    }

    private fun parseNum(s: String): Double? {
        val str = s.trim()
        Regex("""^(\d+)\s+(\d+)/(\d+)$""").find(str)?.let {
            return it.groupValues[1].toDouble() +
                   it.groupValues[2].toDouble() / it.groupValues[3].toDouble()
        }
        Regex("""^(\d+)/(\d+)$""").find(str)?.let {
            return it.groupValues[1].toDouble() / it.groupValues[2].toDouble()
        }
        return str.toDoubleOrNull()
    }

    // Format a metric value: integer if within 5% of whole, otherwise 1 decimal place
    private fun fmtNum(d: Double): String {
        val ri = d.roundToInt()
        return if (ri > 0 && abs(d - ri) / ri < 0.05) "$ri" else String.format("%.1f", d)
    }

    // Format an imperial value: prefer common fractions (1/8, 1/4, 1/3, 1/2, 2/3, 3/4),
    // fall back to decimal for anything in between
    private fun fmtFrac(d: Double): String {
        val fracs = listOf(
            1.0 / 8 to "1/8", 1.0 / 4 to "1/4", 1.0 / 3 to "1/3",
            3.0 / 8 to "3/8", 1.0 / 2 to "1/2", 2.0 / 3 to "2/3",
            3.0 / 4 to "3/4", 7.0 / 8 to "7/8"
        )
        val whole = d.toInt()
        val frac  = d - whole
        if (frac < 0.06) return if (whole == 0) "0" else "$whole"
        for ((value, label) in fracs) {
            if (abs(frac - value) < 0.06) return if (whole == 0) label else "$whole $label"
        }
        return fmtNum(d)
    }
}
