package com.mimi.destinationfinder.settings

/**
 * Created by Mimi on 18/11/2017.
 *
 */
class SettingsPresenter : SettingsContract.Presenter {
    override lateinit var view: SettingsContract.View

    override fun start() {
        view.init()
    }

}