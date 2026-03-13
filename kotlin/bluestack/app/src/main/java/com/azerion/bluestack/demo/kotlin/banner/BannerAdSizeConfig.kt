package com.azerion.bluestack.demo.kotlin.banner

import androidx.annotation.StringRes
import com.azerion.bluestack.banner.BannerAdSize
import com.azerion.bluestack.demo.kotlin.R

/**
 * Configuration class that encapsulates banner ad size information.
 *
 * This enum provides a centralized, type-safe way to manage banner ad sizes,
 * combining the AdSize enum with display labels and providing utility methods
 * for conversions.
 */
enum class BannerAdSizeConfig(
    val adSize: BannerAdSize,
    @param:StringRes val labelResId: Int
) {
    STANDARD(BannerAdSize.BANNER, R.string.standard),
    FULL(BannerAdSize.FULL_BANNER, R.string.full),
    LARGE(BannerAdSize.LARGE_BANNER, R.string.large),
    LEADER(BannerAdSize.LEADERBOARD, R.string.leader);

    companion object {
        /**
         * Returns all available banner ad size configurations.
         */
        fun all(): List<BannerAdSizeConfig> = entries

        /**
         * Converts a BannerAdSizeConfig enum name string to its corresponding instance.
         * This method uses our own enum names (STANDARD, FULL, LARGE, LEADER) instead of
         * relying on external AdSize enum properties.
         *
         * @param configName The string name of the BannerAdSizeConfig enum (e.g., "STANDARD", "FULL")
         * @return The corresponding BannerAdSizeConfig, defaulting to STANDARD if not found
         */
        fun fromConfigName(configName: String): BannerAdSizeConfig {
            return entries.find { it.name == configName } ?: STANDARD
        }
    }
}