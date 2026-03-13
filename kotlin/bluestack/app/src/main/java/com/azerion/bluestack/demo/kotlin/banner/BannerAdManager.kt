package com.azerion.bluestack.demo.kotlin.banner

import android.content.Context
import com.azerion.bluestack.banner.BannerAdSize
import com.azerion.bluestack.banner.BannerView
import com.azerion.bluestack.demo.kotlin.Logger

/**
 * Manager class for multiple banner ads using iOS-style BannerAdViewModel pattern.
 * Each banner is managed by its own ViewModel instance for better encapsulation.
 */
class BannerAdManager(
    private val context: Context,
    private val placementId: String,
    private val adSize: BannerAdSize,
    private val listener: BannerAdListener
) {
    private val TAG = "BannerAdManager"

    // Internal map to track banner views by their unique IDs
    private val viewModelMap: MutableMap<String, BannerAdViewModel> = LinkedHashMap()

    // Track the original count of banners for reload functionality
    private var originalBannerCount: Int = 0
    private var currentLoadingIndex: Int = 0

    private var state: BannerListState = BannerListState.Idle()
        set(value) {
            field = value
            listener.onStateChanged(value)
        }

    // Track if first ad has loaded (for UX optimization)
    private var isFirstAdLoaded: Boolean = false

    /**
     * Creates multiple banner ad instances.
     *
     * @param count Number of banner ads to create
     * @return List of unique banner IDs
     */
    fun create(count: Int): List<String> {
        Logger.d(TAG, "Creating $count banner ViewModels")

        if (originalBannerCount == 0 || count > originalBannerCount) {
            originalBannerCount = count
        }

        val bannerIds = mutableListOf<String>()

        repeat(count) { index ->
            val bannerId = "banner_$index"

            val viewModel = BannerAdViewModel(context, adSize, placementId).apply {
                onAdLoaded = { height -> handleBannerLoaded(bannerId, height) }
                onAdFailedToLoad = { error -> handleBannerFailedToLoad(bannerId, error) }
                onAdRefresh = { listener.onBannerRefreshed(bannerId) }
                onAdFailedToRefresh = { error -> listener.onBannerFailedToRefresh(bannerId, error) }
                onAdResize = { size -> listener.onBannerResized(bannerId, size) }
                onAdClick = { listener.onBannerClicked(bannerId) }
            }

            viewModelMap[bannerId] = viewModel
            bannerIds.add(bannerId)
        }

        return bannerIds
    }

    /**
     * Loads banner ads. If bannerId is provided, loads only that banner.
     * Otherwise, loads all banners sequentially.
     *
     * @param bannerId Optional specific banner ID to load. If null, loads all banners.
     */
    fun load(bannerId: String? = null) {
        if (bannerId != null) {
            viewModelMap[bannerId]?.loadAd()
        } else {
            // Load all banners sequentially
            isFirstAdLoaded = false
            state = BannerListState.Loading(autoRefreshEnabled = state.getAutoRefreshEnabled())
            currentLoadingIndex = 0
            loadBannerAtIndex(0)
        }
    }

    private fun loadBannerAtIndex(index: Int) {
        val bannerIds = viewModelMap.keys.toList()

        if (index >= bannerIds.size) {
            if (state is BannerListState.Loading) {
                state = BannerListState.Loaded(
                    visible = true,
                    autoRefreshEnabled = (state as? BannerListState.Loading)?.autoRefreshEnabled ?: true
                )
            }
            return
        }

        val bannerId = bannerIds[index]
        val viewModel = viewModelMap[bannerId]

        if (viewModel == null) {
            loadBannerAtIndex(index + 1)
            return
        }

        currentLoadingIndex = index
        viewModel.loadAd()
    }

    private fun handleBannerLoaded(bannerId: String, height: Int) {
        listener.onBannerLoaded(bannerId, height)

        if (!isFirstAdLoaded && state is BannerListState.Loading) {
            isFirstAdLoaded = true
            state = BannerListState.Loaded(
                visible = true,
                autoRefreshEnabled = (state as? BannerListState.Loading)?.autoRefreshEnabled ?: true
            )
        }

        loadBannerAtIndex(currentLoadingIndex + 1)
    }

    private fun handleBannerFailedToLoad(bannerId: String, error: Exception) {
        listener.onBannerFailedToLoad(bannerId, error)
        loadBannerAtIndex(currentLoadingIndex + 1)
    }

    fun getView(bannerId: String): BannerView? {
        return viewModelMap[bannerId]?.getView()
    }

    fun destroy(bannerId: String? = null) {
        if (bannerId != null) {
            viewModelMap[bannerId]?.destroyAd()
            viewModelMap.remove(bannerId)
        } else {
            viewModelMap.values.forEach { it.destroyAd() }
            viewModelMap.clear()
            state = BannerListState.Idle(autoRefreshEnabled = state.getAutoRefreshEnabled())
        }
    }

    fun pause(bannerId: String? = null) {
        if (bannerId != null) {
            viewModelMap[bannerId]?.stopRefresh()
        } else {
            viewModelMap.values.forEach { it.stopRefresh() }
        }
    }

    fun resume(bannerId: String? = null) {
        val currentAutoRefresh = state.getAutoRefreshEnabled()
        if (!currentAutoRefresh) return

        if (bannerId != null) {
            viewModelMap[bannerId]?.startRefresh()
        } else {
            viewModelMap.values.forEach { it.startRefresh() }
        }
    }

    fun hide() {
        if (state is BannerListState.Loaded) {
            state = (state as BannerListState.Loaded).copy(visible = false)
        }
    }

    fun show() {
        if (state is BannerListState.Loaded) {
            state = (state as BannerListState.Loaded).copy(visible = true)
        }
    }

    fun toggleAutoRefresh(enabled: Boolean, bannerId: String? = null) {
        if (bannerId != null) {
            val viewModel = viewModelMap[bannerId]
            if (enabled) viewModel?.startRefresh() else viewModel?.stopRefresh()
        } else {
            viewModelMap.values.forEach { viewModel ->
                if (enabled) viewModel.startRefresh() else viewModel.stopRefresh()
            }
        }

        state = when (val s = state) {
            is BannerListState.Loaded -> s.copy(autoRefreshEnabled = enabled)
            is BannerListState.Idle -> s.copy(autoRefreshEnabled = enabled)
            is BannerListState.Loading -> s.copy(autoRefreshEnabled = enabled)
        }
    }

    fun isIdle(): Boolean = state is BannerListState.Idle
    fun isLoading(): Boolean = state is BannerListState.Loading
    fun isLoaded(): Boolean = state is BannerListState.Loaded
    fun isVisible(): Boolean = (state as? BannerListState.Loaded)?.visible ?: false
    fun isAutoRefreshEnabled(): Boolean = state.getAutoRefreshEnabled()

    fun createAndLoad() {
        val currentAutoRefresh = state.getAutoRefreshEnabled()
        destroy()
        create(originalBannerCount)
        state = BannerListState.Loading(autoRefreshEnabled = currentAutoRefresh)
        load()
    }
}

private fun BannerListState.getAutoRefreshEnabled(): Boolean {
    return when (this) {
        is BannerListState.Loaded -> this.autoRefreshEnabled
        is BannerListState.Idle -> this.autoRefreshEnabled
        is BannerListState.Loading -> this.autoRefreshEnabled
    }
}
