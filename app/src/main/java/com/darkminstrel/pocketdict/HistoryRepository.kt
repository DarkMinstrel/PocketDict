package com.darkminstrel.pocketdict

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private const val LIMIT = 100

@Immutable
data class HistoryItem(val key: String, val value: String)
@Immutable
data class History(
    val items: List<HistoryItem> = emptyList(),
)

interface HistoryRepository {
    val flow: Flow<History>
    suspend fun addItem(key: String, value: String)
    suspend fun deleteItem(key: String)
}

class HistoryRepositoryImpl(
    private val datastore: DataStore<History>,
) : HistoryRepository {
    override val flow = datastore.data
    override suspend fun addItem(key: String, value: String): Unit = withContext(Dispatchers.IO) {
        datastore.updateData { old ->
            History(
                ArrayList<HistoryItem>(old.items.size + 1).apply {
                    add(HistoryItem(key, value))
                    addAll(old.items.filter { it.key != key })
                }.take(LIMIT)
            )
        }
    }

    override suspend fun deleteItem(key: String): Unit = withContext(Dispatchers.IO) {
        datastore.updateData { old ->
            History(old.items.filter { it.key != key })
        }
    }
}