package pounces.komrade.api.data

data class User(
        val id: Int,
        val firstName: String,
        val lastName: String?,
        val username: String?
)