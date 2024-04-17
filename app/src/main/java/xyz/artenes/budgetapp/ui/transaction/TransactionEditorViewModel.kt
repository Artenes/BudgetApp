package xyz.artenes.budgetapp.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.artenes.budgetapp.room.AppDatabase
import xyz.artenes.budgetapp.room.TransactionEntity
import xyz.artenes.budgetapp.utils.Event
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(private val database: AppDatabase) :
    ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _finish = MutableStateFlow(Event(true))
    val finish: StateFlow<Event> = _finish

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun save() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.transactionsDao().insert(
                    TransactionEntity(
                        UUID.randomUUID().toString(),
                        _description.value,
                        _amount.value.toInt(),
                        OffsetDateTime.now().format(dateFormatter)
                    )
                )
            }
            _finish.value = Event()
        }
    }

}