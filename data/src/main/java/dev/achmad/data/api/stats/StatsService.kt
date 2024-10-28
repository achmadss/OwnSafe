package dev.achmad.data.api.stats

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val prefix = "stats"

interface StatsService {

    @GET(prefix)
    suspend fun getLatestStats(
        @Query("amount") amount: Int = 1
    ): Response<List<GetStatsResponseBody>>

}