package com.onlytherecipe

private fun String.esc(): String = this
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")

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

fun Recipe.toHtml(): String {
    val timing = buildList {
        if (timingPrep.isNotEmpty())  add("Prep: ${timingPrep.fmt()}")
        if (timingCook.isNotEmpty())  add("Cook: ${timingCook.fmt()}")
        if (timingTotal.isNotEmpty()) add("Total: ${timingTotal.fmt()}")
    }.joinToString(" &nbsp;·&nbsp; ")

    val ingredients  = ingredients.joinToString("") { "<li>${it.esc()}</li>" }
    val equipment    = equipment.joinToString("") { "<li>${it.esc()}</li>" }
    val instructions = buildInstructionsHtml(instructions)
    val safeSourceUrl = sourceUrl.takeIf {
        it.startsWith("https://") || it.startsWith("http://")
    } ?: ""

    return """
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta http-equiv="Content-Security-Policy" content="default-src 'none'; img-src https: data:; style-src 'unsafe-inline';">

  <style>
    body { font-family: system-ui, -apple-system, sans-serif; max-width: 760px;
           margin: 0 auto; padding: 16px 16px 40px; line-height: 1.6; color: #111; background: #fff; }
    h1   { font-size: 1.6em; margin-top: 0; }
    h2   { font-size: 1.2em; border-bottom: 1px solid #eee; padding-bottom: 4px; margin-top: 24px; }
    h3   { font-size: 1em; margin: 16px 0 4px; }
    .timing { font-size: 0.9em; color: #666; margin-bottom: 16px; }
    .yield  { font-size: 0.9em; color: #444; }
    .hero   { width: 100%; border-radius: 12px; margin-bottom: 20px; display: block; }
    ul, ol  { padding-left: 1.4em; }
    li      { margin-bottom: 6px; }
    .notes  { background: #f0f4ff; border-left: 3px solid #2563eb;
              padding: 12px 16px; border-radius: 4px; font-size: 0.9em;
              white-space: pre-line; margin-top: 20px; }
    .notes strong { display: block; margin-bottom: 6px; font-size: 0.75em;
                    text-transform: uppercase; letter-spacing: 0.05em; opacity: 0.6; }
    .source { font-size: 0.75em; color: #888; margin-top: 24px; word-break: break-all; }
    a { color: #2563eb; }
  </style>
</head>
<body>
  ${if (imageUrl.isNotEmpty()) """<img class="hero" src="${imageUrl.esc()}" alt="${title.esc()}">""" else ""}
  <h1>${title.esc()}</h1>
  ${if (timing.isNotEmpty())             """<p class="timing">$timing</p>""" else ""}
  ${if (recipeYield.isNotEmpty())        """<p class="yield"><strong>Yield:</strong> ${recipeYield.esc()}</p>""" else ""}
  ${if (description.isNotEmpty())        """<p>${description.esc()}</p>""" else ""}
  ${if (this.ingredients.isNotEmpty())   """<h2>Ingredients</h2><ul>$ingredients</ul>""" else ""}
  ${if (this.equipment.isNotEmpty())     """<h2>Equipment</h2><ul>$equipment</ul>""" else ""}
  ${if (this.instructions.isNotEmpty())  """<h2>Instructions</h2>$instructions""" else ""}
  ${if (authorNotes.isNotEmpty())        """<div class="notes"><strong>Author Notes</strong>${authorNotes.esc()}</div>""" else ""}
  ${if (safeSourceUrl.isNotEmpty())      """<p class="source">Source: <a href="${safeSourceUrl.esc()}">${safeSourceUrl.esc()}</a></p>""" else ""}
</body>
</html>""".trimIndent()
}

private fun buildInstructionsHtml(steps: List<String>): String {
    val sb = StringBuilder("<ol>")
    for (step in steps) {
        if (step.startsWith("§:")) {
            sb.append("</ol><h3>${step.substring(2).esc()}</h3><ol>")
        } else {
            sb.append("<li>${step.esc()}</li>")
        }
    }
    sb.append("</ol>")
    return sb.toString()
}
