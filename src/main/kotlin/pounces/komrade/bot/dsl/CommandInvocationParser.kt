package pounces.komrade.bot.dsl

import pounces.komrade.bot.UserFacingException
import java.util.*
import kotlin.text.Regex

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