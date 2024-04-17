package xyz.artenes.budgetapp.logging

import android.util.Log
import timber.log.Timber

/**
 * Logging tree used to send crashes, errors and warnings to a log collector
 */
class ProductionTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (t == null) {
            return
        }

        if (priority <= Log.INFO) {
            return
        }

        //send crash and error logs to somewhere here

    }

}