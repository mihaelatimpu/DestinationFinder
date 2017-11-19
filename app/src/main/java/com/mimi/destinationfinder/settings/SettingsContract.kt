package com.mimi.destinationfinder.settings

import com.mimi.destinationfinder.base.BasePresenter
import com.mimi.destinationfinder.base.BaseView
import com.mimi.destinationfinder.dto.Settings

/**
 * Created by Mimi on 18/11/2017.
 *
 */
interface SettingsContract{
    interface View:BaseView<Presenter>{
        fun refreshSettings(settings: Settings)
        fun exit()
        fun collectSettings():Settings
    }
    interface Presenter:BasePresenter<View>{
        fun saveSettings()
    }
}