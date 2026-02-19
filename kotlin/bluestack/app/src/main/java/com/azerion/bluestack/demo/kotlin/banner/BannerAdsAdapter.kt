package com.azerion.bluestack.demo.kotlin.banner

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import com.azerion.bluestack.demo.kotlin.DimensionUtils
import com.azerion.bluestack.demo.kotlin.R
import com.azerion.bluestack.demo.kotlin.hide
import com.azerion.bluestack.demo.kotlin.show

class BannerAdsAdapter(
    private val items: List<ContentItem>,
    private val bannerAdManager: BannerAdManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_BANNER_AD = 1
        private const val TAG = "BannerAdsAdapter"
    }

    // Filtered list that's actually rendered
    private var filteredItems: MutableList<ContentItem> = items.toMutableList()

    override fun getItemViewType(position: Int): Int {
        return when (filteredItems[position]) {
            is ContentItem.ImageItem -> VIEW_TYPE_IMAGE
            is ContentItem.BannerAdItem -> VIEW_TYPE_BANNER_AD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_content_image, parent, false)
                ImageViewHolder(view)
            }

            VIEW_TYPE_BANNER_AD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_content_ad_container, parent, false)
                BannerAdViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                val item = filteredItems[position] as ContentItem.ImageItem
                holder.bind(item)
            }

            is BannerAdViewHolder -> {
                holder.itemView.tag = position
                val item = filteredItems[position] as ContentItem.BannerAdItem
                Log.i(TAG, "onBindViewHolder: ad loading with position: $position")
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = filteredItems.size

    /**
     * Hide all banner ads by filtering them out from the display list.
     * Checks the manager's visibility state to prevent redundant updates.
     */
    fun hideAds() {
        // Check if already hidden via manager's state
        if (!bannerAdManager.isVisible()) return
        
        filteredItems = items.filterNot { it is ContentItem.BannerAdItem }.toMutableList()
        notifyDataSetChanged()
    }

    /**
     * Show all banner ads by restoring the full list.
     * Checks the manager's visibility state to prevent redundant updates.
     */
    fun showAds() {
        // Check if already visible via manager's state
        if (bannerAdManager.isVisible()) return
        
        filteredItems = items.toMutableList()
        notifyDataSetChanged()
    }

    /**
     * Get all banner ad items from the main list
     */
    fun getBannerAdItems(): List<ContentItem.BannerAdItem> {
        return items.filterIsInstance<ContentItem.BannerAdItem>()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: ContentItem.ImageItem) {
            imageView.setImageResource(item.imageRes)
            imageView.minimumHeight = DimensionUtils.convertDpToPixel(
                itemView.context,
                item.minHeight
            )
        }
    }

    /**
     * ViewHolder for banner ads.
     * Gets the actual BannerView from BannerAdManager using the bannerId.
     */
    inner class BannerAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ContentItem.BannerAdItem) {
            val adContainer = itemView as ViewGroup
            
            // Get the banner view from the manager
            val bannerView = bannerAdManager.getView(item.bannerId)
            
            if (bannerView == null) {
                Log.w(TAG, "Banner view not found for ID: ${item.bannerId}")
                adContainer.hide()
                return
            }

            // Add the banner ad to the ad view
            Log.i(TAG, "BannerAdViewHolder bind: ${bannerView.getAdState()}")

            // Make sure the BannerView for this position doesn't already have a parent
            // from a different recycled ViewHolder
            if (bannerView.parent != null) {
                (bannerView.parent as ViewGroup).removeView(bannerView)
            }

            // The ViewHolder recycled by RecyclerView may be a different
            // instance than the one used previously for this position. Clear the
            // ViewHolder of any subviews in case it has a different
            // BannerView associated with it
            adContainer.apply {
                if (isNotEmpty()) adContainer.removeAllViews()
                addView(bannerView)
            }

            // Set the preferred height if available and show the ad container
            item.preferredHeightDP?.let { heightDP ->
                adContainer.layoutParams?.height =
                    DimensionUtils.convertDpToPixel(itemView.context, heightDP)
                adContainer.requestLayout()
                adContainer.show()
            } ?: run {
                adContainer.hide()
            }
        }
    }
}
