package com.azerion.bluestack.demo.kotlin.banner

import com.azerion.bluestack.util.Size

/**
 * Interface for communication between BannerAdManager and its client (e.g., BannerAdFragment).
 * Each callback includes a bannerId to identify which specific banner triggered the event.
 */
interface BannerAdListener {
    
    /**
     * Called when a banner ad successfully loads.
     * 
     * @param bannerId Unique identifier for the banner that loaded
     * @param preferredHeightDP Preferred height of the banner in DP
     */
    fun onBannerLoaded(bannerId: String, preferredHeightDP: Int)
    
    /**
     * Called when a banner ad fails to load.
     * 
     * @param bannerId Unique identifier for the banner that failed to load
     * @param exception The exception that caused the failure
     */
    fun onBannerFailedToLoad(bannerId: String, exception: Exception)
    
    /**
     * Called when a banner ad fails to refresh.
     * 
     * @param bannerId Unique identifier for the banner that failed to refresh
     * @param exception The exception that caused the failure
     */
    fun onBannerFailedToRefresh(bannerId: String, exception: Exception)
    
    /**
     * Called when a banner ad successfully refreshes.
     * 
     * @param bannerId Unique identifier for the banner that refreshed
     */
    fun onBannerRefreshed(bannerId: String)
    
    /**
     * Called when a banner ad is resized.
     * 
     * @param bannerId Unique identifier for the banner that was resized
     * @param size New size of the banner
     */
    fun onBannerResized(bannerId: String, size: Size)
    
    /**
     * Called when a banner ad is clicked.
     * 
     * @param bannerId Unique identifier for the banner that was clicked
     */
    fun onBannerClicked(bannerId: String)
    
    /**
     * Called when the overall state of the banner ad manager changes.
     * 
     * @param bannerListState New state of the banner ad manager
     */
    fun onStateChanged(bannerListState: BannerListState)
}
