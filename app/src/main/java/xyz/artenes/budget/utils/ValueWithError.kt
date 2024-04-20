package xyz.artenes.budget.utils

data class ValueWithError(
    val value: String = "",
    val error: String? = null
) {

    fun hasError() = error != null

}