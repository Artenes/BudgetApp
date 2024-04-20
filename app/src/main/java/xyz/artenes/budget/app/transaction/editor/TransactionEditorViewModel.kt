package xyz.artenes.budget.app.transaction.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.TransactionEntity
import xyz.artenes.budget.utils.Event
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _event = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun save() {
        viewModelScope.launch {
            repository.saveTransaction(
                TransactionEntity(
                    UUID.randomUUID(),
                    _description.value,
                    _amount.value.toInt(),
                    LocalDate.now(),
                    OffsetDateTime.now()
                )
            )
            _event.value = Event("finish")
        }
    }

}