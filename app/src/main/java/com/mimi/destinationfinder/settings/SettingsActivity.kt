package com.mimi.destinationfinder.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.mimi.destinationfinder.R
import com.mimi.destinationfinder.taskmain.MainFragment
import com.mimi.destinationfinder.utils.replaceFragmentInActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.android.ext.android.inject

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SettingsActivity : AppCompatActivity() {
    private var mVisible: Boolean = false
    private val fragment: SettingsFragment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        supportFragmentManager.findFragmentById(R.id.contentFrame) as MainFragment?
                ?: fragment.also {
            replaceFragmentInActivity(it, R.id.contentFrame)
        }
    }


}
