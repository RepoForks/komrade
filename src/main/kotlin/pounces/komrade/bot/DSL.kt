package pounces.komrade.bot

import pounces.komrade.api.Telegram
import pounces.komrade.api.data.Message
import java.util.*
import kotlin.text.Regex

interface Parameter {
    val name: String
    val description: String
    val typeDescription: String
    fun parse(parameter: String): Any
}

data class StringParameter(override val name: String, override val description: String) : Parameter {
    override val typeDescription: String = "text"
    override fun parse(parameter: String): Any = parameter
}

data class IntParameter(override val name: String, override val description: String) : Parameter {
    override val typeDescription: String = "integer"
    override fun parse(parameter: String): Any = try {
        parameter.toInt()
    } catch(e: Throwable) {
        throw UserFacingException("The value you gave for parameter \"$name\" is not an integer.")
    }
}

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

data class FloatParameter(override val name: String, override val description: String) : Parameter {
    override val typeDescription: String = "number"
    override fun parse(parameter: String): Any = try {
        parameter.toFloat()
    } catch(e: Throwable) {
        throw UserFacingException("The value you gave for parameter \"$name\" is not a number.")
    }
}


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


data class CommandInvocation(val name: String, val parameters: List<String>)

object CommandInvocationParser {
    val COMMAND_NAME_REGEX = Regex("^(/[A-Za-z0-9_]+)\b?")

    fun parse(commandLine: String): CommandInvocation? {
        val result = COMMAND_NAME_REGEX.find(commandLine) ?: return null
        val name = result.groups[1]!!.value

        val tail = commandLine.substring(result.range.endInclusive + 1)
        val parameters = ArrayList<String>()
        if (tail.isNotBlank()) {
            var currentParameter = StringBuilder()

            var insideString = false
            var escape = false
            for (char in tail.trim().asSequence()) {
                when {
                    char == '\\' && insideString ->
                        escape = true

                    insideString && escape -> {
                        escape = false
                        currentParameter.append(char)
                    }

                    char == '"' -> {
                        when {
                            insideString && escape -> {
                                escape = false
                                currentParameter.append(char)
                            }
                            insideString -> {
                                insideString = false
                                parameters.add(currentParameter.toString())
                                currentParameter = StringBuilder()
                            }
                            else -> {
                                insideString = true
                            }
                        }
                    }

                    Character.isWhitespace(char) -> {
                        when {
                            insideString -> currentParameter.append(char)
                            else -> {
                                parameters.add(currentParameter.toString())
                                currentParameter = StringBuilder()
                            }
                        }
                    }

                    else -> {
                        currentParameter.append(char)
                    }
                }
            }

            if (currentParameter.isNotBlank()) {
                parameters.add(currentParameter.toString())
            }

            if (insideString) {
                throw UserFacingException("Unterminated string literal in the parameter list.")
            }

            parameters.removeIf { it.isNullOrBlank() }
        }

        return CommandInvocation(name, parameters)
    }
}


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


fun command(name: String, block: Command.() -> Unit) {
    val command = Command(name)
    command.block()
}

