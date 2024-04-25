package xyz.artenes.budget.utils

data class RelativeDate(
    val relative: String,
    val absolute: String
) {

    val isRelative = relative.isNotEmpty()

    override fun toString(): String {
        return "$relative ($absolute)"
    }

}