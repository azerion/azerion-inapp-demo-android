package com.azerion.bluestack.demo.kotlin

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.azerion.bluestack.MobileAds

/**
 * Custom Application class that manages app-wide app open ad lifecycle.
 * Integrates with Android's lifecycle callbacks to automatically show ads when the app
 * comes to the foreground and tracks the current foreground activity.
 */
class MyApplication : Application(), Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver,
    AppOpenAdManager.ActivityContextProvider {
    private val appOpenAdManager = AppOpenAdManager(this)
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super<Application>.onCreate()
        registerActivityLifecycleCallbacks(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // Only update currentActivity if we're not showing an ad.
        // This prevents referencing the activity that displays the app open ad.
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (MobileAds.isInitialized()) {
            currentActivity?.let { activity ->
                // Don't show app open ad on launcher/splash activities
                // as they manage their own app open ad flow
                val shouldShowAd = activity !is CustomLauncherActivity &&
                        activity !is SplashScreenActivity

                if (shouldShowAd) {
                    appOpenAdManager.showAdIfAvailable(activity)
                }
            }
        }
    }

    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: AppOpenAdManager.OnShowAdCompleteListener
    ) {
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    fun loadAd(activity: Activity) {
        appOpenAdManager.loadAd(activity)
    }

    override fun getActivity() = currentActivity
}