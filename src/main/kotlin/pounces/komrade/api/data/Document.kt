package pounces.komrade.api.data

data class Document(
        val fileId: String,
        val thumb: PhotoSize?,
        val fileName: String?,
        val mimeType: String?,
        val fileSize: Int?
)