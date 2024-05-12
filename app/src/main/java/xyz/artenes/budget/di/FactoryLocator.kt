package xyz.artenes.budget.di

import xyz.artenes.budget.app.category.editor.CategoryEditorFactory
import xyz.artenes.budget.app.transaction.editor.TransactionEditorFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class FactoryLocatorMapping @Inject constructor(
    val transactionEditorFactory: TransactionEditorFactory,
    val categoryEditorFactory: CategoryEditorFactory
)

object FactoryLocator {

    lateinit var instance: FactoryLocatorMapping

}