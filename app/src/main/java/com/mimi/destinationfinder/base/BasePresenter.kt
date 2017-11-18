package com.mimi.destinationfinder.base

/**
 * Created by Mimi on 02/11/2017.
 *
 */
interface BasePresenter<T>{

    var view: T
    fun start()
}