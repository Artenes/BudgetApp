package xyz.artenes.budget.app.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.utils.LabelPresenter
import xyz.artenes.budget.utils.ValueWithError
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

class CategoryEditorViewModel @Inject constructor(
    private val id: UUID?,
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter
) : ViewModel() {

    private val _name = MutableStateFlow(ValueWithError())
    val name: StateFlow<ValueWithError> = _name

    init {
        viewModelScope.launch {
            if (id == null) {
                return@launch
            }
            val category = repository.getCategoryById(id)
            _name.value = ValueWithError(category.name)
        }
    }

    fun setName(value: String) {
        _name.value = ValueWithError(value)
    }

}

@Suppress("UNCHECKED_CAST")
@Singleton
class CategoryEditorFactory @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter,
) {

    fun make(id: UUID?): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryEditorViewModel(
                    id,
                    repository,
                    labelPresenter
                ) as T
            }

        }

    }

}