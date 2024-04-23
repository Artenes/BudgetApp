package xyz.artenes.budget.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector

object IconsMap {

    fun getIcon(name: String): ImageVector {

        return when (name) {
            "Outlined.DirectionsBus" -> Icons.Outlined.DirectionsBus
            "Outlined.MonitorHeart" -> Icons.Outlined.MonitorHeart
            "Outlined.LocalGroceryStore" -> Icons.Outlined.LocalGroceryStore
            "Outlined.Book" -> Icons.Outlined.Book
            "Outlined.Home" -> Icons.Outlined.Home
            "Outlined.QuestionMark" -> Icons.Outlined.QuestionMark
            "Outlined.Money" -> Icons.Outlined.Money
            else -> throw RuntimeException("Invalid icon $name")
        }

    }

}