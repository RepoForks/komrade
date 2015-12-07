package pounces.komrade.bot.dsl

import pounces.komrade.bot.dsl.parameters.*
import java.util.*
import kotlin.text.Regex

class ParameterList {
    val parameters = ArrayList<Parameter>()

    fun string(name: String, description: String = "") {
        validateParameterName(name)
        parameters.add(StringParameter(name, description))
    }

    fun int(name: String, description: String = "") {
        validateParameterName(name)
        parameters.add(IntParameter(name, description))
    }

    fun rangedInt(name: String, min: Int, max: Int, description: String = "") {
        validateParameterName(name)
        parameters.add(IntRangeParameter(name, description, min, max))
    }

    fun float(name: String, description: String = "") {
        validateParameterName(name)
        parameters.add(FloatParameter(name, description))
    }

    fun validateParameterName(name: String) {
        when {
            name.isNullOrBlank() ->
                throw IllegalArgumentException("name must not be null or blank")
            PARAMETER_NAME_REGEX.matchEntire(name) == null ->
                throw IllegalArgumentException("name is not a valid parameter name")
        }
    }

    companion object {
        // https://core.telegram.org/bots#edit-settings
        val PARAMETER_NAME_REGEX = Regex("^[A-Za-z0-9_]+$")
    }
}