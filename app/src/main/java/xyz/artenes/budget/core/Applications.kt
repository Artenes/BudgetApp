package xyz.artenes.budget.core

import xyz.artenes.budget.core.models.FunctionResult

interface Applications {

    fun sendEmailTo(email: String, subject: String, message: String = ""): FunctionResult<Nothing>

}