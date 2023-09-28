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

fun getIconTaskDrawable(groupId: Int) = when (groupId) {
    0 -> R.drawable.empty_btn_radio
    1 -> R.drawable.work_btn_radio
    2 -> R.drawable.rest_btn_radio
    3 -> R.drawable.food_btn_radio
    4 -> R.drawable.home_btn_radio
    5 -> R.drawable.fish_btn_radio
    6 -> R.drawable.bird_btn_radio
    7 -> R.drawable.blueberries_btn_radio
    8 -> R.drawable.bicycle_btn_radio
    9 -> R.drawable.saw_btn_radio
    10 -> R.drawable.camera_btn_radio
    11 -> R.drawable.broom_btn_radio
    12 -> R.drawable.film_btn_radio
    13 -> R.drawable.collision_btn_radio
    14 -> R.drawable.coconut_btn_radio
    15 -> R.drawable.beer_btn_radio
    16 -> R.drawable.boy_btn_radio
    17 -> R.drawable.underwear_btn_radio
    18 -> R.drawable.balloon_btn_radio
    19 -> R.drawable.alien_btn_radio
    20 -> R.drawable.car_btn_radio
    21 -> R.drawable.amphora_btn_radio
    22 -> R.drawable.accordion_btn_radio
    23 -> R.drawable.airplane_btn_radio
    24 -> R.drawable.tree_btn_radio
    25 -> R.drawable.bandage_btn_radio
    26 -> R.drawable.deer_btn_radio
    27 -> R.drawable.knife_btn_radio
    else -> R.drawable.empty_btn_radio
}