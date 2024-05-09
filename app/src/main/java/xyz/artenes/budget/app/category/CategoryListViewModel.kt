package xyz.artenes.budget.app.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.utils.LabelPresenter
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val repository: AppRepository,
    private val labelPresenter: LabelPresenter
) : ViewModel() {

    val categories = repository.getAllCategoriesAsFlow().map { categories ->

        categories.map { category ->

            CategoryItem(
                id = category.id,
                name = category.name,
                icon = category.icon,
                type = labelPresenter.present(category.type)
            )

        }

    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}