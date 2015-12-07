package pounces.komrade.bot.dsl.parameters

data class StringParameter(override val name: String, override val description: String) : Parameter {
    override val typeDescription: String = "text"
    override fun parse(parameter: String): Any = parameter
}