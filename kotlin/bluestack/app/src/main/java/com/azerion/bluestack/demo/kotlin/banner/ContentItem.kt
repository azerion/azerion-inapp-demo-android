package com.azerion.bluestack.demo.kotlin.banner

import androidx.annotation.DrawableRes

sealed class ContentItem {
    data class ImageItem(
        @param:DrawableRes val imageRes: Int,
        val minHeight: Int = 150
    ) : ContentItem()

    data class BannerAdItem(
        val bannerId: String,
        var preferredHeightDP: Int? = null
    ) : ContentItem()
}