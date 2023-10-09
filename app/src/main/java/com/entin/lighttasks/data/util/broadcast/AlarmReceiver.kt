package com.entin.lighttasks.data.util.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.entin.lighttasks.presentation.util.INTENT_MESSAGE

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(INTENT_MESSAGE) ?: return
    }
}