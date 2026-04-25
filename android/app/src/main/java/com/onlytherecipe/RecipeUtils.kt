package com.onlytherecipe

fun String.fmt(): String {
    if (!startsWith("PT")) return this
    val hours   = Regex("(\\d+)H").find(this)?.groupValues?.get(1)
    val minutes = Regex("(\\d+)M").find(this)?.groupValues?.get(1)
    val parts   = mutableListOf<String>()
    if (hours   != null) parts.add("$hours hr")
    if (minutes != null) parts.add("$minutes min")
    return parts.joinToString(" ").ifEmpty { this }
}

fun Recipe.toShareText(): String {
    val sb = StringBuilder()
    sb.appendLine(title)
    sb.appendLine()

    val timing = buildList {
        if (timingPrep.isNotEmpty())  add("Prep: ${timingPrep.fmt()}")
        if (timingCook.isNotEmpty())  add("Cook: ${timingCook.fmt()}")
        if (timingTotal.isNotEmpty()) add("Total: ${timingTotal.fmt()}")
    }
    if (timing.isNotEmpty())          sb.appendLine(timing.joinToString(" · "))
    if (recipeYield.isNotEmpty())     sb.appendLine("Yield: $recipeYield")
    if (timing.isNotEmpty() || recipeYield.isNotEmpty()) sb.appendLine()

    if (description.isNotEmpty()) {
        sb.appendLine(description)
        sb.appendLine()
    }

    if (ingredients.isNotEmpty()) {
        sb.appendLine("INGREDIENTS")
        ingredients.forEach { sb.appendLine("• $it") }
        sb.appendLine()
    }

    if (equipment.isNotEmpty()) {
        sb.appendLine("EQUIPMENT")
        equipment.forEach { sb.appendLine("• $it") }
        sb.appendLine()
    }

    if (instructions.isNotEmpty()) {
        sb.appendLine("INSTRUCTIONS")
        var stepNum = 1
        instructions.forEach { step ->
            if (step.startsWith("§:")) {
                sb.appendLine()
                sb.appendLine(step.substring(2).uppercase())
            } else {
                sb.appendLine("$stepNum. $step")
                stepNum++
            }
        }
        sb.appendLine()
    }

    if (authorNotes.isNotEmpty()) {
        sb.appendLine("NOTES")
        sb.appendLine(authorNotes)
        sb.appendLine()
    }

    if (notes.isNotEmpty()) {
        sb.appendLine("MY NOTES")
        sb.appendLine(notes)
        sb.appendLine()
    }

    val safeUrl = sourceUrl.takeIf { it.startsWith("https://") || it.startsWith("http://") }
    if (safeUrl != null) sb.append("Source: $safeUrl")

    return sb.toString().trimEnd()
}
