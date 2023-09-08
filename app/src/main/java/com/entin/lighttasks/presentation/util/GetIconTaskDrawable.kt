package com.entin.lighttasks.presentation.util

import com.entin.lighttasks.R
import com.entin.lighttasks.domain.entity.Task

fun getIconTaskDrawable(task: Task) = when (task.group) {
    0 -> R.drawable.ic_nothing
    1 -> R.drawable.ic_work
    2 -> R.drawable.ic_rest
    3 -> R.drawable.ic_food
    4 -> R.drawable.ic_home
    5 -> R.drawable.ic_fish
    6 -> R.drawable.ic_bird
    7 -> R.drawable.ic_blueberries
    8 -> R.drawable.ic_bicycle
    9 -> R.drawable.ic_saw
    10 -> R.drawable.ic_camera
    11 -> R.drawable.ic_broom
    12 -> R.drawable.ic_film
    13 -> R.drawable.ic_collision
    14 -> R.drawable.ic_coconut
    15 -> R.drawable.ic_beer
    16 -> R.drawable.ic_boy
    17 -> R.drawable.ic_underwear
    18 -> R.drawable.ic_balloon
    19 -> R.drawable.ic_alien
    20 -> R.drawable.ic_car
    21 -> R.drawable.ic_amphora
    22 -> R.drawable.ic_accordion
    23 -> R.drawable.ic_airplane
    24 -> R.drawable.ic_tree
    25 -> R.drawable.ic_bandage
    26 -> R.drawable.ic_deer
    27 -> R.drawable.ic_knife
    else -> R.drawable.ic_nothing
}