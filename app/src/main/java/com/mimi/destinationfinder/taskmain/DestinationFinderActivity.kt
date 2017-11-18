package com.mimi.destinationfinder.taskmain

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.settings.SettingsActivity
import com.mimi.destinationfinder.utils.replaceFragmentInActivity
import org.koin.android.ext.android.inject

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DestinationFinderActivity : AppCompatActivity() {
    companion object {
        val SETTINGS_ACTIVITY = 34564
    }

    private var mVisible: Boolean = false
    private val fragment: MainFragment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_destination_finder)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        mVisible = true

        supportFragmentManager.findFragmentById(R.id.contentFrame) as MainFragment?
                ?: fragment.also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
            = when (item.itemId) {
        R.id.action_settings -> {
            startSettingsActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun startSettingsActivity() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), SETTINGS_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }
}
