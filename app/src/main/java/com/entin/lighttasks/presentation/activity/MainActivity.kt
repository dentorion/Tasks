package com.entin.lighttasks.presentation.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.entin.lighttasks.presentation.widget.LightTasksWidget
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.ActivityMainBinding
import com.entin.lighttasks.presentation.util.LANGUAGE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.get
import com.entin.lighttasks.presentation.util.hideKeyboard
import com.entin.lighttasks.presentation.util.set
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: MainActivityViewModel by viewModels()

    private var countWidget: Int = ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppLanguage()

        binding = ActivityMainBinding.inflate(layoutInflater)

        clearUnusedFiles()
        observeState()
        setupNavigationAndActionBar()
        setContentView(binding.root)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun clearUnusedFiles() {
        viewModel.deleteUnusedPhotos()
        viewModel.deleteUnusedSoundRecords()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeState() {
        this.lifecycleScope.launch {
            viewModel.tasksEvent.collect { state: MainActivityEvent ->
                when(state) {
                    is MainActivityEvent.CountTasksWidget -> {
                        setCountWidget(state)
                    }
                }
            }
        }
    }

    private fun setCountWidget(state: MainActivityEvent.CountTasksWidget) {
        countWidget = state.count
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this)
        val thisWidget: ComponentName = ComponentName(this, LightTasksWidget::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (widgetId in allWidgetIds) {
            //get any views in this instance of the widget
            val remoteViews = RemoteViews(this.packageName, R.layout.light_tasks_widget)
            // Set the text
            remoteViews.setTextViewText(
                R.id.appwidget_tasks_quantity,
                getString(R.string.widget_tasks_count, countWidget)
            )
            //update the widget with any change we just made
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    private fun setupNavigationAndActionBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)
        setSupportActionBar(binding.myActionBar)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()

    // Navigation Change Listener for hiding opened keyboard

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?,
    ) {
        currentFocus?.hideKeyboard()
    }

    private fun setAppLanguage() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        sharedPref.get<String>(LANGUAGE)?.let {
            val locale = Locale(it)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onPause() {
        viewModel.updateCount()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }
}
