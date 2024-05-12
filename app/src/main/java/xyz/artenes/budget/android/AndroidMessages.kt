package xyz.artenes.budget.android

import android.content.Context
import xyz.artenes.budget.core.Messages

class AndroidMessages(private val context: Context) : Messages {

    override fun get(id: Int, vararg args: Any) = context.getString(id, *args)

}