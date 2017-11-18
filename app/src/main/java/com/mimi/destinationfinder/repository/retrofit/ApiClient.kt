package com.mimi.destinationfinder.repository.retrofit

import android.content.Context
import android.content.pm.PackageManager
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


/**
 * Created by Mimi on 17/11/2017.
 *
 */

object ApiClient {
    private val BASE_URL = "https://maps.googleapis.com/"
    val client by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }
    fun getApiKey(context: Context)= context.packageManager.
            getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .metaData.get("com.google.android.geo.API_KEY") as String

    val googleApi by lazy { client.create(RetrofitInterface::class.java) }
}