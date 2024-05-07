package xyz.artenes.budget.app.transaction.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.CategoryEntity
import xyz.artenes.budget.data.TransactionEntity
import xyz.artenes.budget.utils.Event
import xyz.artenes.budget.utils.LabelPresenter
import xyz.artenes.budget.utils.ValueAndLabel
import xyz.artenes.budget.utils.ValueWithError
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter
) :
    ViewModel() {

    private val _description = MutableStateFlow(ValueWithError())
    val description: StateFlow<ValueWithError> = _description

    private val _amount = MutableStateFlow(ValueWithError())
    val amount: StateFlow<ValueWithError> = _amount

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date

    private val type = MutableStateFlow<TransactionType?>(null)
    val types =
        flowOf(TransactionType.entries.toTypedArray()).combine(type) { types, selectedType ->

            if (selectedType == null) {
                type.value = types[0]
            }

            category.value = null

            types.map { type ->

                ValueAndLabel(type, labelPresenter.present(type), type == selectedType)

            }

        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val category = MutableStateFlow<CategoryEntity?>(null)
    val categories = type.map { type ->

        Timber.d("Type changed to $type")

        if (type == null) {
            return@map emptyList<CategoryEntity>()
        }

        category.value = null
        repository.getCategoriesByType(type)

    }.combine(category) { categories, selectedCategory ->

        Timber.d("Categories has now ${categories.size} items")
        Timber.d("Selected category: $selectedCategory")

        if (category.value == null && categories.isNotEmpty()) {
            category.value = categories[0]
        }

        categories.map { category ->
            ValueAndLabel(category, category.name, category == selectedCategory)
        }

    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _event = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    fun setDescription(value: String) {
        _description.value = ValueWithError(value)
    }

    fun setAmount(value: String) {
        _amount.value = ValueWithError(value)
    }

    fun setType(value: TransactionType) {
        type.value = value
    }

    fun setCategory(value: ValueAndLabel<CategoryEntity>) {
        category.value = value.value
    }

    fun setDate(value: LocalDate) {
        _date.value = value
    }

    fun save() {

        val description = _description.value
        val amount = _amount.value
        val category = categories.value.firstOrNull { it.selected }?.value

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
                    Money(amount.value.toInt()),
                    _date.value,
                    type.value!!,
                    category!!.id,
                    OffsetDateTime.now()
                )
            )
            _event.value = Event("finish")
        }
    }

}