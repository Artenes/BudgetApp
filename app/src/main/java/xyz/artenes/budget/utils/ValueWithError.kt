package xyz.artenes.budget.utils

data class ValueWithError<T>(
    val value: T,
    val error: String? = null
) {

    fun hasError() = error != null

}