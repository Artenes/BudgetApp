package xyz.artenes.budget.core.models

data class ValueWithError<T>(
    val value: T,
    val error: String? = null
) {

    fun hasError() = error != null

}