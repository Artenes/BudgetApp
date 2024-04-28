package xyz.artenes.budget.app.load

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.app.theme.CustomColorScheme

@Composable
fun InitialLoadScreen(
    viewModel: InitialLoadViewModel = hiltViewModel(),
    goToTransactionsScreen: () -> Unit
) {

    val event by viewModel.event.collectAsState()

    LaunchedEffect(event) {
        event.consume {
            goToTransactionsScreen()
        }
    }

    Scaffold {

        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Loading initial data",
                    color = CustomColorScheme.textColor()
                )
            }

        }

    }

}