package pounces.komrade.api;

import com.squareup.okhttp.RequestBody;
import kotlin.Unit;
import pounces.komrade.api.data.*;
import retrofit.http.*;
import rx.Observable;

import java.util.List;

public interface TelegramApi {
    @FormUrlEncoded
    @POST("getUpdates")
    Observable<Response<List<Update>>> getUpdates(
            @Field("offset") Integer offset,
            @Field("limit") Integer limit,
            @Field("timeout") Integer timeout
    );

    @POST("setWebhook")
    Observable<Response<Boolean>> setWebhook(
            @Query("url") String url
    );

    @Multipart
    @POST("setWebhook")
    Observable<Response<Boolean>> setWebhook(
            @Query("url") String url,
            @Part("certificate") String certificate
    );

    @GET("getMe")
    Observable<Response<User>> apiGetMe();

    @FormUrlEncoded
    @POST("sendMessage")
    Observable<Response<Message>> sendMessage(
            @Field("chat_id") String chatId,
            @Field("text") String text,
            @Field("parse_mode") String parseMode,
            @Field("disable_web_page_preview") Boolean disableWebPagePreview,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("forwardMessage")
    Observable<Response<Message>> forwardMessage(
            @Field("chat_id") String chatId,
            @Field("from_chat_id") String fromChatId,
            @Field("message_id") Integer messageId
    );

    @FormUrlEncoded
    @POST("sendPhoto")
    Observable<Response<Message>> sendPhoto(
            @Field("chat_id") String chatId,
            @Field("photo") String photo,
            @Field("caption") String caption,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @Multipart
    @POST("sendPhoto")
    Observable<Response<Message>> sendPhoto(
            @Field("chat_id") String chatId,
            @Part("photo") RequestBody photo,
            @Field("caption") String caption,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("sendAudio")
    Observable<Response<Message>> sendAudio(
            @Field("chat_id") String chatId,
            @Field("audio") String audio,
            @Field("duration") Integer duration,
            @Field("performer") String performer,
            @Field("title") String title,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @Multipart
    @POST("sendAudio")
    Observable<Response<Message>> sendAudio(
            @Field("chat_id") String chatId,
            @Part("audio") RequestBody audio,
            @Field("duration") Integer duration,
            @Field("performer") String performer,
            @Field("title") String title,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("sendDocument")
    Observable<Response<Message>> sendDocument(
            @Field("chat_id") String chatId,
            @Field("document") String document,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @Multipart
    @POST("sendDocument")
    Observable<Response<Message>> sendDocument(
            @Field("chat_id") String chatId,
            @Part("document") RequestBody document,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("sendSticker")
    Observable<Response<Message>> sendSticker(
            @Field("chat_id") String chatId,
            @Field("sticker") String sticker,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @Multipart
    @POST("sendSticker")
    Observable<Response<Message>> sendSticker(
            @Field("chat_id") String chatId,
            @Part("sticker") RequestBody sticker,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("sendVideo")
    Observable<Response<Message>> sendVideo(
            @Field("chat_id") String chatId,
            @Field("video") String video,
            @Field("duration") Integer duration,
            @Field("caption") String caption,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @Multipart
    @POST("sendVideo")
    Observable<Response<Message>> sendVideo(
            @Field("chat_id") String chatId,
            @Part("video") RequestBody video,
            @Field("duration") Integer duration,
            @Field("caption") String caption,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("sendVoice")
    Observable<Response<Message>> sendVoice(
            @Field("chat_id") String chatId,
            @Field("voice") String voice,
            @Field("duration") Integer duration,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @Multipart
    @POST("sendVoice")
    Observable<Response<Message>> sendVoice(
            @Field("chat_id") String chatId,
            @Part("voice") RequestBody voice,
            @Field("duration") Integer duration,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );

    @FormUrlEncoded
    @POST("sendLocation")
    Observable<Response<Message>> sendLocation(
            @Field("chat_id") String chatId,
            @Field("latitude") Float latitude,
            @Field("longitude") Float longitude,
            @Field("reply_to_message_id") Integer replyToMessageId,
            @Field("reply_markup") ReplyMarkup replyMarkup
    );


    @FormUrlEncoded
    @POST("sendChatAction")
    Observable<Response<Unit>> sendChatAction(
            @Field("chat_id") String chatId,
            @Field("action") String action
    );

    @GET("getUserProfilePhotos")
    Observable<Response<UserProfilePhotos>> getUserProfilePhotos(
            @Query("user_id") Integer userId,
            @Query("offset") Integer offset,
            @Query("limit") Integer limit
    );

    @GET("getFile")
    Observable<Response<File>> getFile(
            @Query("file_id") String fileId
    );
}