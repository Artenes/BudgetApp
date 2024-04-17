package xyz.artenes.budgetapp.utils

class Event(private val ignore: Boolean = false) {

    private var consumed: Boolean = false

    fun consume(consumer: () -> Unit) {
        if (!consumed && !ignore) {
            consumer()
            consumed = true
        }
    }

}