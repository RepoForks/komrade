package pounces.komrade.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import pounces.komrade.api.data.Message
import retrofit.JacksonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import rx.Observable
import rx.lang.kotlin.toObservable
import java.util.concurrent.TimeUnit

class Telegram constructor(api: TelegramApi, val timeout: Int) : TelegramBase(api) {
    fun getUpdateStream(): Observable<Message> {
        var offset = 0
        val deferredMessages = Observable.defer {
            getUpdates(offset = offset, timeout = timeout)
                    .map {
                        it.result.map {
                            offset = Math.max(offset, it.updateId + 1)
                            it.message
                        }.filterNotNull().toObservable()
                    }
        }

        return Observable.merge(deferredMessages.repeat())
    }

    companion object {
        fun forToken(token: String, longPollTimeout: Int = 60): Telegram {
            val api = retrofit(token, longPollTimeout.toLong()).create(TelegramApi::class.java)
            return Telegram(api, longPollTimeout)
        }

        private fun objectMapper(): ObjectMapper =
                ObjectMapper()
                        .registerKotlinModule()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)

        private fun client(longPollTimeout: Long): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

            val client = OkHttpClient()
            client.setReadTimeout(longPollTimeout, TimeUnit.SECONDS)
            client.interceptors().add(interceptor)

            return client
        }

        private fun retrofit(token: String, longPollTimeout: Long = 60): Retrofit =
                Retrofit.Builder()
                        .baseUrl("https://api.telegram.org/bot$token/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper()))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(client(longPollTimeout))
                        .build()
    }
}