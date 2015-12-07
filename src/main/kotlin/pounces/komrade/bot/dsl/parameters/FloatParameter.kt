package pounces.komrade.bot.dsl.parameters

import pounces.komrade.bot.UserFacingException

data class FloatParameter(override val name: String, override val description: String) : Parameter {
    override val typeDescription: String = "number"
    override fun parse(parameter: String): Any = try {
        parameter.toFloat()
    } catch(e: Throwable) {
        throw UserFacingException("The value you gave for parameter \"$name\" is not a number.")
    }
}


