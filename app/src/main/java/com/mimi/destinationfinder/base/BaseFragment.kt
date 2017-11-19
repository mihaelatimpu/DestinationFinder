package com.mimi.destinationfinder.base

import android.support.annotation.StringRes
import android.widget.Toast
import com.mimi.destinationfinder.R
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import org.koin.android.contextaware.ContextAwareFragment

/**
 * Created by Mimi on 08/11/2017.
 *
 */
abstract class BaseFragment : ContextAwareFragment() {
    private val loadingDialog by lazy {
        indeterminateProgressDialog(R.string.loading_data, R.string.please_wait)
    }

    fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    fun toast(@StringRes text: Int) {
        toast(getText(text))
    }

    fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}