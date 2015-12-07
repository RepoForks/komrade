package pounces.komrade.bot.dsl.parameters

import pounces.komrade.bot.UserFacingException

data class IntRangeParameter(override val name: String, override val description: String, val min: Int, val maxInclusive: Int) : Parameter {
    override val typeDescription: String = "integer ($min to $maxInclusive inclusive)"
    override fun parse(parameter: String): Any {
        val num = parameter.toInt()
        if (num < min || num > maxInclusive) {
            throw UserFacingException("The value you gave for parameter \"$name\" is out of range.")
        }
        return num
    }
}