package xyz.artenes.budget.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EscalatorWarning
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.Gite
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.OtherHouses
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.RoomPreferences
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Villa
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.artenes.budget.app.theme.CustomColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomIconPickerInput(
    label: String,
    value: ImageVector,
    onIconSelected: (ImageVector) -> Unit,
    modifier: Modifier = Modifier
) {

    val coroutine = rememberCoroutineScope()
    val icons = items(value)
    val state = rememberLazyGridState()
    var showDialog by remember {
        mutableStateOf(false)
    }

    val dismiss: () -> Unit = {
        showDialog = false
        coroutine.launch {
            state.scrollToItem(icons.indexOfFirst { it.selected })
        }
    }

    if (showDialog) {

        LaunchedEffect(key1 = value) {
            val index = icons.indexOfFirst { it.selected }
            Timber.d("Icon: ${value.name}, selected: $index")
            state.scrollToItem(index)
        }

        BasicAlertDialog(onDismissRequest = dismiss) {
            Surface(
                color = DatePickerDefaults.colors().containerColor,
                shape = DatePickerDefaults.shape,
                modifier = Modifier
                    .heightIn(max = 580.dp)
                    .requiredWidth(360.dp)
            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(30.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    state = state
                ) {

                    items(
                        count = icons.size,
                        key = { index -> icons[index].value.name }
                    ) { index ->

                        Box(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onBackground,
                                    MaterialTheme.shapes.small
                                )
                                .background(
                                    if (icons[index].selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    MaterialTheme.shapes.small
                                )
                                .clickable {
                                    onIconSelected(icons[index].value)
                                    dismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = icons[index].value,
                                contentDescription = "",
                                modifier = Modifier.size(100.dp),
                            )

                        }

                    }

                }

            }
        }

    }

    Box(
        modifier = Modifier.then(modifier)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    RoundedCornerShape(5.dp),
                )
                .clickable {
                    showDialog = true
                },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = value,
                contentDescription = "",
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(50.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

        }

        Text(
            text = label,
            color = CustomColorScheme.textColor(),
            modifier = Modifier
                .offset(y = (-8).dp, x = 20.dp)
                .background(MaterialTheme.colorScheme.background),
            style = MaterialTheme.typography.bodySmall,
        )

    }

}

private data class IconItem(
    val value: ImageVector,
    val selected: Boolean = false
)

private fun items(selected: ImageVector): List<IconItem> = listOf(
    IconItem(Icons.Filled.Storefront),
    IconItem(Icons.Filled.Apartment),
    IconItem(Icons.Filled.FitnessCenter),
    IconItem(Icons.Filled.BusinessCenter),
    IconItem(Icons.Filled.Spa),
    IconItem(Icons.Filled.MeetingRoom),
    IconItem(Icons.Filled.House),
    IconItem(Icons.Filled.CorporateFare),
    IconItem(Icons.Filled.AcUnit),
    IconItem(Icons.Filled.Cottage),
    IconItem(Icons.Filled.FamilyRestroom),
    IconItem(Icons.Filled.Checkroom),
    IconItem(Icons.Filled.OtherHouses),
    IconItem(Icons.Filled.AllInclusive),
    IconItem(Icons.Filled.Grass),
    IconItem(Icons.Filled.AirportShuttle),
    IconItem(Icons.Filled.ChildCare),
    IconItem(Icons.Filled.BeachAccess),
    IconItem(Icons.Filled.Pool),
    IconItem(Icons.Filled.Kitchen),
    IconItem(Icons.Filled.Casino),
    IconItem(Icons.Filled.HolidayVillage),
    IconItem(Icons.Filled.RoomService),
    IconItem(Icons.Filled.Roofing),
    IconItem(Icons.Filled.RoomPreferences),
    IconItem(Icons.Filled.SportsBar),
    IconItem(Icons.Filled.FreeBreakfast),
    IconItem(Icons.Filled.Bathtub),
    IconItem(Icons.Filled.EscalatorWarning),
    IconItem(Icons.Filled.ChildFriendly),
    IconItem(Icons.Filled.Foundation),
    IconItem(Icons.Filled.FoodBank),
    IconItem(Icons.Filled.Gite),
    IconItem(Icons.Filled.Villa),
    IconItem(Icons.Filled.NightShelter),
    IconItem(Icons.Filled.DirectionsBus),
    IconItem(Icons.Filled.MonitorHeart),
    IconItem(Icons.Filled.LocalGroceryStore),
    IconItem(Icons.Filled.Book),
    IconItem(Icons.Filled.Home),
    IconItem(Icons.Filled.QuestionMark),
    IconItem(Icons.Filled.MonetizationOn),
).map { item ->
    item.copy(selected = item.value == selected)
}