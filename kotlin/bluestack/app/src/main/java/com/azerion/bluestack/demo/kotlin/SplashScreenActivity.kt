package com.azerion.bluestack.demo.kotlin

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.azerion.bluestack.MobileAds
import com.azerion.bluestack.initialization.InitializationListener
import com.azerion.bluestack.initialization.SDKInitializationStatus
import java.util.concurrent.TimeUnit

/**
 * SDK initialization using Android's Splash Screen API (system-integrated splash).
 * For custom splash screen approach, see CustomLauncherActivity.kt
 */
class SplashScreenActivity : AppCompatActivity() {
    private val TAG = "SplashActivityAPI"

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.decorView.post {
            initCMP()
        }
    }

    private fun initCMP() {
        DummyCMPManager.show(this, object : DummyCMPManager.OnConsentProvidedListener {
            override fun consentProvided() {
                // User provided consent - proceed with SDK initialization
                // The SDK will use the consent status for personalized ads
                this@SplashScreenActivity.runOnUiThread { initializeBlueStackSDK() }
            }

            override fun consentFailed() {
                // Consent failed or was denied - still proceed with SDK initialization
                // The SDK can still serve non-personalized ads without user consent
                this@SplashScreenActivity.runOnUiThread { initializeBlueStackSDK() }
            }
        })
    }

    private fun initializeBlueStackSDK() {
        MobileAds.setDebugModeEnabled(true)
        MobileAds.initialize(this, Constants.APP_ID, object : InitializationListener {
            override fun onInitialized(status: SDKInitializationStatus) {
                status.mediationAdapterStatusMap.forEach { (adNetworkName, adapterStatus) ->
                    Logger.d(
                        TAG,
                        "AdNetwork: $adNetworkName, " +
                                "Name: ${adapterStatus.name}, " +
                                "State: ${adapterStatus.state}, " +
                                "Description: ${adapterStatus.description}"
                    )
                }

                this@SplashScreenActivity.runOnUiThread {
                    (application as MyApplication).loadAd(this@SplashScreenActivity)
                }
            }
        })

        // Create a timer so that the SplashScreenActivity is displayed for a fixed duration,
        // giving the SDK enough time to load and show the app‑open ad.
        createTimer()
    }

    /**
     Create the countdown timer, which counts down to zero and show the app open ad.
     */
    private fun createTimer() {
        val countDownTimer: CountDownTimer =
            object : CountDownTimer(Constants.SPLASH_COUNTER_TIME_MILLISECONDS, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.d(
                        TAG,
                        "Splash screen loading in: ${
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                        }"
                    )
                }

                override fun onFinish() {
                    showAppOpenAd()
                }
            }
        countDownTimer.start()
    }

    private fun showAppOpenAd() {
        (application as MyApplication).showAdIfAvailable(
            this@SplashScreenActivity,
            object : AppOpenAdManager.OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    navigateToMainActivity()
                }
            }
        )
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}