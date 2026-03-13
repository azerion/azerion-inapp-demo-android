package com.azerion.bluestack.demo.kotlin

import android.app.Activity
import com.azerion.bluestack.appopen.AppOpenAd
import com.azerion.bluestack.appopen.AppOpenAdListener

/**
 * Manages the lifecycle of app open ads, including loading, showing, and automatic reloading.
 * This manager handles ad state tracking and ensures ads are only shown when available and ready.
 *
 * @property activityContextProvider Provider for obtaining the current activity context
 */
class AppOpenAdManager(private val activityContextProvider: ActivityContextProvider) :
    AppOpenAdListener {
    private var appOpenAd: AppOpenAd? = null
    private var onShowAdCompleteListener: OnShowAdCompleteListener? = null

    var isShowingAd = false

    fun initializeAppOpenAd() {
        if (appOpenAd == null) {
            appOpenAd = AppOpenAd(Constants.APP_OPEN_PLACEMENT_ID)
            appOpenAd?.setAppOpenAdListener(this)
        }
    }

    fun loadAd(activity: Activity) {
        initializeAppOpenAd()
        appOpenAd?.load(activity)
    }

    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(activity, object : OnShowAdCompleteListener {
            override fun onShowAdComplete() {
                // Empty because the user will go back to the activity that shows the ad.
            }
        })
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        initializeAppOpenAd()

        if (isShowingAd) {
            Logger.d(TAG, "The app open ad is already showing.")
            return
        }

        if (appOpenAd?.isReady() == false) {
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
            return
        }

        this.onShowAdCompleteListener = onShowAdCompleteListener
        isShowingAd = true
        appOpenAd?.show(activity)
    }


    override fun onAdFailedToLoad(exception: Exception) {
        Logger.e(TAG, "App open ad failed to load", exception)
    }

    override fun onAdLoaded() {
        Logger.i(TAG, "App open ad loaded")
    }

    override fun onAdClicked() {
        Logger.i(TAG, "App open ad clicked")
    }

    override fun onAdDismissed() {
        Logger.i(TAG, "App open ad dismissed")
        isShowingAd = false
        onShowAdCompleteListener?.onShowAdComplete()
        activityContextProvider.getActivity()?.let { loadAd(it) }
    }

    override fun onAdDisplayed() {
        Logger.i(TAG, "App open ad displayed")
    }

    override fun onAdFailedToDisplay(exception: Exception) {
        Logger.e(TAG, "App open ad failed to display", exception)
        isShowingAd = false
        onShowAdCompleteListener?.onShowAdComplete()
        activityContextProvider.getActivity()?.let { loadAd(it) }
    }

    companion object {
        private const val TAG = "AppOpenAdManager"
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    interface ActivityContextProvider {
        fun getActivity(): Activity?
    }
}