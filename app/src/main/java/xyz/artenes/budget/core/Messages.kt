package xyz.artenes.budget.core

interface Messages {

    fun get(id: Int, vararg args: Any): String

}