package xyz.artenes.budget.core

import java.text.NumberFormat

data class Money(val amount: Int) {

    val toDouble: Double
        get() = amount / 100.0

    fun toSigned(type: TransactionType) = if (type == TransactionType.EXPENSE) {
        -amount
    } else {
        amount
    }

    fun format(format: NumberFormat): String {
        return format.format(toDouble)
    }

    override fun toString(): String {
        return toDouble.toString()
    }

}