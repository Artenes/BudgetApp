package xyz.artenes.budget.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DevelopmentScreen(
    viewModel: DevelopmentViewModel = hiltViewModel()
) {

    val deleteAllButtonState by viewModel.deleteAllButtonState.collectAsState()
    val insertFakeDataButtonState by viewModel.insertFakeDataButtonState.collectAsState()
    val showSnackBar by viewModel.showSnackBar.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = showSnackBar) {
        showSnackBar.consume {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(it)
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {

        Column(modifier = Modifier.padding(it).padding(30.dp)) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.deleteAll() },
                enabled = deleteAllButtonState
            ) {
                Text(text = "Clear database")
            }

            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                onClick = { viewModel.insertFakeData() },
                enabled = insertFakeDataButtonState
            ) {
                Text(text = "Insert fake data")
            }

        }

    }

}