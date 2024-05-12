package xyz.artenes.budget.core.models

sealed class DataState<out T> {
    data object Uninitialized : DataState<Nothing>()
    data object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val exception: Throwable) : DataState<Nothing>()
}