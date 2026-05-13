package com.onlytherecipe

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class RecipeActivity : AppCompatActivity() {

    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val editedSavedAt = result.data?.getLongExtra("saved_at", -1L) ?: -1L
            if (editedSavedAt > 0) {
                val reloaded = RecipeStorage.getById(this, editedSavedAt)
                if (reloaded != null) {
                    currentRecipe = reloaded
                    populateRecipeViews(reloaded, isSaved = true)
                }
            } else {
                // New recipe just saved — Mode B → Mode A transition
                val justSaved = currentRecipe?.let { c ->
                    RecipeStorage.loadAll(this).firstOrNull { it.sourceUrl == c.sourceUrl }
                }
                if (justSaved != null) {
                    currentRecipe = justSaved
                    populateRecipeViews(justSaved, isSaved = true)
                } else {
                    saveBtn.text = "Saved ✓"
                    saveBtn.isEnabled = false
                }
            }
        }
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var recipeScroll: View
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var backBtn: TextView
    private lateinit var saveBtn: TextView
    private lateinit var shareBtn: TextView
    private lateinit var editBtn: TextView
    private lateinit var groceryBtn: TextView

    private lateinit var heroFrame: FrameLayout
    private lateinit var heroImage: ImageView
    private lateinit var heroPlaceholderLabel: TextView

    private lateinit var kicker: TextView
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var attributionView: TextView

    private lateinit var timingStrip: View
    private lateinit var timingPrepValue: TextView
    private lateinit var timingCookValue: TextView
    private lateinit var timingTotalValue: TextView

    private lateinit var ingredientsSection: View
    private lateinit var ingredientsList: LinearLayout
    private lateinit var equipmentSection: View
    private lateinit var equipmentList: LinearLayout
    private lateinit var methodSection: View
    private lateinit var methodList: LinearLayout
    private lateinit var noteCard: View
    private lateinit var noteBody: TextView

    private lateinit var nutritionSection: View
    private lateinit var nutritionGrid: LinearLayout
    private lateinit var nutritionServingSize: TextView

    private lateinit var sourceSection: View
    private lateinit var sourceUrlView: TextView

    private lateinit var myNotesSection: View
    private lateinit var notesInput: EditText

    private var currentRecipe: Recipe? = null
    private var extractionDone = false
    private var extractionWebView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        applySystemBarInsets()

        bindViews()
        wireToolbar()
        wireBottomNav()

        // Mode A: view a saved recipe
        val savedAt = intent.getLongExtra("saved_at", -1L)
        if (savedAt > 0) {
            extractionDone = true
            val recipe = RecipeStorage.getById(this, savedAt)
            if (recipe != null) {
                currentRecipe = recipe
                populateRecipeViews(recipe, isSaved = true)
            } else {
                finish()
            }
            return
        }

        // Mode B: extract from URL via an offscreen WebView
        val url = intent.getStringExtra("url") ?: run { finish(); return }
        if (!url.isSafeUrl()) {
            finish()
            return
        }
        recipeScroll.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        startExtraction(url)
    }

    private fun bindViews() {
        rootFrame             = findViewById(R.id.rootFrame)
        progressBar           = findViewById(R.id.progressBar)
        recipeScroll          = findViewById(R.id.recipeScroll)
        bottomNav             = findViewById(R.id.bottomNav)

        backBtn    = findViewById(R.id.backBtn)
        saveBtn    = findViewById(R.id.saveBtn)
        shareBtn   = findViewById(R.id.shareBtn)
        editBtn    = findViewById(R.id.editBtn)
        groceryBtn = findViewById(R.id.groceryBtn)

        heroFrame             = findViewById(R.id.heroFrame)
        heroImage             = findViewById(R.id.heroImage)
        heroPlaceholderLabel  = findViewById(R.id.heroPlaceholderLabel)

        kicker          = findViewById(R.id.recipeKicker)
        titleView       = findViewById(R.id.recipeTitle)
        descriptionView = findViewById(R.id.recipeDescription)
        attributionView = findViewById(R.id.recipeAttribution)

        timingStrip       = findViewById(R.id.timingStrip)
        timingPrepValue   = findViewById(R.id.timingPrepValue)
        timingCookValue   = findViewById(R.id.timingCookValue)
        timingTotalValue  = findViewById(R.id.timingTotalValue)

        ingredientsSection = findViewById(R.id.ingredientsSection)
        ingredientsList    = findViewById(R.id.ingredientsList)
        equipmentSection   = findViewById(R.id.equipmentSection)
        equipmentList      = findViewById(R.id.equipmentList)
        methodSection      = findViewById(R.id.methodSection)
        methodList         = findViewById(R.id.methodList)
        noteCard           = findViewById(R.id.noteCard)
        noteBody           = findViewById(R.id.noteBody)

        nutritionSection      = findViewById(R.id.nutritionSection)
        nutritionGrid         = findViewById(R.id.nutritionGrid)
        nutritionServingSize  = findViewById(R.id.nutritionServingSize)

        sourceSection  = findViewById(R.id.sourceSection)
        sourceUrlView  = findViewById(R.id.sourceUrl)

        myNotesSection = findViewById(R.id.myNotesSection)
        notesInput     = findViewById(R.id.notesInput)
    }

    private fun wireToolbar() {
        backBtn.setOnClickListener { finish() }

        saveBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                EditRecipeActivity.pendingRecipe = recipe
                editLauncher.launch(Intent(this, EditRecipeActivity::class.java))
            }
        }
        editBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                EditRecipeActivity.pendingRecipe = recipe
                editLauncher.launch(Intent(this, EditRecipeActivity::class.java))
            }
        }
        shareBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                startActivity(Intent.createChooser(recipe.toShareIntent(), "Share recipe via…"))
            }
        }
        groceryBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                if (recipe.ingredients.isEmpty()) {
                    Toast.makeText(this, "No ingredients found.", Toast.LENGTH_SHORT).show()
                } else {
                    GroceryStorage.addItems(
                        context = this,
                        items = recipe.ingredients,
                        recipeSavedAt = recipe.savedAt.takeIf { it > 0 },
                        recipeName = recipe.title
                    )
                    Toast.makeText(this,
                        "${recipe.ingredients.size} item(s) added to grocery list.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun wireBottomNav() {
        // The user is here from Library, so Recipes is the "active" tab.
        bottomNav.selectedItemId = R.id.nav_library
        bottomNav.setOnItemSelectedListener { item ->
            val tab = when (item.itemId) {
                R.id.nav_library -> "library"
                R.id.nav_grocery -> "grocery"
                else -> return@setOnItemSelectedListener false
            }
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra("tab", tab)
            startActivity(intent)
            finish()
            true
        }
    }

    // ─── Mode B: offscreen WebView extraction ─────────────────────────────

    @SuppressLint("SetJavaScriptEnabled")
    private fun startExtraction(url: String) {
        val webView = WebView(this).apply {
            settings.javaScriptEnabled    = true
            settings.domStorageEnabled    = true
            settings.allowFileAccess      = false
            settings.allowContentAccess   = false
            alpha = 0f
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, finishedUrl: String) {
                    if (!extractionDone) {
                        view.postDelayed({ injectExtractor(view) }, 600)
                    }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    progressBar.progress = newProgress
                }
            }
            addJavascriptInterface(Bridge(), "Android")
        }
        // Full-size so the page loads at a normal mobile viewport (some recipe
        // sites use viewport-aware JS and won't deliver content to a 1×1 frame).
        // Added at index 0 so the LinearLayout draws on top — combined with
        // alpha=0 the WebView is invisible but functional.
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        rootFrame.addView(webView, 0, params)
        extractionWebView = webView
        webView.loadUrl(url)
    }

    private fun injectExtractor(webView: WebView) {
        val js = assets.open("extractor.js").bufferedReader().readText()
        webView.evaluateJavascript(js, null)
    }

    private inner class Bridge {
        @JavascriptInterface
        fun onRecipeExtracted(json: String?) {
            runOnUiThread {
                extractionDone = true
                extractionWebView?.let { wv ->
                    wv.removeJavascriptInterface("Android")
                    rootFrame.removeView(wv)
                    wv.destroy()
                }
                extractionWebView = null
                progressBar.visibility = View.GONE
                recipeScroll.visibility = View.VISIBLE

                if (json == null) {
                    Toast.makeText(this@RecipeActivity,
                        "No recipe found on this page.", Toast.LENGTH_LONG).show()
                    finish()
                    return@runOnUiThread
                }
                try {
                    val recipe = RecipeStorage.fromJson(JSONObject(json))
                    currentRecipe = recipe
                    populateRecipeViews(recipe, isSaved = false)
                } catch (_: Exception) {
                    Toast.makeText(this@RecipeActivity,
                        "Could not parse recipe data.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    // ─── Native population ────────────────────────────────────────────────

    private fun populateRecipeViews(recipe: Recipe, isSaved: Boolean) {
        val display = if (AppPrefs.useMetric(this))
            with(RecipeConverter) { recipe.withUnits(toMetric = true) }
        else recipe

        // Toolbar visibility — saved recipe gets Edit + Share + List; new
        // extraction gets Save instead of Edit.
        saveBtn.visibility    = if (isSaved) View.GONE else View.VISIBLE
        editBtn.visibility    = if (isSaved) View.VISIBLE else View.GONE
        shareBtn.visibility   = View.VISIBLE
        groceryBtn.visibility = if (display.ingredients.isEmpty()) View.GONE else View.VISIBLE
        if (!isSaved) {
            saveBtn.text = "Save"
            saveBtn.isEnabled = true
        }

        bindHero(display)
        bindTitleBlock(display)
        bindTiming(display)
        bindIngredients(display.ingredients)
        bindEquipment(display.equipment)
        bindMethod(display.instructions, display.authorNotes)
        bindNutrition(display.nutrition)
        bindSource(display)
        bindMyNotes(display, isSaved)
    }

    private fun bindSource(recipe: Recipe) {
        if (!recipe.sourceUrl.isSafeUrl()) {
            sourceSection.visibility = View.GONE
            return
        }
        sourceSection.visibility = View.VISIBLE
        sourceUrlView.text = recipe.sourceUrl
        sourceUrlView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(recipe.sourceUrl)))
        }
    }

    private fun bindHero(recipe: Recipe) {
        heroImage.setImageDrawable(null)
        heroPlaceholderLabel.visibility = View.GONE
        if (recipe.imageUrl.isBlank()) {
            heroPlaceholderLabel.visibility = View.VISIBLE
            return
        }
        ImageLoader.loadInto(heroImage, recipe.imageUrl) {
            heroPlaceholderLabel.visibility = View.VISIBLE
        }
    }

    private fun bindTitleBlock(recipe: Recipe) {
        titleView.text = recipe.title.ifEmpty { recipe.sourceUrl }

        val kickerText = recipe.recipeYield.takeIf { it.isNotBlank() }?.uppercase().orEmpty()
        kicker.text = kickerText
        kicker.visibility = if (kickerText.isBlank()) View.GONE else View.VISIBLE

        descriptionView.text = recipe.description
        descriptionView.visibility = if (recipe.description.isBlank()) View.GONE else View.VISIBLE

        val domain = deriveDomain(recipe.sourceUrl)
        attributionView.text = if (domain.isNotBlank()) "via $domain" else ""
        attributionView.visibility = if (domain.isBlank()) View.GONE else View.VISIBLE
        attributionView.setOnClickListener {
            if (recipe.sourceUrl.isSafeUrl()) {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(recipe.sourceUrl)))
            }
        }
    }

    private fun bindTiming(recipe: Recipe) {
        val prep  = recipe.timingPrep.fmt().takeIf { it.isNotBlank() }
        val cook  = recipe.timingCook.fmt().takeIf { it.isNotBlank() }
        val total = recipe.timingTotal.fmt().takeIf { it.isNotBlank() }
        if (prep == null && cook == null && total == null) {
            timingStrip.visibility = View.GONE
            return
        }
        timingStrip.visibility = View.VISIBLE
        timingPrepValue.text  = prep ?: "—"
        timingCookValue.text  = cook ?: "—"
        timingTotalValue.text = total ?: "—"
    }

    private fun bindIngredients(items: List<String>) {
        ingredientsList.removeAllViews()
        if (items.isEmpty()) {
            ingredientsSection.visibility = View.GONE
            return
        }
        ingredientsSection.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(this)
        items.forEachIndexed { index, item ->
            if (index > 0) ingredientsList.addView(createDottedDivider())
            val row = inflater.inflate(R.layout.item_ingredient, ingredientsList, false)
            val (qty, name) = splitIngredient(item)
            row.findViewById<TextView>(R.id.ingredientQty).text = qty
            row.findViewById<TextView>(R.id.ingredientName).text = name
            ingredientsList.addView(row)
        }
    }

    private fun bindEquipment(items: List<String>) {
        equipmentList.removeAllViews()
        if (items.isEmpty()) {
            equipmentSection.visibility = View.GONE
            return
        }
        equipmentSection.visibility = View.VISIBLE
        items.forEachIndexed { index, item ->
            if (index > 0) equipmentList.addView(createDottedDivider())
            val tv = TextView(this).apply {
                text = item
                setTextAppearance(R.style.TextAppearance_OTR_Body)
                setPadding(0, dpToPx(7), 0, dpToPx(7))
            }
            equipmentList.addView(tv, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
        }
    }

    private fun bindMethod(steps: List<String>, authorNotes: String) {
        methodList.removeAllViews()
        val inflater = LayoutInflater.from(this)
        if (steps.isEmpty()) {
            // If we have no steps but DO have an author note, still show the
            // section so the cook's note card renders.
            methodSection.visibility = if (authorNotes.isBlank()) View.GONE else View.VISIBLE
        } else {
            methodSection.visibility = View.VISIBLE
            var stepNum = 1
            for (step in steps) {
                if (step.startsWith("§:")) {
                    val sectionRow = inflater.inflate(R.layout.item_method_section, methodList, false)
                    sectionRow.findViewById<TextView>(R.id.sectionLabel).text = step.substring(2)
                    methodList.addView(sectionRow)
                    stepNum = 1
                } else {
                    val row = inflater.inflate(R.layout.item_method_step, methodList, false)
                    row.findViewById<TextView>(R.id.stepNumber).text = stepNum.toString()
                    row.findViewById<TextView>(R.id.stepBody).text = step
                    methodList.addView(row)
                    stepNum++
                }
            }
        }

        if (authorNotes.isBlank() || !AppPrefs.showAuthorNotes(this)) {
            noteCard.visibility = View.GONE
        } else {
            noteCard.visibility = View.VISIBLE
            noteBody.text = authorNotes
        }
    }

    private fun bindNutrition(n: Nutrition) {
        nutritionGrid.removeAllViews()
        if (n.isEmpty()) {
            nutritionSection.visibility = View.GONE
            return
        }
        nutritionSection.visibility = View.VISIBLE

        nutritionServingSize.text = if (n.servingSize.isNotBlank()) "per ${n.servingSize}" else ""
        nutritionServingSize.visibility = if (n.servingSize.isNotBlank()) View.VISIBLE else View.GONE

        val rows = listOf(
            "Calories"      to n.calories,
            "Protein"       to n.protein,
            "Carbohydrates" to n.carbohydrates,
            "Fat"           to n.fat,
            "Saturated fat" to n.saturatedFat,
            "Fiber"         to n.fiber,
            "Sugar"         to n.sugar,
            "Sodium"        to n.sodium,
            "Cholesterol"   to n.cholesterol,
        )
        val present = rows.filter { it.second.isNotBlank() }
        present.forEachIndexed { index, (label, value) ->
            if (index > 0) nutritionGrid.addView(createDottedDivider())
            nutritionGrid.addView(buildNutritionRow(label, value))
        }
    }

    private fun buildNutritionRow(label: String, value: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(7), 0, dpToPx(7))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val labelTv = TextView(this).apply {
            text = label
            setTextAppearance(R.style.TextAppearance_OTR_Body)
        }
        val spacer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
        val valueTv = TextView(this).apply {
            text = value
            setTextAppearance(R.style.TextAppearance_OTR_MonoNumber)
            setTextColor(getColor(R.color.otr_accent))
            textSize = 11f
        }
        row.addView(labelTv)
        row.addView(spacer)
        row.addView(valueTv)
        return row
    }

    private fun bindMyNotes(recipe: Recipe, isSaved: Boolean) {
        if (!isSaved) {
            myNotesSection.visibility = View.GONE
            return
        }
        myNotesSection.visibility = View.VISIBLE
        notesInput.setText(recipe.notes)
        notesInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                RecipeStorage.updateNotes(this, recipe.savedAt, notesInput.text.toString())
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private fun createDottedDivider(): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(1)
        )
        setBackgroundResource(R.drawable.divider_dotted_horizontal)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    private fun splitIngredient(text: String): Pair<String, String> {
        // Mirrors the regex in design/v1-editorial.jsx: leading numeric/fraction
        // tokens + up to 3 trailing word chars (often a unit abbreviation) form
        // the qty column. Imperfect for items with no unit but matches the
        // design's stated extraction logic.
        val regex = Regex("""^([\d¼½¾⅓⅔/.\s]+)(\w{0,3})\s+(.+)$""")
        val match = regex.find(text) ?: return Pair("", text)
        val qty = (match.groupValues[1] + match.groupValues[2]).trim()
        return Pair(qty, match.groupValues[3])
    }

    private fun deriveDomain(url: String): String {
        if (url.isBlank()) return ""
        return try {
            val host = java.net.URI(url).host ?: return ""
            if (host.startsWith("www.")) host.substring(4) else host
        } catch (_: Exception) { "" }
    }

    override fun onDestroy() {
        extractionWebView?.destroy()
        extractionWebView = null
        super.onDestroy()
    }
}
