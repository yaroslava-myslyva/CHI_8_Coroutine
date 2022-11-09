package com.example.chi_8_coroutine.network

import com.example.chi_8_coroutine.Animal
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService {
    @GET("/animals/rand/1/")
    fun getResponseItem(): Call<List<Animal>>
}