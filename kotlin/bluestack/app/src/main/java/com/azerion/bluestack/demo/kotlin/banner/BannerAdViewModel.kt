package com.azerion.bluestack.demo.kotlin.banner

import android.content.Context
import com.azerion.bluestack.RequestOptions
import com.azerion.bluestack.banner.BannerAdSize
import com.azerion.bluestack.banner.BannerView
import com.azerion.bluestack.banner.BannerViewListener
import com.azerion.bluestack.demo.kotlin.Logger
import com.azerion.bluestack.util.Size

/**
 * View model for managing banner ad lifecycle (iOS pattern).
 * Manages a single banner ad instance with encapsulated state and callbacks.
 */
class BannerAdViewModel(
    private val context: Context,
    val adSize: BannerAdSize,
    val placementId: String
) {
    private val TAG = "BannerAdViewModel"

    enum class AdLoadState {
        NONE, LOADING, LOADED
    }

    var onAdLoaded: ((preferredHeight: Int) -> Unit)? = null
    var onAdFailedToLoad: ((error: Exception) -> Unit)? = null
    var onAdRefresh: (() -> Unit)? = null
    var onAdFailedToRefresh: ((error: Exception) -> Unit)? = null
    var onAdResize: ((size: Size) -> Unit)? = null
    var onAdClick: (() -> Unit)? = null

    private var bannerView: BannerView? = null
    private var adLoadState: AdLoadState = AdLoadState.NONE

    fun loadAd() {
        adLoadState = AdLoadState.LOADING
        bannerView = null

        bannerView = BannerView(context).apply {
            setPlacementId(placementId)
            setAdSize(adSize)
            setBannerViewListener(bannerViewListener)
            load(prepareRequestOptions())
        }

        Logger.d(TAG, "Loading banner ad: $placementId")
    }

    fun isAdLoaded(): Boolean = adLoadState == AdLoadState.LOADED

    fun getView(): BannerView? = bannerView

    fun destroyAd() {
        Logger.d(TAG, "Destroying banner ad")
        bannerView?.destroy()
        bannerView = null
        adLoadState = AdLoadState.NONE
        
        onAdLoaded = null
        onAdFailedToLoad = null
        onAdRefresh = null
        onAdFailedToRefresh = null
        onAdResize = null
        onAdClick = null
    }

    fun stopRefresh() {
        bannerView?.stopAutoRefresh()
    }

    fun startRefresh() {
        bannerView?.startAutoRefresh()
    }

    private fun prepareRequestOptions(): RequestOptions? {
        return try {
            null
        } catch (e: Exception) {
            Logger.e(TAG, "Failed to create RequestOptions", e)
            null
        }
    }

    private val bannerViewListener = object : BannerViewListener {
        override fun onAdLoad(preferredHeightDP: Int) {
            Logger.d(TAG, "Banner ad loaded: ${preferredHeightDP}dp")
            adLoadState = AdLoadState.LOADED
            onAdLoaded?.invoke(preferredHeightDP)
        }

        override fun onAdFailToLoad(exception: Exception) {
            adLoadState = AdLoadState.NONE
            Logger.e(TAG, "Failed to load banner ad", exception)
            onAdFailedToLoad?.invoke(exception)
        }

        override fun onAdRefresh() {
            Logger.d(TAG, "Banner ad refreshed")
            onAdRefresh?.invoke()
        }

        override fun onAdFailToRefresh(exception: Exception) {
            Logger.e(TAG, "Failed to refresh banner ad", exception)
            onAdFailedToRefresh?.invoke(exception)
        }

        override fun onResize(size: Size) {
            Logger.d(TAG, "Banner ad resized: ${size.width}x${size.height}dp")
            onAdResize?.invoke(size)
        }

        override fun onAdClick() {
            Logger.d(TAG, "Banner ad clicked")
            onAdClick?.invoke()
        }
    }
}
