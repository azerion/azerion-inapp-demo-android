package com.azerion.bluestack.demo.kotlin.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.azerion.bluestack.demo.kotlin.databinding.FragmentBannerAdTabBinding
import com.google.android.material.tabs.TabLayoutMediator

class BannerAdTabFragment : Fragment() {
    private val TAG = "BannerAdTabFragment"
    private var _binding: FragmentBannerAdTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: BannerAdTabPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBannerAdTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        // Initialize the adapter
        pagerAdapter = BannerAdTabPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = pagerAdapter.getTabTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}