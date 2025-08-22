package com.hfad.myapplication.data.remote


import com.hfad.myapplication.domain.ApiData
import com.hfad.myapplication.domain.Pokemon
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PokemonApi {
    @GET("pokemon")
    suspend fun getPokemons(): ApiData

    @GET("pokemon")
    suspend fun getPage(@Query("offset") offset: Int,
                        @Query("limit") limit: Int = 20): ApiData

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(@Path("name") name: String): Pokemon
}

object RetrofitInstance {
    var apiurl = "https://pokeapi.co/api/v2/"

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
       // .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: PokemonApi by lazy {
        Retrofit.Builder()
            .baseUrl(apiurl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(PokemonApi::class.java)
    }
}
