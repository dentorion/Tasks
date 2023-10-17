package com.entin.lighttasks.data.db.converter

import android.net.Uri
import androidx.room.TypeConverter
import com.entin.lighttasks.presentation.util.EMPTY_STRING

class ListUriToStringConverter {
    @TypeConverter
    fun fromUriList(uriList: List<Uri>?): String {
        return uriList?.let {
            uriList.map { it.toString() }.joinToString(",")
        } ?: EMPTY_STRING
    }

    @TypeConverter
    fun toUriList(uriString: String): List<Uri> {
        return if (uriString == EMPTY_STRING) {
            listOf<Uri>()
        } else {
            uriString.split(",").map { Uri.parse(it) }
        }
    }
}
