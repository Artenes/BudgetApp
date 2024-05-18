package xyz.artenes.budget.app.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import xyz.artenes.budget.BuildConfig
import xyz.artenes.budget.R
import xyz.artenes.budget.app.theme.CustomColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    navigateBack: () -> Unit,
    viewModel: InfoViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val userId by viewModel.userId.collectAsState()
    val event by viewModel.event.collectAsState()

    LaunchedEffect(key1 = event) {
        event.consume {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = CustomColorScheme.topAppBar(),
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                            tint = CustomColorScheme.icon()
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {

        Column(
            Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_monochrome),
                contentDescription = "",
                colorFilter = CustomColorScheme.image(),
                modifier = Modifier.size(150.dp)
            )

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = CustomColorScheme.textColor()
            )

            Text(
                text = stringResource(id = R.string.version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyLarge,
                color = CustomColorScheme.textColorLight()
            )

            Text(
                text = stringResource(id = R.string.user_id, userId),
                style = MaterialTheme.typography.bodyLarge,
                color = CustomColorScheme.textColorLight()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(id = R.string.made_by),
                style = MaterialTheme.typography.titleMedium,
                color = CustomColorScheme.textColor()
            )

            Text(
                modifier = Modifier.clickable {
                    viewModel.sendEmail()
                },
                text = stringResource(id = R.string.dev_email),
                style = MaterialTheme.typography.titleMedium,
                color = CustomColorScheme.textColor(),
                textDecoration = TextDecoration.Underline
            )

        }

    }

}