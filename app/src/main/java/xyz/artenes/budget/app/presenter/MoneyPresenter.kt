package xyz.artenes.budget.app.presenter

import xyz.artenes.budget.core.models.FunctionResult
import xyz.artenes.budget.core.models.Money
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MoneyPresenter {

    fun getCurrencySymbol(): String = Currency.getInstance(Locale.getDefault()).symbol

    fun formatFromString(value: String): FunctionResult<String> {
        val result = parse(value)
        if (result is FunctionResult.Error) {
            return result
        }
        val money = (result as FunctionResult.Success).data
        val formattedMoney = formatMoney(money)
        return FunctionResult.Success(formattedMoney)
    }

    fun parse(value: String): FunctionResult<Money> {

        val cleanValue = value.replace(".", "").replace(",", "")

        if (cleanValue.length > 9) {
            return FunctionResult.Error(NumberTooBigException(cleanValue))
        }

        if (cleanValue.isEmpty()) {
            return FunctionResult.Error(NotANumberException(cleanValue))
        }

        return try {

            FunctionResult.Success(Money(cleanValue.toInt()))

        } catch (exception: NumberFormatException) {

            FunctionResult.Error(NotANumberException(cleanValue))

        }

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

    class NumberTooBigException(val rawValue: String) : Exception()

    class NotANumberException(val rawValue: String) : Exception()

}