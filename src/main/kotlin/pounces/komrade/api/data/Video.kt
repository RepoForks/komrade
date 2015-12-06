package pounces.komrade.api.data

data class Video(
        val fileId: String,
        val width: Int,
        val height: Int,
        val duration: Int,
        val thumb: PhotoSize?,
        val mimeType: String?,
        val fileSize: Int?
)