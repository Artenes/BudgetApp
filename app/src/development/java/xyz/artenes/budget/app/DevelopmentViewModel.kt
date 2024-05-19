package xyz.artenes.budget.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.artenes.budget.core.models.Event
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.DatabaseSeeder
import javax.inject.Inject

@HiltViewModel
class DevelopmentViewModel @Inject constructor(
    private val repository: AppRepository,
    private val seeder: DatabaseSeeder
) : ViewModel() {

    private val _showSnackBar = MutableStateFlow(Event())
    val showSnackBar: StateFlow<Event> = _showSnackBar

    private val _deleteAllButtonState = MutableStateFlow(true)
    val deleteAllButtonState: StateFlow<Boolean> = _deleteAllButtonState

    private val _insertFakeDataButtonState = MutableStateFlow(true)
    val insertFakeDataButtonState: StateFlow<Boolean> = _insertFakeDataButtonState

    fun deleteAll() {
        viewModelScope.launch {
            _deleteAllButtonState.value = false
            repository.clearDatabase()
            _deleteAllButtonState.value = true
            _showSnackBar.value = Event("Done")
        }
    }

    fun insertFakeData() {
        viewModelScope.launch {
            _insertFakeDataButtonState.value = false
            seeder.seedTest()
            _insertFakeDataButtonState.value = true
            _showSnackBar.value = Event("Done")
        }
    }

}