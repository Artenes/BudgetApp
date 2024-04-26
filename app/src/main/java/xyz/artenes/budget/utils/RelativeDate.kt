package xyz.artenes.budget.utils

data class RelativeDate(
    val relative: String,
    val absolute: String
) {

    val isRelative = relative.isNotEmpty()

    val displayValue = relative.ifEmpty {
        absolute
    }

    override fun toString(): String {
        return "$relative ($absolute)"
    }

}