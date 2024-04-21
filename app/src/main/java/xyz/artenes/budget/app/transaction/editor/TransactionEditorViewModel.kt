package xyz.artenes.budget.app.transaction.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.TransactionEntity
import xyz.artenes.budget.utils.Event
import xyz.artenes.budget.utils.ValueWithError
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _description = MutableStateFlow(ValueWithError())
    val description: StateFlow<ValueWithError> = _description

    private val _amount = MutableStateFlow(ValueWithError())
    val amount: StateFlow<ValueWithError> = _amount

    private val _type = MutableStateFlow(TransactionType.EXPENSE)
    val type: StateFlow<TransactionType> = _type

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date

    private val _event = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    fun setDescription(value: String) {
        _description.value = ValueWithError(value)
    }

    fun setAmount(value: String) {
        _amount.value = ValueWithError(value)
    }

    fun setType(value: TransactionType) {
        _type.value = value
    }

    fun setDate(value: LocalDate) {
        _date.value = value
    }

    fun save() {

        val description = _description.value
        val amount = _amount.value

        if (description.value.isEmpty()) {
            _description.value = description.copy(error = "Required")
            return
        }

        if (amount.value.isEmpty()) {
            _amount.value = amount.copy(error = "Required")
            return
        }

        if (amount.value.toIntOrNull() == null) {
            _amount.value = amount.copy(error = "Invalid value")
            return
        }

        viewModelScope.launch {
            repository.saveTransaction(
                TransactionEntity(
                    UUID.randomUUID(),
                    description.value,
                    amount.value.toInt(),
                    _date.value,
                    _type.value,
                    OffsetDateTime.now()
                )
            )
            _event.value = Event("finish")
        }
    }

}