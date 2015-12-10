package pounces.komrade.bot.dsl

import pounces.komrade.api.Telegram
import pounces.komrade.api.data.Message
import pounces.komrade.bot.UserFacingException
import pounces.komrade.bot.dsl.parameters.Parameter
import java.util.*
import kotlin.text.Regex

/**
 * Represents a command that can be executed by a Telegram user.
 */
class Command(val name: String) {
    var description: String = ""
    var shortDescription: String = ""
    var showInHelp: Boolean = true
    var body: (Telegram.(Message, Map<String, Any>) -> Any)? = null
    var parameters = ArrayList<Parameter>()
    var optionalParameters = ArrayList<Parameter>()

    init {
        when {
            name.isNullOrBlank() ->
                throw IllegalArgumentException("name must not be null or blank")
            COMMAND_NAME_REGEX.matchEntire(name) == null ->
                throw IllegalArgumentException("name is not a valid command name")
        }
    }

    fun description(description: String) {
        this.description = description
    }

    fun shortDescription(description: String) {
        this.shortDescription = description
    }

    fun showInHelp(showInHelp: Boolean) {
        this.showInHelp = showInHelp
    }

    fun body(block: Telegram.(message: Message, parameters: Map<String, Any>) -> Any) {
        body = block
    }

    fun parameters(block: ParameterList.() -> Unit) {
        val list = ParameterList()
        list.block()
        parameters = list.parameters
    }

    fun optionalParameters(block: ParameterList.() -> Unit) {
        val list = ParameterList()
        list.block()
        optionalParameters = list.parameters
    }

    fun helpText(): String {
        var output = toString()

        if (description.isNotBlank()) {
            output += "\n\n"
            output += description
        }

        if (parameters.isNotEmpty() || optionalParameters.isNotEmpty()) {
            output += "\n\n"
        }

        if (parameters.isNotEmpty()) {
            output += parameters.map {
                it.name + " - " + it.typeDescription +
                        if (it.description.isNotBlank()) ": " + it.description else ""
            }.joinToString("\n") + "\n"
        }

        if (optionalParameters.isNotEmpty()) {
            output += optionalParameters.map {
                it.name + " (Optional) - " + it.typeDescription +
                        if (it.description.isNotBlank()) ": " + it.description else ""
            }.joinToString("\n") + "\n"
        }

        return output
    }

    fun execute(telegram: Telegram, message: Message, rawParameters: List<String>) {
        val minParameterCount = parameters.size
        val maxParameterCount = minParameterCount + optionalParameters.size

        when {
            rawParameters.size < minParameterCount ->
                throw UserFacingException("Sorry, but you gave me too few parameters.\n\nUsage:\n${toString()}")
            rawParameters.size > maxParameterCount ->
                throw UserFacingException("Sorry, but you gave me too many parameters.\n\nUsage:\n${toString()}")
        }

        val parsedParameters = HashMap<String, Any>()
        arrayOf(parameters, optionalParameters).flatMap { it }.zip(rawParameters).forEach {
            val (parameter, input) = it
            parsedParameters.put(parameter.name, parameter.parse(input))
        }

        // TODO verification that body is present and the params are okay
        telegram.(body!!)(message, parsedParameters)
    }

    override fun toString(): String {
        var output = name

        if (parameters.isNotEmpty()) {
            output += " "
            output += parameters.map { it.name }.joinToString(" ")
        }

        if (optionalParameters.isNotEmpty()) {
            output += " "
            output += optionalParameters.map { "[${it.name}]" }.joinToString(" ")
        }

        if (shortDescription.isNotBlank()) {
            output += " - " + shortDescription
        }

        return output
    }

    // Two commands are equivalent if their names match (overloading is not supported).
    override fun equals(other: Any?): Boolean = other is Command && name == other.name

    override fun hashCode(): Int = name.hashCode()

    companion object {
        // https://core.telegram.org/bots#edit-settings
        val COMMAND_NAME_REGEX = Regex("^/[A-Za-z0-9_]{1,32}$")
    }
}