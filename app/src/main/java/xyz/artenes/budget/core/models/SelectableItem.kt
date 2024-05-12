package xyz.artenes.budget.core.models

data class SelectableItem<T>(
    val value: T,
    val label: String,
    val selected: Boolean,
    val isInvalid: Boolean = false
)