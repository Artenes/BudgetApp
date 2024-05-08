package xyz.artenes.budget.di

import xyz.artenes.budget.app.transaction.editor.TransactionEditorFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class FactoryLocatorMapping @Inject constructor(
    val transactionEditorFactory: TransactionEditorFactory
)

object FactoryLocator {

    lateinit var instance: FactoryLocatorMapping

}