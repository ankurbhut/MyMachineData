package com.practical.mydatamachine.localcache


import com.practical.mydatamachine.localcache.PreferenceManager.Companion.getInstance
import com.practical.mydatamachine.localcache.SharedPrefConstant.IS_NOTIFICATION_ON

object LocalDataHelper {

    var isNotificationOn: Boolean
        get() {
            return getInstance().getBoolean(IS_NOTIFICATION_ON, true)
        }
        set(value) {
            return getInstance().putBoolean(IS_NOTIFICATION_ON, value)
        }

    fun clearUserPreference() {
        getInstance().clearUserPreference()
    }
}