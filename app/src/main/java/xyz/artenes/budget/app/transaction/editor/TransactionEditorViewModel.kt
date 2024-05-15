package xyz.artenes.budget.app.transaction.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import xyz.artenes.budget.R
import xyz.artenes.budget.app.presenter.DatePresenter
import xyz.artenes.budget.app.presenter.LabelPresenter
import xyz.artenes.budget.app.presenter.MoneyPresenter
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.core.models.Event
import xyz.artenes.budget.core.models.FormattedValue
import xyz.artenes.budget.core.models.Money
import xyz.artenes.budget.core.models.SelectableItem
import xyz.artenes.budget.core.models.TransactionType
import xyz.artenes.budget.core.models.ValueWithError
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.models.CategoryEntity
import xyz.artenes.budget.data.models.TransactionEntity
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

class TransactionEditorViewModel(
    private val id: UUID?,
    private val repository: AppRepository,
    labelPresenter: LabelPresenter,
    private val moneyPresenter: MoneyPresenter,
    private val datePresenter: DatePresenter,
    private val messages: Messages
) :
    ViewModel() {

    private var createdAt: OffsetDateTime? = null

    private val _description = MutableStateFlow(ValueWithError(""))
    val description: StateFlow<ValueWithError<String>> = _description

    private val _amount = MutableStateFlow(ValueWithError(""))
    val amount: StateFlow<ValueWithError<String>> = _amount

    private val _date = MutableStateFlow(
        FormattedValue<LocalDate>(
            LocalDate.now(),
            datePresenter.formatDate(LocalDate.now())
        )
    )
    val date: StateFlow<FormattedValue<LocalDate>> = _date

    private val _types = MutableStateFlow(
        listOf(
            SelectableItem(
                TransactionType.EXPENSE,
                labelPresenter.present(TransactionType.EXPENSE),
                true
            ),
            SelectableItem(
                TransactionType.INCOME,
                labelPresenter.present(TransactionType.INCOME),
                false
            ),
        )
    )
    val types: StateFlow<List<SelectableItem<TransactionType>>> = _types

    private val _categories =
        MutableStateFlow<ValueWithError<List<SelectableItem<CategoryEntity>>>>(
            ValueWithError(emptyList())
        )
    val categories: StateFlow<ValueWithError<List<SelectableItem<CategoryEntity>>>> = _categories

    private val _event = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    init {
        viewModelScope.launch {

            if (id != null) {
                val data = repository.getTransactionById(id)
                setType(data.transaction.type)
                setDescription(data.transaction.description)
                setAmount(data.transaction.amount)
                setDate(data.transaction.date)
                createdAt = data.transaction.createdAt

                loadCategories(_types.value.first { it.selected }.value)
                setCategory(data.category)

                listenForTypeChange()
                return@launch
            }

            loadCategories(_types.value.first { it.selected }.value)
            listenForTypeChange()

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
        _types.value = _types.value.map { it.copy(selected = it.value == value) }
    }

    fun setCategory(value: SelectableItem<CategoryEntity>) {
        setCategory(value.value)
    }

    private fun setCategory(value: CategoryEntity) {
        val items = _categories.value.value
        val error = if (value.isDeleted) messages.get(R.string.category_deleted) else null
        _categories.value =
            ValueWithError(items.map { it.copy(selected = it.value == value) }, error)
    }

    fun setDate(value: LocalDate) {
        _date.value = FormattedValue(value, datePresenter.formatDate(value))
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
        val category = categories.value.value.firstOrNull { it.selected }?.value
        val parsedAmount = moneyPresenter.parse(amount.value)
        val type = _types.value.first { it.selected }

        if (description.value.isEmpty()) {
            _description.value = description.copy(error = messages.get(R.string.required))
            return
        }

        if (parsedAmount.value == 0) {
            _amount.value = amount.copy(error = messages.get(R.string.required))
            return
        }

        if (category == null) {
            _categories.value =
                _categories.value.copy(error = messages.get(R.string.required))
            return
        }

        viewModelScope.launch {
            repository.saveTransaction(
                TransactionEntity(
                    id ?: UUID.randomUUID(),
                    description.value,
                    parsedAmount,
                    _date.value.original,
                    type.value,
                    category.id,
                    createdAt ?: OffsetDateTime.now()
                )
            )
            _event.value = Event("finish")
        }
    }

    private fun listenForTypeChange() {
        viewModelScope.launch {
            _types.drop(1).collectLatest { items ->
                val value = items.first { it.selected }.value
                loadCategories(value)
            }
        }
    }

    private suspend fun loadCategories(value: TransactionType) {
        _categories.value =
            ValueWithError(repository.getCategoriesByType(value).mapIndexed { index, it ->
                SelectableItem(it, it.name, index == 0)
            })
    }

}

@Suppress("UNCHECKED_CAST")
@Singleton
class TransactionEditorFactory @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val moneyPresenter: MoneyPresenter,
    private val datePresenter: DatePresenter,
    private val androidMessages: Messages
) {

    fun make(id: UUID?): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TransactionEditorViewModel(
                    id,
                    repository,
                    labelPresenter,
                    moneyPresenter,
                    datePresenter,
                    androidMessages
                ) as T
            }

        }

    }

}