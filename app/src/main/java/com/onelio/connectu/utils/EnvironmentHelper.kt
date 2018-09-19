package com.onelio.connectu.utils

import android.app.Activity

class EnvironmentHelper {
    companion object {
        fun AppVersion(activity : Activity): String? {
            val info = activity.packageManager.getPackageInfo(activity.packageName, 0)
            return info.packageName
        }

        fun AppVersionCode(activity : Activity): Int? {
            val info = activity.packageManager.getPackageInfo(activity.packageName, 0)
            return info.versionCode
        }
    }
}