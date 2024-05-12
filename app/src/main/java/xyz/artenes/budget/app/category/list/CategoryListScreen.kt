package xyz.artenes.budget.app.category.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.R
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.core.models.CategoryItem
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onBack: () -> Unit,
    navigateToNewCategory: () -> Unit,
    navigateToCategory: (UUID) -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {

    val categories by viewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = MaterialTheme.colorScheme.background),
                title = {
                    Text(
                        text = stringResource(R.string.categories),
                        color = CustomColorScheme.textColor(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
            )
        },
        floatingActionButton = {

            FloatingActionButton(onClick = navigateToNewCategory) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }

        }
    ) { screen ->

        Column(modifier = Modifier.padding(screen)) {

            LazyColumn {

                items(
                    count = categories.size,
                    key = { index -> categories[index].id }
                ) { index ->

                    Category(
                        category = categories[index],
                        navigateToCategory = navigateToCategory
                    )

                }

            }

        }

    }

}

@Composable
fun Category(
    category: CategoryItem,
    navigateToCategory: (UUID) -> Unit
) {

    Surface(
        color = Color.Transparent,
        onClick = { navigateToCategory(category.id) }
    ) {

        Column {

            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(2f)
                ) {

                    /*
                    Description
                     */
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                }

                /*
                Amount
                 */
                Text(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    text = category.type,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )

            }

            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
            )

        }

    }

}