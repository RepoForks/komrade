package pounces.komrade.bot

import org.junit.Assert.assertEquals
import org.junit.Test

class KomradeBuilderTest {
    @Test
    fun testCommand() {
        val builder = KomradeBuilder()
        builder.command("/test", {})
        assertEquals(builder.commands.first().name, "/test")
    }
}