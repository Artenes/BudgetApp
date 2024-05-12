package xyz.artenes.budget.app.category.editor

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
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.core.models.Event
import xyz.artenes.budget.core.models.SelectableItem
import xyz.artenes.budget.core.models.TransactionType
import xyz.artenes.budget.core.models.ValueWithError
import xyz.artenes.budget.core.presenter.LabelPresenter
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.models.CategoryEntity
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

object CategoryEditorActions {

    const val FINISH = "finish"
    const val REASSIGN_CATEGORY = "reassign_category"
    const val CONFIRM_DELETE = "confirm_delete"

}

class CategoryEditorViewModel @Inject constructor(
    private val id: UUID?,
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val androidMessages: Messages,
) : ViewModel() {

    private lateinit var createdAt: OffsetDateTime

    private var hasDependencies = false

    private val _lastCategory = MutableStateFlow(false)
    val lastCategory: StateFlow<Boolean> = _lastCategory

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

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

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
            hasDependencies = repository.categoryHasDependencies(id)
            _lastCategory.value = repository.getCategoryCountByType(category.type) == 1
            _categories.value = repository.getCategoriesByType(category.type).filter { it.id != id }
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
            _name.value = name.copy(error = androidMessages.get(R.string.required))
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
            _event.value = Event(CategoryEditorActions.FINISH)
        }

    }

    fun requestDelete() {

        if (id == null) {
            throw NullPointerException("Can't delete null category")
        }

        if (_lastCategory.value) {
            throw RuntimeException("Can't delete the last category of a type")
        }

        if (hasDependencies) {
            _event.value = Event(CategoryEditorActions.REASSIGN_CATEGORY)
            return
        }

        _event.value = Event(CategoryEditorActions.CONFIRM_DELETE)

    }

    fun delete() {

        if (id == null) {
            throw NullPointerException("Can't delete null category")
        }

        viewModelScope.launch {
            repository.softDeleteCategoryById(id)
            _event.value = Event(CategoryEditorActions.FINISH)
        }
    }

    fun deleteAndReassign(newCategory: CategoryEntity) {

        if (id == null) {
            throw NullPointerException("Can't delete null category")
        }

        viewModelScope.launch {
            repository.replaceCategoryInTransactions(id, newCategory.id)
            repository.softDeleteCategoryById(id)
            _event.value = Event(CategoryEditorActions.FINISH)
        }

    }

}

@Suppress("UNCHECKED_CAST")
@Singleton
class CategoryEditorFactory @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
    private val androidMessages: Messages
) {

    fun make(id: UUID?): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryEditorViewModel(
                    id,
                    repository,
                    labelPresenter,
                    androidMessages
                ) as T
            }

        }

    }

}