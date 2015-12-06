package pounces.komrade.api.data

data class Contact(
        val phoneNumber: String,
        val firstName: String,
        val lastName: String?,
        val userId: Int?
)