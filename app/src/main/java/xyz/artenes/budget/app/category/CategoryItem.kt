package xyz.artenes.budget.app.category

import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

data class CategoryItem(
    val id: UUID,
    val name: String,
    val icon: ImageVector,
    val type: String,
)
