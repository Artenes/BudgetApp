package xyz.artenes.budget.utils

data class SelectableItem<T>(
    val value: T,
    val label: String,
    val selected: Boolean
)