package xyz.artenes.budget.app.presenter

import xyz.artenes.budget.core.models.Money
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MoneyPresenter {

    fun getCurrencySymbol(): String = Currency.getInstance(Locale.getDefault()).symbol

    fun formatFromString(value: String): String {
        val money = parse(value)
        return formatMoney(money)
    }

    fun parse(value: String): Money {
        var cleanValue = value.replace(".", "").replace(",", "")
        if (cleanValue.length > 9) {
            cleanValue = cleanValue.substring(0, 9)
        }
        if (cleanValue.isEmpty()) {
            cleanValue = "0"
        }
        return Money(cleanValue.toInt())
    }

    fun formatMoney(money: Money): String {
        return getMoneyFormat().format(money.toDouble)
    }

    fun formatMoneyWithCurrency(money: Money): String {
        return "${getCurrencySymbol()} ${getMoneyFormat().format(money.toDouble)}"
    }

    private fun getMoneyFormat() = NumberFormat.getNumberInstance().also {
        it.minimumFractionDigits = 2
        it.maximumFractionDigits = 2
    }

}