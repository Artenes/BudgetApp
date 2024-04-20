package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.utils.YearAndMonth
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    val transactions =
        repository.getAllTransactions().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _total = MutableStateFlow(0)
    val total: MutableStateFlow<Int> = _total

    init {
        viewModelScope.launch {
            val now = LocalDate.now();
            val yearAndMonth = YearAndMonth.fromLocalDate(now)
            _total.value = repository.totalAmountForMonth(yearAndMonth)
        }
    }

}