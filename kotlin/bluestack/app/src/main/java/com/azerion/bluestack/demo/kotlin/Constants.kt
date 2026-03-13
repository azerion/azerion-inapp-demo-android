package com.azerion.bluestack.demo.kotlin

/**
 * You can put your BlueStack App ID and placements here
 */
object Constants {
    const val APP_ID = "3167505"
    const val BANNER_PLACEMENT_ID = "/${APP_ID}/banner"
    const val MREC_PLACEMENT_ID = "/${APP_ID}/mrec"
    const val INTERSTITIAL_PLACEMENT_ID = "/${APP_ID}/interstitial"
    const val REWARDED_VIDEO_PLACEMENT_ID = "/${APP_ID}/rewarded"
    const val APP_OPEN_PLACEMENT_ID = "/${APP_ID}/appOpen"

    // Splash screen timer duration in milliseconds - simulates app loading time
    const val SPLASH_COUNTER_TIME_MILLISECONDS = 5000L

    // Dummy CMP data
    const val IABTCF_TCString = "IABTCF_TCString"
    const val IABTCF_PurposeConsents = "IABTCF_PurposeConsents"
    const val IABTCF_SpecialFeaturesOptIns = "IABTCF_SpecialFeaturesOptIns"
    const val IABTCF_PublisherRestrictions1 = "IABTCF_PublisherRestrictions1"
    const val IABTCF_PublisherRestrictions2 = "IABTCF_PublisherRestrictions2"
    const val IABTCF_GDPRAPPLIES = "IABTCF_gdprApplies"
}