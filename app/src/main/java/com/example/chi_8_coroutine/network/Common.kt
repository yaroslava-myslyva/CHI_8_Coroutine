package com.example.chi_8_coroutine.network

object Common {
    private const val BASE_URL = "https://zoo-animal-api.herokuapp.com/"
    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)
}