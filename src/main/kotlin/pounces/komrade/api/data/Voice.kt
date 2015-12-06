package pounces.komrade.api.data

data class Voice(
        val fileId: String,
        val duration: Int,
        val mimeType: String?,
        val fileSize: Int?
)