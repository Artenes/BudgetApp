package xyz.artenes.budget.utils

data class ListWithValue<T>(
    val value: T? = null,
    val list: List<T> = emptyList()
)
