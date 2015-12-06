package pounces.komrade.api.data

data class ForceReply(
        val forceReply: True,
        val selective: Boolean?
) : ReplyMarkup