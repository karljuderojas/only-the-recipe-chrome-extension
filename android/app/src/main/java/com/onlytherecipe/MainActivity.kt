package com.onlytherecipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var recipeList: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var recipeAdapter: RecipeAdapter

    private lateinit var libraryContainer: LinearLayout
    private lateinit var groceryContainer: LinearLayout
    private lateinit var groceryList: RecyclerView
    private lateinit var groceryEmptyView: TextView
    private lateinit var groceryAdapter: GroceryAdapter
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput          = findViewById(R.id.urlInput)
        recipeList        = findViewById(R.id.recipeList)
        emptyView         = findViewById(R.id.emptyView)
        libraryContainer  = findViewById(R.id.libraryContainer)
        groceryContainer  = findViewById(R.id.groceryContainer)
        groceryList       = findViewById(R.id.groceryList)
        groceryEmptyView  = findViewById(R.id.groceryEmptyView)
        bottomNav         = findViewById(R.id.bottomNav)

        recipeAdapter = RecipeAdapter(
            onClick  = { recipe -> openRecipe(savedAt = recipe.savedAt) },
            onShare  = { recipe -> shareRecipe(recipe) },
            onDelete = { recipe ->
                RecipeStorage.delete(this, recipe.savedAt)
                refreshLibrary()
            }
        )
        recipeList.layoutManager = LinearLayoutManager(this)
        recipeList.adapter = recipeAdapter

        groceryAdapter = GroceryAdapter(
            onToggle = { item ->
                GroceryStorage.toggleChecked(this, item.id)
                refreshGrocery()
            },
            onDelete = { item ->
                GroceryStorage.deleteItem(this, item.id)
                refreshGrocery()
            }
        )
        groceryList.layoutManager = LinearLayoutManager(this)
        groceryList.adapter = groceryAdapter

        findViewById<Button>(R.id.goBtn).setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) openRecipe(url = url)
        }
        urlInput.setOnEditorActionListener { _, _, _ ->
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) openRecipe(url = url)
            true
        }

        findViewById<Button>(R.id.clearDoneBtn).setOnClickListener {
            GroceryStorage.clearChecked(this)
            refreshGrocery()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> { showLibrary(); true }
                R.id.nav_grocery -> { showGrocery(); true }
                else -> false
            }
        }

        handleShareIntent(intent)

        UpdateChecker.check(this) { latestTag, apkUrl ->
            AlertDialog.Builder(this)
                .setTitle("Update available")
                .setMessage("Version $latestTag is ready to install.")
                .setPositiveButton("Download") { _, _ ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)))
                }
                .setNegativeButton("Later", null)
                .show()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshLibrary()
        if (groceryContainer.visibility == View.VISIBLE) refreshGrocery()
    }

    private fun showLibrary() {
        libraryContainer.visibility = View.VISIBLE
        groceryContainer.visibility = View.GONE
        refreshLibrary()
    }

    private fun showGrocery() {
        libraryContainer.visibility = View.GONE
        groceryContainer.visibility = View.VISIBLE
        refreshGrocery()
    }

    private fun refreshLibrary() {
        val recipes = RecipeStorage.loadAll(this)
        recipeAdapter.submitList(recipes)
        emptyView.visibility  = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        recipeList.visibility = if (recipes.isEmpty()) View.GONE    else View.VISIBLE
    }

    private fun refreshGrocery() {
        val items = GroceryStorage.loadAll(this)
        groceryAdapter.submitList(items)
        groceryEmptyView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        groceryList.visibility      = if (items.isEmpty()) View.GONE    else View.VISIBLE
    }

    private fun handleShareIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND || intent.type != "text/plain") return
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
        val url  = extractUrl(text) ?: return
        openRecipe(url = url, fromShare = true)
    }

    private fun shareRecipe(recipe: Recipe) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, recipe.title)
            putExtra(Intent.EXTRA_TEXT, recipe.toShareText())
        }
        startActivity(Intent.createChooser(intent, "Share recipe via…"))
    }

    private fun openRecipe(url: String? = null, savedAt: Long? = null, fromShare: Boolean = false) {
        val intent = Intent(this, RecipeActivity::class.java)
        if (url     != null) intent.putExtra("url",      url)
        if (savedAt != null) intent.putExtra("saved_at", savedAt)
        startActivity(intent)
        if (fromShare) finish()
    }

    private fun extractUrl(text: String): String? {
        val match = Regex("https?://[^\\s\"<>]+").find(text) ?: return null
        return match.value.trimEnd('.', ',', ')', ']', ';')
    }
}
