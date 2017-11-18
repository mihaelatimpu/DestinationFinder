package com.mimi.destinationfinder.settings

import com.mimi.destinationfinder.base.BasePresenter
import com.mimi.destinationfinder.base.BaseView

/**
 * Created by Mimi on 18/11/2017.
 *
 */
interface SettingsContract{
    interface View:BaseView<Presenter>{

    }
    interface Presenter:BasePresenter<View>{

    }
}