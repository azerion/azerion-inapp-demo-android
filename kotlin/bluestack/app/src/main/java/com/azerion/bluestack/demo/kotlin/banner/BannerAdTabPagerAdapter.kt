package com.azerion.bluestack.demo.kotlin.banner

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerAdTabPagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val bannerConfigs = BannerAdSizeConfig.all()

    override fun getItemCount(): Int = bannerConfigs.size

    override fun createFragment(position: Int): Fragment {
        // Pass our own config enum instead of external AdSize
        return BannerAdFragment.newInstance(bannerConfigs[position])
    }

    fun getTabTitle(position: Int): String {
        return fragment.getString(bannerConfigs[position].labelResId)
    }
}