package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.data.AppRepository
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    val transactions =
        repository.getAllTransactions().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}