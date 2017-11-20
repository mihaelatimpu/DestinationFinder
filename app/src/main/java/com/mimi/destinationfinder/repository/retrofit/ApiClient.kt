package com.mimi.destinationfinder.repository.retrofit

import android.content.Context
import android.content.pm.PackageManager
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Mimi on 17/11/2017.
 *
 */

object ApiClient {
    private val BASE_URL = "https://maps.googleapis.com/"
    val client by lazy {
        Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

    }
    fun getApiKey(context: Context)= context.packageManager.
            getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .metaData.get("com.google.android.geo.API_KEY") as String

    val googleApi by lazy { client.create(RetrofitInterface::class.java) }
}