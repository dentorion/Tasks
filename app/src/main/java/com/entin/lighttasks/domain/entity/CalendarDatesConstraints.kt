package com.entin.lighttasks.domain.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * StartStrictMonth -> task date of start should be within month, but finish date can be in another month
 * StartFinishStrictMonth -> task date of start and finish should be strictly within month
 */

@Parcelize
@Keep
sealed class  CalendarDatesConstraints : Parcelable {
    data class StartInMonth(val start: Long, val finish: Long, val iconGroup: Int?) : CalendarDatesConstraints()
    data class StartFinishInMonth(val start: Long, val finish: Long, val iconGroup: Int?) : CalendarDatesConstraints()
}
