package pounces.komrade.bot

import pounces.komrade.api.Telegram
import pounces.komrade.api.data.Message
import pounces.komrade.bot.dsl.Command
import pounces.komrade.bot.dsl.CommandInvocationParser
import pounces.komrade.parallel
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*


class Komrade(val telegram: Telegram, val commands: Set<Command>) {
    fun run(): Observable<Any> {
        return telegram.getUpdateStream().parallel(Schedulers.io()) {
            try {
                processMessage(it)
            } catch (e: UserFacingException) {
                if (e.message == null) throw IllegalStateException("User-facing exception with null message.")
                telegram.sendMessage(it.chat.id, e.message).subscribe({}, { /* TODO log failed message delivery */ })
            }
        }
    }

    private fun processMessage(message: Message) {
        if (message.text != null) {
            val invocation = CommandInvocationParser.parse(message.text) ?: return

            val command = commands.firstOrNull() { it.name == invocation.name }
                    ?: throw UserFacingException("Sorry, but I don't recognize this command: ${invocation.name}")

            command.execute(telegram, message, invocation.parameters)
        }
    }

    companion object {
        fun create(token: String, block: KomradeBuilder.() -> Unit): Komrade =
                create(Telegram.forToken(token), block)

        fun create(telegram: Telegram, block: KomradeBuilder.() -> Unit): Komrade {
            val builder = KomradeBuilder()
            builder.block()
            builder.apply {
                command("/help") {
                    shortDescription("Shows all available commands, or explains a specific command.")

                    optionalParameters {
                        string("command", "A command to explain.")
                    }

                    body { msg, params ->
                        val passedCommand = params["command"] as? String

                        val output = if (passedCommand.isNullOrBlank()) {
                            "You can use the following commands:\n\n" +
                                    builder.commands.filter { it.showInHelp }.joinToString("\n") { it.toString() }
                        } else {
                            val commandToFind = if (passedCommand!!.startsWith("/")) {
                                passedCommand
                            } else {
                                "/" + passedCommand
                            }

                            val command = builder.commands.firstOrNull { it.name == commandToFind }

                            if (command != null) {
                                command.helpText()
                            } else {
                                "Sorry, but the command $commandToFind does not exist."
                            }
                        }

                        sendMessage(msg.chat.id, output).subscribe()
                    }
                }
            }
            val komrade = Komrade(telegram, builder.commands)
            return komrade
        }
    }
}

class KomradeBuilder {
    val commands = HashSet<Command>()

    fun command(name: String, block: Command.() -> Unit) {
        val command = Command(name)
        command.block()
        if (commands.contains(command)) {
            throw IllegalStateException("Command ${command.name} has been defined twice")
        }
        commands.add(command)
    }
}
