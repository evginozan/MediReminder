package com.evginozan.medireminder.data.local.converter

import androidx.room.TypeConverter
import com.evginozan.medireminder.data.local.entity.DoseTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DoseTimeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromDoseTimeList(value: List<DoseTime>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDoseTimeList(value: String): List<DoseTime> {
        return try {
            val listType = object : TypeToken<List<DoseTime>>() {}.type
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class StringListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}