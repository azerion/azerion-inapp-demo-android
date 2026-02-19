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
import com.azerion.bluestack.demo.kotlin.databinding.FragmentBannerAdBinding
import com.azerion.bluestack.demo.kotlin.disable
import com.azerion.bluestack.demo.kotlin.enable
import com.azerion.bluestack.demo.kotlin.hide
import com.azerion.bluestack.demo.kotlin.show
import com.azerion.bluestack.util.Size

class BannerAdFragment : Fragment(), BannerAdListener {
    private val TAG = "BannerAdFragment"
    private var _binding: FragmentBannerAdBinding? = null
    private val binding get() = _binding!!

    private var bannerAdSize: AdSize = AdSize.BANNER

    private val listItems: MutableList<ContentItem> = ArrayList()
    private lateinit var adapter: BannerAdsAdapter
    
    // BannerAdManager to handle all banner ad lifecycle
    private var bannerAdManager: BannerAdManager? = null
    
    // Maximum number of banner ads to create (for performance/demo purposes)
    private val maxBannerAds = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val configName = it.getString(ARG_BANNER_CONFIG_NAME)
            bannerAdSize = configName?.let { name ->
                BannerAdSizeConfig.Companion.fromConfigName(name).adSize
            } ?: AdSize.BANNER
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBannerAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize BannerAdManager
        bannerAdManager = BannerAdManager(
            context = requireContext(),
            placementId = Constants.BANNER_PLACEMENT_ID,
            adSize = bannerAdSize,
            listener = this
        )

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Prepare list data with 60 content items and ads every 7 items
        prepareListData(totalItems = 60, itemsBetweenAds = 7)

        // Set up the adapter with manager reference
        adapter = BannerAdsAdapter(listItems, bannerAdManager!!)
        binding.recyclerView.adapter = adapter

        initViews()

        // Auto-load ads only for non-standard banners
        // Standard banners require manual load via the Load button
        if (!isStandardBanner() && bannerAdManager?.isIdle() == true) {
            bannerAdManager?.load()
        }
    }

    /**
     * Prepares the RecyclerView list data with banner ads inserted at regular intervals.
     *
     * @param totalItems Total number of content items to generate (excluding banner ads)
     * @param itemsBetweenAds Number of content items between consecutive banner ads
     */
    private fun prepareListData(totalItems: Int, itemsBetweenAds: Int) {
        // Add hero image
        listItems.add(ContentItem.ImageItem(R.drawable.content_hero, minHeight = 200))
        
        // Add content images
        repeat(totalItems) {
            listItems.add(ContentItem.ImageItem(R.drawable.content_left))
        }
        
        // Create banner ads and inject them at intervals
        addBannerAdsAtIntervals(itemsBetweenAds)
    }
    
    /**
     * Creates banner ads and injects them into the content list at regular intervals.
     * This implements dynamic banner ad insertion where banner instances are automatically
     * positioned after every m consecutive content items.
     *
     * @param itemsBetweenAds Number of content items between consecutive banner ads
     */
    private fun addBannerAdsAtIntervals(itemsBetweenAds: Int) {
        // Calculate how many banner ads we need based on list size
        // Formula: numberOfAds = totalListItems / (itemsBetweenAds + 1)
        val numberOfAds = ((listItems.size + itemsBetweenAds) / (itemsBetweenAds + 1)).coerceAtMost(maxBannerAds)
        
        Logger.d(TAG, "Creating $numberOfAds banner ads for ${listItems.size} items with $itemsBetweenAds items between ads")
        
        // Create all banner ads once through manager
        val bannerIds = bannerAdManager?.create(count = numberOfAds) ?: emptyList()
        
        // Inject banner ads at regular intervals in the list
        // Start after hero image (position 0) and first batch of content items
        var position = itemsBetweenAds + 1
        bannerIds.forEach { bannerId ->
            if (position <= listItems.size) {
                listItems.add(position, ContentItem.BannerAdItem(bannerId))
                Logger.d(TAG, "Inserted banner $bannerId at position $position")
                // Move to next position (accounting for the banner we just added)
                position += itemsBetweenAds + 1
            }
        }
        
        Logger.d(TAG, "Final list size: ${listItems.size} (${numberOfAds} banner ads)")
    }

    /**
     * Recreates banner ad instances and re-inserts them into existing list positions.
     * Used when removing and recreating banners without changing the list structure.
     */
    private fun recreateBannerItems() {
        // Create 10 fresh banner ads using BannerAdManager
        val bannerIds = bannerAdManager?.create(count = 10) ?: emptyList()
        
        // Calculate positions where banners should be inserted
        // Structure: hero (0), image (1), then pattern: banner, 6 images, banner, 6 images...
        var bannerIndex = 0
        bannerIds.forEach { bannerId ->
            // Position = 2 (after hero and first image) + 7 * bannerIndex (banner + 6 images per iteration)
            val insertPosition = 2 + (bannerIndex * 7)
            if (insertPosition <= listItems.size) {
                listItems.add(insertPosition, ContentItem.BannerAdItem(bannerId))
                bannerIndex++
            }
        }
    }

    // Determine if this is a standard banner size (320x50)
    private fun isStandardBanner(): Boolean {
        return bannerAdSize == AdSize.BANNER
    }

    /**
     * This is for the demo purpose only
     *
     * Toggles the visibility of action button groups based on the banner ad size.
     *
     * Standard banners (320x50) use a different set of action buttons compared to
     * other banner sizes (FULL_BANNER, LARGE_BANNER, LEADERBOARD).
     */
    private fun setButtonsVisibility() {
        if (isStandardBanner()) {
            // Standard banner: Show standard actions (Load, Hide, Show, Remove, Toggle)
            binding.llStandardActions.show()
            binding.llActions.root.hide()
        } else {
            // Non-standard banners: Show simplified actions (Hide/Show only)
            binding.llStandardActions.hide()
            binding.llActions.root.show()
        }
    }

    private fun initViews() {
        setButtonsVisibility()

        binding.llActions.btnLoad.text = getString(R.string.hide_banner)
        binding.llActions.btnShow.text = getString(R.string.show_banner)

        // Hide/Show banner actions using manager
        binding.llActions.btnLoad.setOnClickListener {
            adapter.hideAds()
            bannerAdManager?.hide()
        }
        binding.llActions.btnShow.setOnClickListener {
            adapter.showAds()
            bannerAdManager?.show()
        }

        initializeStandardBannerComponents()
        updateButtonStates()
    }

    // Standard banner actions
    private fun initializeStandardBannerComponents() {
        binding.btnLoad.setOnClickListener {
            if (bannerAdManager?.isLoading() == true) return@setOnClickListener

            // Reload banner ads (recreates instances if they were destroyed via Remove)
            bannerAdManager?.createAndLoad()
            adapter.showAds()
        }

        binding.btnHide.setOnClickListener {
            adapter.hideAds()
            bannerAdManager?.hide()
        }

        binding.btnShow.setOnClickListener {
            adapter.showAds()
            bannerAdManager?.show()
        }

        binding.btnRemove.setOnClickListener {
            // Destroy all banner ads
            bannerAdManager?.destroy()
            
            // Clear old banner items from the list
            listItems.removeAll { it is ContentItem.BannerAdItem }
            
            // Recreate fresh banner placeholders at correct positions
            recreateBannerItems()
            
            // Hide ads from view and update adapter
            adapter.hideAds()
        }

        binding.btnToggle.setOnClickListener {
            val currentAutoRefresh = bannerAdManager?.isAutoRefreshEnabled() ?: true
            bannerAdManager?.toggleAutoRefresh(!currentAutoRefresh)
        }
    }

    // This method is solely for demo purpose
    private fun updateButtonStates() {
        val manager = bannerAdManager ?: return

        when {
            manager.isIdle() -> updateButtonsForIdleState()
            manager.isLoading() -> updateButtonsForLoadingState()
            manager.isLoaded() -> updateButtonsForLoadedState(manager.isVisible())
        }
    }

    // This method is solely for demo purpose
    private fun updateButtonsForIdleState() {
        if (isStandardBanner()) {
            binding.btnLoad.enable()
            binding.btnHide.disable()
            binding.btnShow.disable()
            binding.btnRemove.disable()
            binding.btnToggle.disable()
        } else {
            binding.llActions.btnLoad.disable()
            binding.llActions.btnShow.disable()
        }
    }

    // This method is solely for demo purpose
    private fun updateButtonsForLoadingState() {
        if (isStandardBanner()) {
            binding.btnLoad.disable()
            binding.btnHide.disable()
            binding.btnShow.disable()
            binding.btnRemove.disable()
            binding.btnToggle.disable()
        } else {
            binding.llActions.btnLoad.disable()
            binding.llActions.btnShow.disable()
        }
    }

    // This method is solely for demo purpose
    private fun updateButtonsForLoadedState(visible: Boolean) {
        if (isStandardBanner()) {
            binding.btnLoad.disable()

            binding.btnToggle.apply {
                enable()
            }

            if (visible) {
                binding.btnShow.disable()
                binding.btnHide.enable()
                binding.btnRemove.enable()
            } else {
                binding.btnShow.enable()
                binding.btnHide.disable()
                binding.btnRemove.disable()
            }
        } else {
            if (visible) {
                binding.llActions.btnLoad.enable()
                binding.llActions.btnShow.disable()
            } else {
                binding.llActions.btnLoad.disable()
                binding.llActions.btnShow.enable()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause all banner ads via manager
        bannerAdManager?.pause()
    }

    override fun onResume() {
        super.onResume()
        // Resume all banner ads via manager
        bannerAdManager?.resume()
    }

    override fun onDestroyView() {
        // Destroy all banner ads via manager
        bannerAdManager?.destroy()
        bannerAdManager = null

        // Clear list items for clean recreation
        listItems.clear()

        _binding = null
        super.onDestroyView()
    }

    // BannerAdListener implementation
    override fun onBannerLoaded(bannerId: String, preferredHeightDP: Int) {
        Logger.d(TAG, "Banner ad loaded: $bannerId with preferred height of ${preferredHeightDP}dp")

        updateBannerHeight(bannerId, preferredHeightDP)
    }

    override fun onBannerFailedToLoad(bannerId: String, exception: Exception) {
        Logger.e(TAG, "Failed to load banner ad: $bannerId", exception)
    }

    override fun onBannerFailedToRefresh(bannerId: String, exception: Exception) {
        Logger.e(TAG, "Failed to refresh banner ad: $bannerId", exception)
    }

    override fun onBannerRefreshed(bannerId: String) {
        Logger.d(TAG, "Banner ad refreshed: $bannerId")
    }

    override fun onBannerResized(bannerId: String, size: Size) {
        Logger.d(TAG, "Banner ad resized: $bannerId with height of ${size.height}dp")

        updateBannerHeight(bannerId, size.height)
    }

    override fun onBannerClicked(bannerId: String) {
        Logger.d(TAG, "Banner ad clicked: $bannerId")
    }

    override fun onStateChanged(bannerListState: BannerListState) {
        Logger.d(TAG, "Banner ad state changed to: $bannerListState")
        // Update button states when state changes
        updateButtonStates()
    }

    /**
     * Helper method to update banner height and notify adapter
     */
    private fun updateBannerHeight(bannerId: String, heightDP: Int) {
        // Find the banner item in the list and update its height
        val bannerItemIndex = listItems.indexOfFirst {
            it is ContentItem.BannerAdItem && it.bannerId == bannerId
        }

        if (bannerItemIndex != -1) {
            val bannerItem = listItems[bannerItemIndex] as ContentItem.BannerAdItem
            bannerItem.preferredHeightDP = heightDP

            // Notify adapter to rebind this item with the new height
            binding.recyclerView.adapter?.notifyItemChanged(bannerItemIndex)
        }
    }

    companion object {
        private const val ARG_BANNER_CONFIG_NAME = "banner_config_name"

        /**
         * Creates a new instance of BannerAdFragment with the specified banner configuration.
         * Uses our own BannerAdSizeConfig enum instead of relying on external AdSize properties.
         */
        fun newInstance(config: BannerAdSizeConfig): BannerAdFragment {
            return BannerAdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_BANNER_CONFIG_NAME, config.name)
                }
            }
        }
    }
}
