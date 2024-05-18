package xyz.artenes.budget.app.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.R
import xyz.artenes.budget.analytics.CrashAnalytics
import xyz.artenes.budget.core.Applications
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.core.models.Event
import xyz.artenes.budget.core.models.FunctionResult
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val analytics: CrashAnalytics,
    private val applications: Applications,
    private val messages: Messages
) : ViewModel() {

    private val _event: MutableStateFlow<Event> = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    val userId: StateFlow<String> = flow {

        emit(analytics.getId())

    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun sendEmail() {
        val email = messages.get(R.string.dev_email)
        val subject = messages.get(
            R.string.default_email_subject,
            messages.get(R.string.app_name)
        )

        val result = applications.sendEmailTo(email, subject)

        if (result is FunctionResult.Error) {
            _event.value = Event(messages.get(R.string.no_email_app_found))
            analytics.logException(result.exception)
        }
    }

}