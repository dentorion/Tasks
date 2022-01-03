package com.entin.lighttasks.presentation.util

/**
 * Result operation after AddEditTaskFragment close
 */

const val TASK_NEW: Int = 1

const val TASK_EDIT: Int = 2

const val TASK_EXIST: Int = 3

/**
 * Firebase DI
 */
const val TASKS = "tasks"

const val ERROR_NAME_HILT = "errors"

/**
 * Language block
 */

const val LANGUAGES_KEY = "language"

const val LANGUAGE_ENGLISH = "English"
const val LANGUAGE_RUSSIAN = "Russian"
const val LANGUAGE_POLISH = "Polish"

val LANGUAGES = mapOf(LANGUAGE_ENGLISH to 0, LANGUAGE_RUSSIAN to 1, LANGUAGE_POLISH to 2)
val LANGUAGES_COUNTRY = arrayOf("en", "ru", "pl")

const val LANGUAGES_DEFAULT = 2

/**
 * Fields name for AddEditTaskViewModel
 */

const val TASK_TITLE = "taskTitle"

const val TASK_MESSAGE = "taskMessage"

const val TASK_FINISHED = "taskFinished"

const val TASK_IMPORTANT = "taskImportant"

const val TASK_GROUP = "taskGroup"
