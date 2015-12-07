package pounces.komrade.bot.dsl.parameters

import pounces.komrade.bot.UserFacingException

data class IntParameter(override val name: String, override val description: String) : Parameter {
    override val typeDescription: String = "integer"
    override fun parse(parameter: String): Any = try {
        parameter.toInt()
    } catch(e: Throwable) {
        throw UserFacingException("The value you gave for parameter \"$name\" is not an integer.")
    }
}