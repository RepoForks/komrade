package pounces.komrade.api.data

data class Message(
        val messageId: Int,
        val from: User,
        val date: Int,
        val chat: Chat,
        val forwardFrom: User?,
        val forwardData: Int?,
        val replyToMessage: Message?,
        val text: String?,
        val audio: Audio?,
        val document: Document?,
        val photo: Array<PhotoSize>?,
        val sticker: Sticker?,
        val video: Video?,
        val voice: Voice?,
        val caption: String?,
        val contact: Contact?,
        val location: Location?,
        val newChatParticipant: User?,
        val leftChatParticipant: User?,
        val newChatTitle: String?,
        val newChatPhoto: Array<PhotoSize>?,
        val deleteChatPhoto: True?,
        val groupChatCreated: True?,
        val supergroupChatCreated: True?,
        val channelChatCreated: True?,
        val migrateToChatId: Int?,
        val migrateFromChatId: Int?
)

