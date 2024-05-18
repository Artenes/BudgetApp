package xyz.artenes.budget.core.models

sealed class FunctionResult<out T> {

    data class Success<T>(val data: T) : FunctionResult<T>()

    data object EmptySuccess : FunctionResult<Nothing>()

    data class Error(val exception: Throwable) : FunctionResult<Nothing>()

}