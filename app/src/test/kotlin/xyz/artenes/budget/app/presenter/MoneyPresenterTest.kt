package xyz.artenes.budget.app.presenter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import xyz.artenes.budget.core.models.FunctionResult

class MoneyPresenterTest {

    @Test
    fun parse_moneyValues() {

        val moneyPresenter = MoneyPresenter()
        assertEquals(
            193490,
            (moneyPresenter.parse("1.934,90") as FunctionResult.Success).data.value
        )
        assertEquals(93490, (moneyPresenter.parse("934,90") as FunctionResult.Success).data.value)
        assertEquals(490, (moneyPresenter.parse("4,90") as FunctionResult.Success).data.value)
        assertEquals(90, (moneyPresenter.parse("0,90") as FunctionResult.Success).data.value)
        assertEquals(1, (moneyPresenter.parse("0,01") as FunctionResult.Success).data.value)
        assertEquals(
            193490,
            (moneyPresenter.parse("1,934.90") as FunctionResult.Success).data.value
        )
        assertTrue((moneyPresenter.parse("") as FunctionResult.Error).exception is MoneyPresenter.NotANumberException)
        assertTrue((moneyPresenter.parse("-") as FunctionResult.Error).exception is MoneyPresenter.NotANumberException)
        assertTrue((moneyPresenter.parse("a") as FunctionResult.Error).exception is MoneyPresenter.NotANumberException)
        assertTrue((moneyPresenter.parse("3849585749028373") as FunctionResult.Error).exception is MoneyPresenter.NumberTooBigException)

    }

}