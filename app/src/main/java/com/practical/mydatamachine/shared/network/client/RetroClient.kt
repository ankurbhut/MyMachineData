package com.practical.mydatamachine.shared.network.client

import android.util.Log
import com.practical.mydatamachine.BuildConfig
import com.practical.mydatamachine.localcache.LocalDataHelper
import com.practical.mydatamachine.shared.extension.justTry
import com.practical.mydatamachine.shared.network.ApiEndpoints
import com.practical.mydatamachine.shared.network.security.EncryptionInterceptor
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


object RetroClient {
    private val requestLogInterceptor: Interceptor
        get() {
            val logging = HttpLoggingInterceptor { }
            logging.setLevel(HttpLoggingInterceptor.Level.BODY.takeIf { BuildConfig.DEBUG }
                ?: HttpLoggingInterceptor.Level.NONE)
            return logging
        }

    private val headerLogInterceptor: Interceptor
        get() {
            val logging = HttpLoggingInterceptor { message -> Log.e("API", message) }
            logging.setLevel(HttpLoggingInterceptor.Level.BODY.takeIf { BuildConfig.DEBUG }
                ?: HttpLoggingInterceptor.Level.NONE)
            return logging
        }

    private val builder = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
        .connectTimeout(80, TimeUnit.SECONDS).writeTimeout(80, TimeUnit.SECONDS)
        .readTimeout(80, TimeUnit.SECONDS)

    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
//        request.addHeader(ApiEndpoints.HEADER_CURRENCY, "USD")
//        request.addHeader("Connection", "close")
        request.addHeader(ApiEndpoints.HEADER_VERSION, BuildConfig.VERSION_NAME)
        request.addHeader(ApiEndpoints.HEADER_PACKAGE_NAME, BuildConfig.APPLICATION_ID)
        chain.proceed(request.build())
    }

    private val forbiddenInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {

        }
        response
    }

    val okHttpClient =
        builder.addNetworkInterceptor(headerLogInterceptor).addInterceptor(requestLogInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(forbiddenInterceptor).build()


}