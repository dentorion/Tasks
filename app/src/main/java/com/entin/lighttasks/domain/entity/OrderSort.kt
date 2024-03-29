package com.entin.lighttasks.domain.entity

import androidx.annotation.Keep
import com.entin.lighttasks.presentation.util.ZERO

@Keep
enum class OrderSort {
    SORT_BY_DATE,
    SORT_BY_TITLE,
    SORT_BY_IMPORTANT,
    SORT_BY_MANUAL,
    SORT_BY_ICON;

    open var groupId: Int = ZERO
}
