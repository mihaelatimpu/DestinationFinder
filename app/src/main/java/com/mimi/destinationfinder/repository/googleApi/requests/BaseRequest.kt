package com.mimi.destinationfinder.repository.googleApi.requests

import com.mimi.destinationfinder.repository.googleApi.RetrofitInterface
import com.mimi.destinationfinder.repository.googleApi.responses.BaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Mimi on 17/11/2017.
 *
 */
abstract class BaseRequest<T : BaseResponse> : Callback<T> {
    companion object {
        val RESPONSE_OK = "OK"
    }
    fun start(api: RetrofitInterface, apiKey: String){
        call(api, apiKey).enqueue(this)
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        onError(t)
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        if (response == null || !response.isSuccessful || response.body() == null) {
            onError(Throwable("UNKNOWN ERROR"))
        } else {
            val resp = response.body()!!
            if (resp.status != RESPONSE_OK) {
                onError(Throwable(resp.status))
                return
            }
            onCompleted(resp)
        }
    }

    abstract protected fun call(api: RetrofitInterface, apiKey: String):Call<T>

    abstract protected fun onError(t: Throwable?)
    abstract protected fun onCompleted(result: T)

}