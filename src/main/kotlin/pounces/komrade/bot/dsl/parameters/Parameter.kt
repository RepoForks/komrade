package pounces.komrade.bot.dsl.parameters

interface Parameter {
    val name: String
    val description: String
    val typeDescription: String
    fun parse(parameter: String): Any
}