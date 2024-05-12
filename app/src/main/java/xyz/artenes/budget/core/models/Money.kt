package xyz.artenes.budget.core.models

import kotlin.math.abs

data class Money(val value: Int) {

    val toDouble: Double
        get() = value / 100.0

    fun minus(other: Money): Money {
        return Money(value - other.value)
    }

    fun absolute(): Money {
        return Money(abs(value))
    }

    override fun toString(): String {
        return toDouble.toString()
    }

}