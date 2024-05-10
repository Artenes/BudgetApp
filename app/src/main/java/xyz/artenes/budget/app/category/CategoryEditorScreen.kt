package xyz.artenes.budget.app.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    id: UUID? = null,
    onBack: () -> Unit,
) {

    Scaffold(
        topBar = {

            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    if (id != null) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = ""
                            )
                        }
                    }
                }
            )

        },
    ) { screen ->

        Column(modifier = Modifier.padding(screen)) {

            Text(text = id?.toString() ?: "null")

        }

    }

}