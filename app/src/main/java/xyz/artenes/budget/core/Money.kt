package xyz.artenes.budget.core

import java.text.NumberFormat

data class Money(val value: Int) {

    val toDouble: Double
        get() = value / 100.0

    fun minus(other: Money): Money {
        return Money(value - other.value)
    }

    fun format(format: NumberFormat): String {
        return format.format(toDouble)
    }

    override fun toString(): String {
        return toDouble.toString()
    }

}