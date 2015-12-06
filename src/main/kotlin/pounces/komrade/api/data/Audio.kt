package pounces.komrade.api.data

data class Audio(
        val fileId: String,
        val duration: Int,
        val performer: String?,
        val title: String?,
        val mimeType: String?,
        val fileSize: Int
)