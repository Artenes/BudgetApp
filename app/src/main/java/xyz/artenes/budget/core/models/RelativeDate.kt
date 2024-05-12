package xyz.artenes.budget.core.models

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