package pounces.komrade.api

data class Response<T>(val ok: Boolean, val result: T, val error_code: Int?, val description: String?)