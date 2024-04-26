package xyz.artenes.budget.utils

data class LoadingData<T>(
    val loading: Boolean,
    val data: T?
)
