package xyz.artenes.budget.analytics

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashAnalytics @Inject constructor() {

    suspend fun getId(): String {
        return FirebaseInstallations.getInstance().id.await()
    }

    fun setIdentifier(id: String) {
        Firebase.crashlytics.setUserId(id)
    }

    fun setKey(key: String, value: String) {
        Firebase.crashlytics.setCustomKey(key, value)
    }

    /**
     * Log error for next crash report.
     * This will be send only next time a crash, ANR or non-fatal error happens
     */
    fun logError(error: String) {
        Firebase.crashlytics.log(error)
    }

    fun logException(error: Throwable) {
        Firebase.crashlytics.recordException(error)
    }

}