package com.onlytherecipe

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<Button>(R.id.settingsBackBtn).setOnClickListener { finish() }

        val metricSwitch = findViewById<SwitchCompat>(R.id.metricSwitch)
        metricSwitch.isChecked = AppPrefs.useMetric(this)
        metricSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppPrefs.setUseMetric(this, isChecked)
        }

        val version = try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionName ?: "—"
        } catch (_: Exception) { "—" }
        findViewById<TextView>(R.id.versionText).text = "Version $version"
    }
}
