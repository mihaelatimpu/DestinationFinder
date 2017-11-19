package com.mimi.destinationfinder.settings

import com.mimi.destinationfinder.dto.Settings
import com.mimi.destinationfinder.repository.SharedPreferenceUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class SettingsPresenter : SettingsContract.Presenter {
    override lateinit var view: SettingsContract.View

    override fun start() {
        view.init()
        loadSettingsFromSharedPreferences()
    }
    private fun loadSettingsFromSharedPreferences(){
        view.showLoadingDialog()
        var settings:Settings?= null
        doAsync {
            try{
                settings = SharedPreferenceUtils.retrieveSettings(view.getContext())
            } catch (e:Exception){
                e.printStackTrace()
            }
            onComplete {
                if(settings != null)
                    view.refreshSettings(settings!!)
                view.hideLoadingDialog()
            }
        }
    }

    override fun saveSettings() {
        val settings = view.collectSettings()
        view.showLoadingDialog()
        doAsync {
            SharedPreferenceUtils.saveSettings(view.getContext(),settings)
            onComplete {
                view.hideLoadingDialog()
                view.exit()
            }
        }
    }

}