package pounces.komrade.api.data

data class ReplyKeyboardHide(
        val hideKeyboard: True,
        val selective: Boolean?
) : ReplyMarkup