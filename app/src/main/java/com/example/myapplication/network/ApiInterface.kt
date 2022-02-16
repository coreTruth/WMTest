package com.example.myapplication.network

import com.example.myapplication.data.FlickerSearchPhotoResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiInterface {
    @GET("rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    suspend fun searchPhotos(@Query("api_key") apiKey: String = testApiKey, @Query("text") text: String, @Query("page") page: Int, @Query("per_page") perPage: Int = 25): FlickerSearchPhotoResponse?

    companion object {

        var BASE_URL = "https://www.flickr.com/services/"
        var testApiKey = "1508443e49213ff84d566777dc211f2a"
        fun create(): ApiInterface {
            val okhttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .apply { addOptionalLogging() }
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okhttpClient)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }

        private fun OkHttpClient.Builder.addOptionalLogging() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            addInterceptor(logging)
        }
    }
}