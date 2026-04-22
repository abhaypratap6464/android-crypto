package com.abhay.crypto.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abhay.crypto.data.mapper.CoinMapper
import com.abhay.crypto.data.remote.BinanceApi
import com.abhay.crypto.domain.model.Coin
import retrofit2.HttpException
import java.io.IOException

class CoinPagingSource(
    private val api: BinanceApi,
    // Callback instead of direct MutableStateFlow mutation — keeps the paging source
    // decoupled from repository internals (single-responsibility).
    private val onPricesCached: (Map<String, Double>) -> Unit,
) : PagingSource<Int, Coin>() {

    private var coins: List<Coin>? = null

    override fun getRefreshKey(state: PagingState<Int, Coin>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Coin> {
        return try {
            val allCoins = coins ?: api.getTickerPrices()
                .filter { it.symbol.endsWith("USDT") }
                .map { CoinMapper.toDomain(it) }
                .also { list ->
                    coins = list
                    onPricesCached(list.associate { it.symbol to it.price })
                }

            val start = params.key ?: 0
            val end = (start + params.loadSize).coerceAtMost(allCoins.size)
            LoadResult.Page(
                data = allCoins.subList(start, end),
                prevKey = if (start == 0) null else (start - params.loadSize).coerceAtLeast(0),
                nextKey = if (end == allCoins.size) null else end,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
