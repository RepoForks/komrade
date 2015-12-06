package pounces.komrade.api.data

data class Sticker(
        val fileId: String,
        val width: Int,
        val height: Int,
        val thumb: PhotoSize?,
        val fileSize: Int?
)

