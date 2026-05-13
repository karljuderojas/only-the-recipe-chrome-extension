package com.onlytherecipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private val privacyUrl =
        "https://github.com/karljuderojas/only-the-recipe-chrome-extension/blob/master/PRIVACY.md"

    private val servingOptions = listOf(0, 1, 2, 4, 6, 8, 12)

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val uri = result.data?.data ?: return@registerForActivityResult
        writeLibraryToUri(uri)
    }

    private lateinit var servingsValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        applySystemBarInsets()

        findViewById<View>(R.id.settingsBackBtn).setOnClickListener { finish() }

        wireMetricToggle()
        wireShowNotesToggle()
        wireServingsRow()
        wireExportRow()
        wireClearGroceryRow()
        wirePrivacyRow()
        wireBottomNav()
        renderVersionFooter()
    }

    private fun wireMetricToggle() {
        val switch = findViewById<SwitchCompat>(R.id.metricSwitch)
        switch.isChecked = AppPrefs.useMetric(this)
        switch.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setUseMetric(this, isChecked)
        }
    }

    private fun wireShowNotesToggle() {
        val switch = findViewById<SwitchCompat>(R.id.showNotesSwitch)
        switch.isChecked = AppPrefs.showAuthorNotes(this)
        switch.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setShowAuthorNotes(this, isChecked)
        }
    }

    private fun wireServingsRow() {
        servingsValue = findViewById(R.id.servingsValue)
        refreshServingsLabel()
        findViewById<View>(R.id.servingsRow).setOnClickListener {
            val current = AppPrefs.defaultServings(this)
            val labels = servingOptions.map { servingLabel(it) }.toTypedArray()
            val checkedIndex = servingOptions.indexOf(current).coerceAtLeast(0)
            AlertDialog.Builder(this)
                .setTitle("Default servings")
                .setSingleChoiceItems(labels, checkedIndex) { dialog, which ->
                    AppPrefs.setDefaultServings(this, servingOptions[which])
                    refreshServingsLabel()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun refreshServingsLabel() {
        val value = AppPrefs.defaultServings(this)
        servingsValue.text = "${servingLabel(value)} →"
    }

    private fun servingLabel(value: Int): String =
        if (value == AppPrefs.SERVINGS_AS_WRITTEN) "As written" else value.toString()

    private fun wireExportRow() {
        findViewById<View>(R.id.exportRow).setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "only-the-recipe-${System.currentTimeMillis()}.json")
            }
            exportLauncher.launch(intent)
        }
    }

    private fun writeLibraryToUri(uri: Uri) {
        try {
            val json = RecipeStorage.exportJson(this)
            contentResolver.openOutputStream(uri)?.use { out ->
                out.write(json.toByteArray(Charsets.UTF_8))
            }
            val count = RecipeStorage.loadAll(this).size
            Toast.makeText(this,
                "Exported $count recipe${if (count == 1) "" else "s"}.",
                Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun wireClearGroceryRow() {
        findViewById<View>(R.id.clearGroceryRow).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear grocery list?")
                .setMessage("This removes every item, checked or not. The action can't be undone.")
                .setPositiveButton("Clear") { _, _ ->
                    GroceryStorage.clearAll(this)
                    Toast.makeText(this, "Grocery list cleared.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun wirePrivacyRow() {
        findViewById<View>(R.id.privacyRow).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
        }
    }

    private fun wireBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        // No tab is "active" from Settings — the user arrived here from the
        // gear icon, not from a tab — but Library is the closest match since
        // tapping it returns to the start.
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

    private fun renderVersionFooter() {
        val version = try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionName ?: "—"
        } catch (_: Exception) { "—" }
        findViewById<TextView>(R.id.versionText).text = "Only the Recipe · v$version"
    }
}
