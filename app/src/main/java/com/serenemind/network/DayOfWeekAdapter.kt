package com.serenemind.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.DayOfWeek

class DayOfWeekAdapter : TypeAdapter<DayOfWeek>() {
    override fun write(out: JsonWriter, value: DayOfWeek?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.name)
        }
    }

    override fun read(reader: JsonReader): DayOfWeek? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return try {
            DayOfWeek.valueOf(reader.nextString().uppercase())
        } catch (e: Exception) {
            null
        }
    }
}
