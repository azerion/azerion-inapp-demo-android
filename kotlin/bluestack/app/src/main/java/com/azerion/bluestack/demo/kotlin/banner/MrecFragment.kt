package com.azerion.bluestack.demo.kotlin.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.azerion.bluestack.banner.AdSize
import com.azerion.bluestack.demo.kotlin.Constants
import com.azerion.bluestack.demo.kotlin.Logger
import com.azerion.bluestack.demo.kotlin.R
import com.azerion.bluestack.demo.kotlin.databinding.FragmentMrecBinding
import com.azerion.bluestack.demo.kotlin.disable
import com.azerion.bluestack.demo.kotlin.enable
import com.azerion.bluestack.util.Size

class MrecFragment : Fragment(), BannerAdListener {
    private val TAG = "MrecFragment"
    private var _binding: FragmentMrecBinding? = null
    private val binding get() = _binding!!
    private val contentItems: MutableList<ContentItem> = ArrayList()
    private lateinit var adapter: BannerAdsAdapter
    private var bannerAdManager: BannerAdManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMrecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize banner ad manager
        if (bannerAdManager == null) {
            bannerAdManager = BannerAdManager(
                context = requireContext(),
                placementId = Constants.MREC_PLACEMENT_ID,
                adSize = AdSize.MEDIUM_RECTANGLE,
                listener = this
            )
        }

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Prepare dummy list data (or recreate if returning to fragment)
        if (contentItems.isEmpty()) {
            prepareListData()
        }

        // Set up the adapter
        adapter = BannerAdsAdapter(contentItems, bannerAdManager!!)
        binding.recyclerView.adapter = adapter

        initiateViews()

        // Auto-load ads on first creation
        if (bannerAdManager?.isIdle() == true) {
            bannerAdManager?.load()
        }
    }

    private fun prepareListData() {
        // Add image items for ui demonstration
        contentItems.add(ContentItem.ImageItem(R.drawable.content_hero, minHeight = 200))
        contentItems.add(ContentItem.ImageItem(R.drawable.content_left))

        // Create 4 banner ads through manager
        val bannerIds = bannerAdManager?.create(4) ?: emptyList()

        // Add banner ad items
        bannerIds.forEachIndexed { index, bannerId ->
            // Add it to the content items list at position 2 (after 2 images)
            contentItems.add(ContentItem.BannerAdItem(bannerId))

            // Add remaining images after the ad
            repeat(3) {
                contentItems.add(ContentItem.ImageItem(R.drawable.content_left))
            }
        }
    }

    private fun updateBannerHeight(bannerId: String, heightDP: Int, itemIndex: Int) {
        val bannerItem = contentItems[itemIndex] as? ContentItem.BannerAdItem
        bannerItem?.preferredHeightDP = heightDP
        binding.recyclerView.adapter?.notifyItemChanged(itemIndex)
    }

    // BannerAdListener implementations
    override fun onBannerLoaded(bannerId: String, preferredHeightDP: Int) {
        Logger.d(TAG, "MREC loaded with preferred height of ${preferredHeightDP}dp")
        
        // Find the item index for this banner
        val itemIndex = contentItems.indexOfFirst { 
            it is ContentItem.BannerAdItem && it.bannerId == bannerId 
        }
        if (itemIndex != -1) {
            updateBannerHeight(bannerId, preferredHeightDP, itemIndex)
        }
    }

    override fun onBannerFailedToLoad(bannerId: String, exception: Exception) {
        Logger.e(TAG, "Failed to load MREC ad: $bannerId", exception)
    }

    override fun onBannerResized(bannerId: String, size: Size) {
        Logger.d(TAG, "MREC resized with height of ${size.height}dp")
        
        // Find the item index for this banner
        val itemIndex = contentItems.indexOfFirst { 
            it is ContentItem.BannerAdItem && it.bannerId == bannerId 
        }
        if (itemIndex != -1) {
            updateBannerHeight(bannerId, size.height, itemIndex)
        }
    }

    override fun onBannerClicked(bannerId: String) {
        Logger.d(TAG, "MREC ad clicked")
    }

    override fun onBannerRefreshed(bannerId: String) {
        Logger.d(TAG, "MREC refreshed")
    }

    override fun onBannerFailedToRefresh(bannerId: String, exception: Exception) {
        Logger.e(TAG, "Failed to refresh MREC ad", exception)
    }

    override fun onStateChanged(bannerListState: BannerListState) {
        Logger.d(TAG, "State changed to: $bannerListState")
        // Update button states when state changes
        updateButtonStates()
    }

    private fun initiateViews() {
        binding.llActions.btnLoad.text = getString(R.string.hide_mrec)
        binding.llActions.btnShow.text = getString(R.string.show_mrec)

        // Hide/Show functionality using manager and adapter
        binding.llActions.btnLoad.setOnClickListener {
            adapter.hideAds()
            bannerAdManager?.hide()
        }
        binding.llActions.btnShow.setOnClickListener {
            adapter.showAds()
            bannerAdManager?.show()
        }

        updateButtonStates()
    }

    // This method is solely for demo purpose
    private fun updateButtonStates() {
        val manager = bannerAdManager ?: return

        when {
            manager.isIdle() -> {
                binding.llActions.btnLoad.disable()
                binding.llActions.btnShow.disable()
            }

            manager.isLoading() -> {
                binding.llActions.btnLoad.disable()
                binding.llActions.btnShow.disable()
            }

            manager.isLoaded() -> {
                if (manager.isVisible()) {
                    binding.llActions.btnLoad.enable()
                    binding.llActions.btnShow.disable()
                } else {
                    binding.llActions.btnLoad.disable()
                    binding.llActions.btnShow.enable()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause all banner ads
        bannerAdManager?.pause()
    }

    override fun onResume() {
        super.onResume()
        // Resume all banner ads
        bannerAdManager?.resume()
    }

    override fun onDestroyView() {
        // Destroy all banner ads
        bannerAdManager?.destroy()

        _binding = null
        super.onDestroyView()
    }
}
