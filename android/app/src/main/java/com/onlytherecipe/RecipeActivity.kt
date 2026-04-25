package com.onlytherecipe

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class RecipeActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var saveBtn: Button
    private lateinit var notesInput: EditText

    private lateinit var shareBtn: Button

    private var currentRecipe: Recipe? = null
    private var extractionDone = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        webView     = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        saveBtn     = findViewById(R.id.saveBtn)
        shareBtn    = findViewById(R.id.shareBtn)
        notesInput  = findViewById(R.id.notesInput)

        findViewById<Button>(R.id.backBtn).setOnClickListener { finish() }

        shareBtn.setOnClickListener { currentRecipe?.let { shareRecipe(it) } }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        // Prevent the WebView from reading local files or content:// URIs
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false

        webView.addJavascriptInterface(Bridge(), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (!extractionDone) {
                    // Slight delay lets JS-rendered content settle before extraction
                    view.postDelayed({ injectExtractor() }, 600)
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) progressBar.visibility = View.GONE
            }
        }

        saveBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                RecipeStorage.save(this, recipe)
                saveBtn.text = "Saved ✓"
                saveBtn.isEnabled = false
            }
        }

        // Mode A: view a saved recipe by its savedAt timestamp
        val savedAt = intent.getLongExtra("saved_at", -1L)
        if (savedAt > 0) {
            val recipe = RecipeStorage.getById(this, savedAt)
            if (recipe != null) {
                displayRecipe(recipe, isSaved = true)
            } else {
                finish()
            }
            return
        }

        // Mode B: extract a recipe from a URL
        val url = intent.getStringExtra("url") ?: run { finish(); return }
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            finish()
            return
        }
        webView.loadUrl(url)
    }

    private inner class Bridge {
        @JavascriptInterface
        fun onRecipeExtracted(json: String?) {
            runOnUiThread {
                extractionDone = true
                if (json == null) {
                    Toast.makeText(this@RecipeActivity,
                        "No recipe found on this page.", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }
                try {
                    val recipe = RecipeStorage.fromJson(JSONObject(json))
                    currentRecipe = recipe
                    displayRecipe(recipe, isSaved = false)
                } catch (_: Exception) {
                    Toast.makeText(this@RecipeActivity,
                        "Could not parse recipe data.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun injectExtractor() {
        val js = assets.open("extractor.js").bufferedReader().readText()
        webView.evaluateJavascript(js, null)
    }

    private fun displayRecipe(recipe: Recipe, isSaved: Boolean) {
        progressBar.visibility = View.GONE
        saveBtn.visibility     = if (isSaved) View.GONE else View.VISIBLE
        shareBtn.visibility    = View.VISIBLE

        if (isSaved && recipe.notes.isNotEmpty()) {
            notesInput.visibility = View.VISIBLE
            notesInput.setText(recipe.notes)
            notesInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    RecipeStorage.updateNotes(this, recipe.savedAt, notesInput.text.toString())
                }
            }
        } else if (isSaved) {
            notesInput.visibility = View.VISIBLE
            notesInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    RecipeStorage.updateNotes(this, recipe.savedAt, notesInput.text.toString())
                }
            }
        }

        val html = buildHtml(recipe)
        val baseUrl = recipe.sourceUrl.takeIf {
            it.startsWith("https://") || it.startsWith("http://")
        } ?: "about:blank"
        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
    }

    private fun buildHtml(recipe: Recipe): String {
        val timing = buildList {
            if (recipe.timingPrep.isNotEmpty())  add("Prep: ${formatDuration(recipe.timingPrep)}")
            if (recipe.timingCook.isNotEmpty())  add("Cook: ${formatDuration(recipe.timingCook)}")
            if (recipe.timingTotal.isNotEmpty()) add("Total: ${formatDuration(recipe.timingTotal)}")
        }.joinToString(" &nbsp;·&nbsp; ")

        val ingredients   = recipe.ingredients.joinToString("") { "<li>${it.esc()}</li>" }
        val equipment     = recipe.equipment.joinToString("") { "<li>${it.esc()}</li>" }
        val instructions  = buildInstructionsHtml(recipe.instructions)
        // Only allow http/https in the source link — reject javascript:, data:, etc.
        val safeSourceUrl = recipe.sourceUrl.takeIf {
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
  ${if (recipe.imageUrl.isNotEmpty()) """<img class="hero" src="${recipe.imageUrl.esc()}" alt="${recipe.title.esc()}">""" else ""}
  <h1>${recipe.title.esc()}</h1>
  ${if (timing.isNotEmpty())          """<p class="timing">$timing</p>""" else ""}
  ${if (recipe.recipeYield.isNotEmpty()) """<p class="yield"><strong>Yield:</strong> ${recipe.recipeYield.esc()}</p>""" else ""}
  ${if (recipe.description.isNotEmpty()) """<p>${recipe.description.esc()}</p>""" else ""}
  ${if (recipe.ingredients.isNotEmpty()) """<h2>Ingredients</h2><ul>$ingredients</ul>""" else ""}
  ${if (recipe.equipment.isNotEmpty())   """<h2>Equipment</h2><ul>$equipment</ul>""" else ""}
  ${if (recipe.instructions.isNotEmpty()) """<h2>Instructions</h2>$instructions""" else ""}
  ${if (recipe.authorNotes.isNotEmpty()) """<div class="notes"><strong>Author Notes</strong>${recipe.authorNotes.esc()}</div>""" else ""}
  ${if (safeSourceUrl.isNotEmpty()) """<p class="source">Source: <a href="${safeSourceUrl.esc()}">${safeSourceUrl.esc()}</a></p>""" else ""}
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

    private fun formatDuration(iso: String): String {
        if (!iso.startsWith("PT")) return iso
        val hours   = Regex("(\\d+)H").find(iso)?.groupValues?.get(1)
        val minutes = Regex("(\\d+)M").find(iso)?.groupValues?.get(1)
        val parts   = mutableListOf<String>()
        if (hours   != null) parts.add("$hours hr")
        if (minutes != null) parts.add("$minutes min")
        return parts.joinToString(" ").ifEmpty { iso }
    }

    private fun shareRecipe(recipe: Recipe) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, recipe.title)
            putExtra(Intent.EXTRA_TEXT, buildShareText(recipe))
        }
        startActivity(Intent.createChooser(intent, "Share recipe via…"))
    }

    private fun buildShareText(recipe: Recipe): String {
        val sb = StringBuilder()

        sb.appendLine(recipe.title)
        sb.appendLine()

        val timing = buildList {
            if (recipe.timingPrep.isNotEmpty())  add("Prep: ${formatDuration(recipe.timingPrep)}")
            if (recipe.timingCook.isNotEmpty())  add("Cook: ${formatDuration(recipe.timingCook)}")
            if (recipe.timingTotal.isNotEmpty()) add("Total: ${formatDuration(recipe.timingTotal)}")
        }
        if (timing.isNotEmpty())           sb.appendLine(timing.joinToString(" · "))
        if (recipe.recipeYield.isNotEmpty()) sb.appendLine("Yield: ${recipe.recipeYield}")
        if (timing.isNotEmpty() || recipe.recipeYield.isNotEmpty()) sb.appendLine()

        if (recipe.description.isNotEmpty()) {
            sb.appendLine(recipe.description)
            sb.appendLine()
        }

        if (recipe.ingredients.isNotEmpty()) {
            sb.appendLine("INGREDIENTS")
            recipe.ingredients.forEach { sb.appendLine("• $it") }
            sb.appendLine()
        }

        if (recipe.equipment.isNotEmpty()) {
            sb.appendLine("EQUIPMENT")
            recipe.equipment.forEach { sb.appendLine("• $it") }
            sb.appendLine()
        }

        if (recipe.instructions.isNotEmpty()) {
            sb.appendLine("INSTRUCTIONS")
            var stepNum = 1
            recipe.instructions.forEach { step ->
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

        if (recipe.authorNotes.isNotEmpty()) {
            sb.appendLine("NOTES")
            sb.appendLine(recipe.authorNotes)
            sb.appendLine()
        }

        if (recipe.notes.isNotEmpty()) {
            sb.appendLine("MY NOTES")
            sb.appendLine(recipe.notes)
            sb.appendLine()
        }

        val safeUrl = recipe.sourceUrl.takeIf {
            it.startsWith("https://") || it.startsWith("http://")
        }
        if (safeUrl != null) sb.append("Source: $safeUrl")

        return sb.toString().trimEnd()
    }

    private fun String.esc(): String = this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}
