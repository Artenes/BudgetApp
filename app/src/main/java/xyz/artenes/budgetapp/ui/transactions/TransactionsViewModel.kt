package xyz.artenes.budgetapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budgetapp.room.AppDatabase
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val database: AppDatabase) : ViewModel() {

    val transactions = database.transactionsDao().getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}