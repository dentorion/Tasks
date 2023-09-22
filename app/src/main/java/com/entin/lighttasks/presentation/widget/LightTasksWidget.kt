package com.entin.lighttasks.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.entin.lighttasks.R
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 */
@AndroidEntryPoint
class LightTasksWidget : AppWidgetProvider() {

    @Inject
    lateinit var tasksRepository: TasksRepository

    private var myAppWidgetManager: AppWidgetManager? = null
    private var myAppWidgetIds: IntArray? = null

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        myAppWidgetManager = appWidgetManager
        myAppWidgetIds = appWidgetIds

        // There may be multiple widgets active, so update all of them
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        context?.let { updateAppWidget(it) }
    }

    override fun onEnabled(context: Context) {
        updateAppWidget(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Get count of tasks for today
            val count = tasksRepository.getCountTasksForWidget().first()
            // Generate result value of string
            val resultValue = context.resources.getString(R.string.widget_tasks_count, count)
            // Create an Intent to launch MainActivity
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */  0,
                /* intent = */ Intent(context, MainActivity::class.java).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                },
                /* flags = */ PendingIntent.FLAG_IMMUTABLE
            )
            // Set views in widget
            withContext(Dispatchers.Main) {
                val views =
                    RemoteViews(context.packageName, R.layout.light_tasks_widget).apply {
                        setImageViewResource(
                            R.id.appwidget_icon_task,
                            R.drawable.empty_radio_state_unchecked
                        )
                        setTextViewText(R.id.appwidget_tasks_quantity, resultValue)
                        setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)
                    }
                myAppWidgetManager?.updateAppWidget(myAppWidgetIds, views)
            }
        }
    }
}
