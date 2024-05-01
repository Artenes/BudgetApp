package xyz.artenes.budget.app.load

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import xyz.artenes.budget.BuildConfig
import xyz.artenes.budget.data.DatabaseSeeder
import xyz.artenes.budget.utils.Event
import javax.inject.Inject

@HiltViewModel
class InitialLoadViewModel @Inject constructor(private val seeder: DatabaseSeeder) : ViewModel() {

    private val _event = MutableStateFlow(Event())
    val event: StateFlow<Event> = _event

    init {
        viewModelScope.launch {
            if (BuildConfig.LOAD_DATA) {
                seeder.seedTest()
            } else {
                seeder.seed()
            }
            _event.value = Event("finish")
        }
    }

}