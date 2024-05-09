package xyz.artenes.budget.app.transaction.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.artenes.budget.app.presenters.MoneyPresenter
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
import javax.inject.Singleton

class TransactionEditorViewModel @Inject constructor(
    private val id: UUID?,
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val moneyPresenter: MoneyPresenter
) :
    ViewModel() {

    private var createdAt: OffsetDateTime? = null

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

    init {
        viewModelScope.launch {
            if (id == null) {
                return@launch
            }
            val data = repository.getTransactionById(id)
            setType(data.transaction.type)
            setDescription(data.transaction.description)
            setAmount(data.transaction.amount)
            setDate(data.transaction.date)
            setCategory(data.category)
            createdAt = data.transaction.createdAt
        }
    }

    fun setDescription(value: String) {
        _description.value = ValueWithError(value)
    }

    fun setAmount(value: String) {
        val newFormattedValue = moneyPresenter.formatFromString(value)
        _amount.value = ValueWithError(newFormattedValue)
    }

    private fun setAmount(value: Money) {
        val formatted = moneyPresenter.formatMoney(value)
        _amount.value = ValueWithError(formatted)
    }

    fun setType(value: TransactionType) {
        type.value = value
    }

    fun setCategory(value: ValueAndLabel<CategoryEntity>) {
        setCategory(value.value)
    }

    private fun setCategory(value: CategoryEntity) {
        category.value = value
    }

    fun setDate(value: LocalDate) {
        _date.value = value
    }

    fun delete() {
        viewModelScope.launch {
            repository.deleteTransactionById(id!!)
            _event.value = Event("finish")
        }
    }

    fun save() {

        val description = _description.value
        val amount = _amount.value
        val category = categories.value.firstOrNull { it.selected }?.value
        val parsedAmount = moneyPresenter.parse(amount.value)

        if (description.value.isEmpty()) {
            _description.value = description.copy(error = "Required")
            return
        }

        if (parsedAmount.value == 0) {
            _amount.value = amount.copy(error = "Required")
            return
        }

        viewModelScope.launch {
            repository.saveTransaction(
                TransactionEntity(
                    id ?: UUID.randomUUID(),
                    description.value,
                    parsedAmount,
                    _date.value,
                    type.value!!,
                    category!!.id,
                    createdAt ?: OffsetDateTime.now()
                )
            )
            _event.value = Event("finish")
        }
    }

}

@Suppress("UNCHECKED_CAST")
@Singleton
class TransactionEditorFactory @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val moneyPresenter: MoneyPresenter,
) {

    fun make(id: UUID?): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TransactionEditorViewModel(
                    id,
                    repository,
                    labelPresenter,
                    moneyPresenter
                ) as T
            }

        }

    }

}