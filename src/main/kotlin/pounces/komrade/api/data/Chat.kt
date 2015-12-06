package pounces.komrade.api.data

data class Chat(
        val id: Int,
        val type: String,
        val title: String?,
        val username: String?,
        val firstName: String?,
        val lastName: String?
)