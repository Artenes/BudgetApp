package xyz.artenes.budget.android

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Messages @Inject constructor(@ApplicationContext private val context: Context) {

    fun get(id: Int, vararg args: Any) = context.getString(id, *args)

}