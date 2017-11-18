package com.mimi.destinationfinder.utils

import com.mimi.destinationfinder.settings.SettingsContract
import com.mimi.destinationfinder.settings.SettingsFragment
import com.mimi.destinationfinder.settings.SettingsPresenter
import com.mimi.destinationfinder.taskmain.MainContract
import com.mimi.destinationfinder.taskmain.MainFragment
import com.mimi.destinationfinder.taskmain.MainPresenter
import com.mimi.destinationfinder.utils.Context.DestinationFinder
import com.mimi.destinationfinder.utils.Context.Settings
import org.koin.android.module.AndroidModule

/**
 * Created by Mimi on 17/11/2017.
 *
 */

fun appModules() = listOf(RecipeModule())
class RecipeModule: AndroidModule(){
    override fun context() = applicationContext {
        context(DestinationFinder){
            provide { MainFragment() }
            provide { MainPresenter() } bind MainContract.Presenter::class
        }
        context(Settings){
            provide { SettingsFragment() }
            provide { SettingsPresenter() } bind SettingsContract.Presenter::class
        }
    }
}
/**
 * Module constants
 */
object Context {
    val DestinationFinder = "DestinationFinder"
    val Settings = "Context"
}