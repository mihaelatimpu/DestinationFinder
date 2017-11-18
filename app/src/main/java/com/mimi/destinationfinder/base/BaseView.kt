package com.mimi.destinationfinder.base

import android.content.Context
import android.support.annotation.StringRes

/**
 * Created by Mimi on 02/11/2017.
 * The base view
 */
interface BaseView<out T : BasePresenter<*>> {
    val presenter: T
    var isActive: Boolean
    fun getContext(): Context
    fun showLoadingDialog()
    fun hideLoadingDialog()
    fun init()
    fun toast(@StringRes text:Int)
    fun toast(text:String)
}
