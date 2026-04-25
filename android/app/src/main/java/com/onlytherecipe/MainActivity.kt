package com.onlytherecipe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var recipeList: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput   = findViewById(R.id.urlInput)
        recipeList = findViewById(R.id.recipeList)
        emptyView  = findViewById(R.id.emptyView)

        adapter = RecipeAdapter(
            onClick  = { recipe -> openRecipe(savedAt = recipe.savedAt) },
            onDelete = { recipe ->
                RecipeStorage.delete(this, recipe.savedAt)
                refreshList()
            }
        )
        recipeList.layoutManager = LinearLayoutManager(this)
        recipeList.adapter = adapter

        findViewById<Button>(R.id.goBtn).setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) openRecipe(url = url)
        }

        urlInput.setOnEditorActionListener { _, _, _ ->
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) openRecipe(url = url)
            true
        }

        // Share intent received while app was not running
        handleShareIntent(intent)
    }

    // Called when app is already running (singleTop) and user shares again
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun handleShareIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND || intent.type != "text/plain") return
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
        val url  = extractUrl(text) ?: return
        // Launch RecipeActivity and finish this one so Back returns to the browser, not the library
        openRecipe(url = url, fromShare = true)
    }

    private fun refreshList() {
        val recipes = RecipeStorage.loadAll(this)
        adapter.submitList(recipes)
        emptyView.visibility  = if (recipes.isEmpty()) View.VISIBLE else View.GONE
        recipeList.visibility = if (recipes.isEmpty()) View.GONE    else View.VISIBLE
    }

    private fun openRecipe(url: String? = null, savedAt: Long? = null, fromShare: Boolean = false) {
        val intent = Intent(this, RecipeActivity::class.java)
        if (url     != null) intent.putExtra("url",      url)
        if (savedAt != null) intent.putExtra("saved_at", savedAt)
        startActivity(intent)
        // When triggered by share, remove MainActivity from the back stack so
        // pressing Back in RecipeActivity returns to the browser, not the library.
        if (fromShare) finish()
    }

    private fun extractUrl(text: String): String? {
        // Chrome shares as "Page Title\nhttps://..." or just the URL
        val match = Regex("https?://[^\\s\"<>]+").find(text) ?: return null
        return match.value.trimEnd('.', ',', ')', ']', ';')
    }
}
