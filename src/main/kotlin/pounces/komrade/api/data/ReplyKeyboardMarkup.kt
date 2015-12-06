package pounces.komrade.api.data

data class ReplyKeyboardMarkup(
        val keyboard: Array<Array<String>>,
        val resizeKeyboard: Boolean?,
        val oneTimeKeyboard: Boolean?,
        val selective: Boolean?
) : ReplyMarkup