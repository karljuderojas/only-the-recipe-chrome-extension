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
    private lateinit var shareBtn: Button
    private lateinit var groceryBtn: Button
    private lateinit var convertBtn: Button
    private lateinit var notesInput: EditText

    private var currentRecipe: Recipe? = null
    private var extractionDone = false
    private var isMetric = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        webView     = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        saveBtn     = findViewById(R.id.saveBtn)
        shareBtn    = findViewById(R.id.shareBtn)
        groceryBtn  = findViewById(R.id.groceryBtn)
        convertBtn  = findViewById(R.id.convertBtn)
        notesInput  = findViewById(R.id.notesInput)

        findViewById<Button>(R.id.backBtn).setOnClickListener { finish() }

        shareBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, recipe.title)
                    putExtra(Intent.EXTRA_TEXT, recipe.toShareText())
                }
                startActivity(Intent.createChooser(intent, "Share recipe via…"))
            }
        }

        groceryBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                if (recipe.ingredients.isEmpty()) {
                    Toast.makeText(this, "No ingredients found.", Toast.LENGTH_SHORT).show()
                } else {
                    GroceryStorage.addItems(this, recipe.ingredients)
                    Toast.makeText(this,
                        "${recipe.ingredients.size} item(s) added to grocery list.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        convertBtn.setOnClickListener {
            isMetric = !isMetric
            convertBtn.text = if (isMetric) "Imperial" else "Metric"
            renderHtml()
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
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
            extractionDone = true  // prevent extractor firing on the rendered HTML
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
        currentRecipe = recipe
        progressBar.visibility = View.GONE
        saveBtn.visibility     = if (isSaved) View.GONE else View.VISIBLE
        shareBtn.visibility    = View.VISIBLE
        groceryBtn.visibility  = if (recipe.ingredients.isEmpty()) View.GONE else View.VISIBLE
        convertBtn.visibility  = View.VISIBLE

        if (isSaved) {
            notesInput.visibility = View.VISIBLE
            notesInput.setText(recipe.notes)
            notesInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    RecipeStorage.updateNotes(this, recipe.savedAt, notesInput.text.toString())
                }
            }
        }

        renderHtml()
    }

    private fun renderHtml() {
        val recipe = currentRecipe ?: return
        val displayed = if (isMetric) recipe.withUnits(toMetric = true) else recipe
        val baseUrl = displayed.sourceUrl.takeIf {
            it.startsWith("https://") || it.startsWith("http://")
        } ?: "about:blank"
        webView.loadDataWithBaseURL(baseUrl, displayed.toHtml(), "text/html", "UTF-8", null)
    }
}
