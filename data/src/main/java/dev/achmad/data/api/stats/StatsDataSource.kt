package dev.achmad.data.api.stats

import dev.achmad.core.network.await

class StatsDataSource(
    private val statsService: StatsService,
) {
    suspend fun getLatestStats() = await { statsService.getLatestStats() }
}