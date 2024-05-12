package xyz.artenes.budget.core

import kotlinx.coroutines.flow.Flow

interface UserPreferences {

    suspend fun save(key: String, value: String)

    suspend fun get(key: String, default: String = ""): String

    fun listen(key: String, default: String = ""): Flow<String>

}