package xyz.artenes.budget.app.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.CategoryEntity
import xyz.artenes.budget.utils.Event
import xyz.artenes.budget.utils.LabelPresenter
import xyz.artenes.budget.utils.SelectableItem
import xyz.artenes.budget.utils.ValueWithError
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

class CategoryEditorViewModel @Inject constructor(
    private val id: UUID?,
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val messages: Messages,
) : ViewModel() {

    private lateinit var createdAt: OffsetDateTime

    private val _name = MutableStateFlow(ValueWithError(""))
    val name: StateFlow<ValueWithError<String>> = _name

    private val _icon = MutableStateFlow(ValueWithError(Icons.Filled.Storefront))
    val icon: StateFlow<ValueWithError<ImageVector>> = _icon

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
                true
            ),
        )
    )
    val types: StateFlow<List<SelectableItem<TransactionType>>> = _types

    private val _event = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    init {
        viewModelScope.launch {
            if (id == null) {
                createdAt = OffsetDateTime.now()
                return@launch
            }
            val category = repository.getCategoryById(id)
            createdAt = category.createdAt
            setName(category.name)
            setType(category.type)
            setIcon(category.icon)
        }
    }

    fun setName(value: String) {
        _name.value = ValueWithError(value)
    }

    fun setType(value: TransactionType) {
        _types.value =
            _types.value.map { item -> item.copy(selected = item.value == value) }
    }

    fun setIcon(value: ImageVector) {
        _icon.value = ValueWithError(value)
    }

    fun save() {

        val name = _name.value
        val type = _types.value.first { it.selected }.value
        val icon = _icon.value

        if (name.value.isEmpty()) {
            _name.value = name.copy(error = messages.get(R.string.required))
            return
        }

        viewModelScope.launch {
            repository.saveCategory(
                CategoryEntity(
                    id = id ?: UUID.randomUUID(),
                    name = name.value,
                    color = Color.Transparent.toArgb(),
                    icon = icon.value,
                    type = type,
                    createdAt = createdAt,
                    deletedAt = null
                )
            )
            _event.value = Event("Finish")
        }

    }

}

@Suppress("UNCHECKED_CAST")
@Singleton
class CategoryEditorFactory @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val messages: Messages
) {

    fun make(id: UUID?): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryEditorViewModel(
                    id,
                    repository,
                    labelPresenter,
                    messages
                ) as T
            }

        }

    }

}