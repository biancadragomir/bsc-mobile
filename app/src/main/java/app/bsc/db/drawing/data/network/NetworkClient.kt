package app.bsc.db.drawing.data.network

import android.content.Context
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient

object NetworkClient {
//    private val BASE_URL = "http://192.168.100.91:5000"
    private val BASE_URL = "https://alarmed-server2.herokuapp.com/"
    private var retrofit: Retrofit? = null
    fun getRetrofitClient(context: Context): Retrofit? {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}