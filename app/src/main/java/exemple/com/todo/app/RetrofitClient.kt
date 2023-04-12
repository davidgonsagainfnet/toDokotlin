package exemple.com.todo.app

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.facebook.stetho.okhttp3.StethoInterceptor
import exemple.com.todo.model.services.SyncApiTime

private const val URL_WEATHER = "https://api.weatherapi.com/v1/"

class RetrofitClient(mCtx: Context?, needsContentType:Boolean = false) {

    private val base_url = URL_WEATHER

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .addNetworkInterceptor(StethoInterceptor())
        .addInterceptor{ chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder().method(original.method, original.body)

            if (needsContentType) {
                requestBuilder.addHeader("Content-Type", "application/json")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    fun syncCampService() = retrofit.create(SyncApiTime::class.java)

}