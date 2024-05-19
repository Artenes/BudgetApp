package xyz.artenes.budget.android

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import xyz.artenes.budget.app.MainActivity
import xyz.artenes.budget.core.Applications
import xyz.artenes.budget.core.models.FunctionResult

class AndroidApplications(private val context: Context) : Applications {

    @SuppressLint("QueryPermissionsNeeded")
    override fun sendEmailTo(
        email: String,
        subject: String,
        message: String
    ): FunctionResult<Nothing> {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(emailIntent)
            FunctionResult.EmptySuccess
        } catch (exception: ActivityNotFoundException) {
            FunctionResult.Error(exception)
        }
    }

    override fun openBudgetApp() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

}