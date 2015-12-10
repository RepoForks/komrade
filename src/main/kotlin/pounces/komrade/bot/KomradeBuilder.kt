package pounces.komrade.bot

import pounces.komrade.bot.dsl.Command
import java.util.*

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