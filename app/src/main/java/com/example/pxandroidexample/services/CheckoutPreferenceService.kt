package com.example.pxandroidexample.services

import com.example.pxandroidexample.model.CheckoutPreference
import com.example.pxandroidexample.model.Item
import com.example.pxandroidexample.model.Payer
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.IOError
import java.io.IOException
import java.lang.Exception

/**
 * Interface used to create real checkout preferences for a better example experience
 */
interface CheckoutPreferenceAPI {
    @POST("/checkout/preferences")
    suspend fun create(@Body body : CheckoutPreference,
        @Header("Authorization") token : String) : CheckoutPreference
}

interface CheckoutPreferenceService{
    val retrofitService : CheckoutPreferenceAPI
}

class CheckoutPreferenceServiceImpl : CheckoutPreferenceService{
    companion object{
        private const val BASE_URL = "https://api.mercadopago.com"

        private val retrofit  = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    override val retrofitService : CheckoutPreferenceAPI by lazy {
        retrofit.create(CheckoutPreferenceAPI::class.java)
    }

    suspend fun create(
        body: CheckoutPreference,
        accessToken: String
    ): ResultWrapper<CheckoutPreference> {
        return safeApiCall(Dispatchers.IO) { retrofitService.create(body, makeToken(accessToken)) }
    }

    private fun makeToken(token : String) : String{
        return "Bearer $token"
    }

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try{
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable){
                when (throwable){
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = throwable.response()?.errorBody()?.toString()
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}