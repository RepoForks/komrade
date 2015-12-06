package pounces.komrade.api

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.util.concurrent.RateLimiter
import com.squareup.okhttp.RequestBody
import pounces.komrade.api.data.*
import rx.Observable
import java.util.concurrent.TimeUnit

open class TelegramBase constructor(val api: TelegramApi) {
    private val globalSendRateLimiter = RateLimiter.create(Constants.GLOBAL_MESSAGES_PER_SECOND)
    private val perChatRateLimiters =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(10, TimeUnit.SECONDS)
                    .build(object : CacheLoader<String, RateLimiter>() {
                        override fun load(key: String): RateLimiter =
                                RateLimiter.create(Constants.MESSAGES_PER_CHAT_PER_SECOND)
                    })

    private fun <T> throttle(chatId: Any, func: () -> T): T {
        globalSendRateLimiter.acquire()
        perChatRateLimiters.get(chatId.toString()).acquire()
        return func()
    }

    fun getUpdates(
            offset: Int? = null,
            limit: Int? = null,
            timeout: Int? = null
    ): Observable<Response<List<Update>>> =
            api.getUpdates(offset, limit, timeout)

    fun setWebhook(
            url: String = "",
            certificate: String? = null
    ): Observable<Response<Boolean>> {
        return if (certificate != null) {
            api.setWebhook(url, certificate)
        } else {
            api.setWebhook(url)
        }
    }

    fun getMe(): Observable<Response<User>> = api.apiGetMe();

    fun sendMessage(
            chatId: Any,
            text: String,
            parseMode: String? = null,
            disableWebPagePreview: Boolean? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendMessage(chatId.toString(), text, parseMode, disableWebPagePreview, replyToMessageId, replyMarkup) }

    // Kotlin slash Java's type safety + "Integer or String" as type for an API request ->
    //      either an awful amount of overloads, or ignoring type safety.
    fun forwardMessage(
            chatId: Any,
            fromChatId: Any,
            messageId: Int
    ): Observable<Response<Message>> =
            throttle(chatId) { api.forwardMessage(chatId.toString(), fromChatId.toString(), messageId) }

    fun sendPhoto(
            chatId: Any,
            photo: RequestBody,
            caption: String? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendPhoto(chatId.toString(), photo, caption, replyToMessageId, replyMarkup) }

    fun sendPhoto(
            chatId: Any,
            photo: String,
            caption: String? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendPhoto(chatId.toString(), photo, caption, replyToMessageId, replyMarkup) }

    fun sendAudio(
            chatId: Any,
            audio: RequestBody,
            duration: Int? = null,
            performer: String? = null,
            title: String? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendAudio(chatId.toString(), audio, duration, performer, title, replyToMessageId, replyMarkup) }

    fun sendAudio(
            chatId: Any,
            audio: String,
            duration: Int? = null,
            performer: String? = null,
            title: String? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendAudio(chatId.toString(), audio, duration, performer, title, replyToMessageId, replyMarkup) }

    fun sendDocument(
            chatId: Any,
            duration: RequestBody,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendDocument(chatId.toString(), duration, replyToMessageId, replyMarkup) }

    fun sendDocument(
            chatId: Any,
            duration: String,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendDocument(chatId.toString(), duration, replyToMessageId, replyMarkup) }

    fun sendSticker(
            chatId: Any,
            sticker: RequestBody,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendSticker(chatId.toString(), sticker, replyToMessageId, replyMarkup) }

    fun sendSticker(
            chatId: Any,
            sticker: String,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendSticker(chatId.toString(), sticker, replyToMessageId, replyMarkup) }

    fun sendVideo(
            chatId: Any,
            video: RequestBody,
            duration: Int? = null,
            caption: String? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendVideo(chatId.toString(), video, duration, caption, replyToMessageId, replyMarkup) }

    fun sendVideo(
            chatId: Any,
            video: String,
            duration: Int? = null,
            caption: String? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendVideo(chatId.toString(), video, duration, caption, replyToMessageId, replyMarkup) }


    fun sendVoice(
            chatId: Any,
            voice: RequestBody,
            duration: Int? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendVoice(chatId.toString(), voice, duration, replyToMessageId, replyMarkup) }

    fun sendVoice(
            chatId: Any,
            voice: String,
            duration: Int? = null,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendVoice(chatId.toString(), voice, duration, replyToMessageId, replyMarkup) }

    fun sendLocation(
            chatId: Any,
            latitude: Float,
            longitude: Float,
            replyToMessageId: Int? = null,
            replyMarkup: ReplyMarkup? = null
    ): Observable<Response<Message>> =
            throttle(chatId) { api.sendLocation(chatId.toString(), latitude, longitude, replyToMessageId, replyMarkup) }

    fun sendChatAction(
            chatId: Any,
            action: String
    ): Observable<Response<Unit>> =
            throttle(chatId) { api.sendChatAction(chatId.toString(), action) }

    fun getUserProfilePhotos(
            userId: Int,
            offset: Int? = null,
            limit: Int? = null
    ): Observable<Response<UserProfilePhotos>> =
            api.getUserProfilePhotos(userId, offset, limit)

    fun getFile(
            fileId: String
    ): Observable<Response<File>> =
            api.getFile(fileId)
}
