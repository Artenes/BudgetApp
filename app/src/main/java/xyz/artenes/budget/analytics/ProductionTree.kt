package xyz.artenes.budget.analytics

import android.util.Log
import timber.log.Timber
import javax.inject.Inject

/**
 * Logging tree used to send crashes, errors and warnings to a log collector
 */
class ProductionTree @Inject constructor(private val crashAnalytics: CrashAnalytics) :
    Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (priority <= Log.INFO) {
            return
        }

        crashAnalytics.logError("[${priorityToString(priority)}] $tag: $message")

        if (t != null) {
            crashAnalytics.logException(t)
        }

    }

    private fun priorityToString(priority: Int) = when (priority) {
        5 -> "WARN"
        6 -> "ERROR"
        7 -> "ASSERT"
        else -> "UNKNOWN"
    }

}