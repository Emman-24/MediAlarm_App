package com.emman.android.medialarm.utils

import androidx.room.TypeConverter
import com.emman.android.medialarm.domain.models.MedicationTime
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}

class MedicationTimeListConverter {

    @TypeConverter
    fun fromMedicationTimeList(value: List<MedicationTime>): String {
        val gson = createGson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMedicationTimeList(value: String?): List<MedicationTime>? {
        if (value == null) return null
        val gson = createGson()
        val listType = object : TypeToken<List<MedicationTime>>() {}.type
        return gson.fromJson(value, listType)
    }

    private fun createGson(): Gson {
        return Gson().newBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .create()
    }

    private inner class LocalTimeTypeAdapter : TypeAdapter<LocalTime>() {
        override fun write(out: JsonWriter, value: LocalTime?) {
            if (value == null) {
                out.nullValue()
            } else {
                out.value(value.toSecondOfDay())
            }
        }

        override fun read(input:JsonReader): LocalTime? {
            if (input.peek() == JsonToken.NULL) {
                input.nextNull()
                return null
            }
            return LocalTime.ofSecondOfDay(input.nextLong())
        }
    }

    private inner class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
        override fun write(out: JsonWriter, value: LocalDateTime?) {
            if (value == null) {
                out.nullValue()
            } else {
                out.value(value.toEpochSecond(ZoneOffset.UTC))
            }
        }

        override fun read(input: JsonReader): LocalDateTime? {
            if (input.peek() == JsonToken.NULL) {
                input.nextNull()
                return null
            }
            return LocalDateTime.ofEpochSecond(input.nextLong(), 0, ZoneOffset.UTC)
        }
    }
}

class DayOfWeekSetConverter {
    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>): String {
        return days.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekSet(data: String): Set<DayOfWeek> {
        if (data.isEmpty()) {
            return emptySet()
        }
        return data.split(',').map { DayOfWeek.valueOf(it) }.toSet()
    }
}

class LocalDateTimeConverter {
    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): Long? {
        return localDateTime?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun toLocalDateTime(timestamp: Long?): LocalDateTime? {
        return timestamp?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }
}

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime?): Int? {
        return localTime?.toSecondOfDay()
    }

    @TypeConverter
    fun toLocalTime(secondOfDay: Int?): LocalTime? {
        return secondOfDay?.let { LocalTime.ofSecondOfDay(it.toLong()) }
    }
}
