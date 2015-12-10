package pounces.komrade.bot

/**
 * Indicates to the user that something has gone wrong. The message will be displayed verbatim to the user.
 */
class UserFacingException : RuntimeException {
    constructor(message: String, ex: Exception?) : super(message, ex) {
    }

    constructor(message: String) : super(message) {
    }

    constructor(ex: Exception) : super(ex) {
    }
}