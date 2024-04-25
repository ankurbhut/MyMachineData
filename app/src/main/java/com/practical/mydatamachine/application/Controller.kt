package com.practical.mydatamachine.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.practical.mydatamachine.localcache.LocalDataHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class Controller : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        instance = this
        observeApplicationState()
    }

    companion object {
        lateinit var instance: Controller
        var appInForeground: Boolean = false
        var foregroundActivity: Activity? = null
        var foregroundView: View? = null
    }


    private fun observeApplicationState() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                appInForeground = true
                Log.e("Foreground--->", "ON_RESUME")
            }

            Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                Log.e("Foreground--->", "ON_STOP")
                if (appInForeground) {
                    appInForeground = false
                }
            }

            else -> {}
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        Log.e("check_cycle_ads", "BaseApplication->${activity.localClassName}")
        foregroundActivity = activity
        foregroundView = activity.window.decorView
    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {
        foregroundActivity = activity
        foregroundView = activity.window.decorView
    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}