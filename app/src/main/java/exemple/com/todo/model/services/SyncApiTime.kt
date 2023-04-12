package exemple.com.todo.model.services

import exemple.com.todo.model.data.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface SyncApiTime {

    @GET("current.json")
    suspend fun getWeather(
        @Query("lang") lang: String,
        @Query("key") apiKey: String,
        @Query("q") latitudeElongitude: String
    ): Weather

}