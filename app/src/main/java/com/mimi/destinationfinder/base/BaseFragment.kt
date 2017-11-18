package com.mimi.destinationfinder.base

import android.support.annotation.StringRes
import com.mimi.destinationfinder.R
import org.jetbrains.anko.support.v4.progressDialog
import org.jetbrains.anko.support.v4.toast
import org.koin.android.contextaware.ContextAwareFragment

/**
 * Created by Mimi on 08/11/2017.
 *
 */
abstract class BaseFragment : ContextAwareFragment() {
    private val loadingDialog by lazy {
        progressDialog(R.string.loading_data, R.string.please_wait)
    }

    fun showLoadingDialog() {
        loadingDialog.isIndeterminate = true
        loadingDialog.show()
    }

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }
    fun toast(@StringRes text: Int){
        toast(getText(text))
    }
    fun toast(text: String) {
//super.toast(text)
    }


}