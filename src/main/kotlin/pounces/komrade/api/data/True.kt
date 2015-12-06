package pounces.komrade.api.data

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = True.TrueSerializer::class)
@JsonDeserialize(using = True.TrueDeserializer::class)
object True {
    override fun toString(): String = "True"

    internal class TrueSerializer : JsonSerializer<True>() {
        override fun serialize(value: True?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen!!.writeBoolean(true)
        }
    }

    internal class TrueDeserializer : JsonDeserializer<True>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): True {
            return True
        }
    }
}

