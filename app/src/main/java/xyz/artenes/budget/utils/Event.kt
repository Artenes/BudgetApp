package xyz.artenes.budget.utils

class Event(private val name: String? = null) {

    private var consumed: Boolean = false

    fun consume(consumer: (String) -> Unit) {
        if (!consumed && name != null) {
            consumer(name)
            consumed = true
        }
    }

}