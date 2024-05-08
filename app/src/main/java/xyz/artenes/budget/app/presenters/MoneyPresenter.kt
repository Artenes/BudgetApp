package xyz.artenes.budget.app.presenters

import xyz.artenes.budget.core.Money
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoneyPresenter @Inject constructor() {


    fun getCurrencySymbol() = Currency.getInstance(Locale.getDefault()).symbol

    fun formatFromString(value: String): String {
        val money = parse(value)
        return getMoneyFormat().format(money.toDouble)
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