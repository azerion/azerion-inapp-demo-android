package com.azerion.bluestack.demo.kotlin

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.azerion.bluestack.MobileAds
import com.azerion.bluestack.demo.kotlin.databinding.ActivitySplashBinding
import com.azerion.bluestack.initialization.InitializationListener
import com.azerion.bluestack.initialization.SDKInitializationStatus
import java.util.concurrent.TimeUnit

/**
 * SDK initialization using custom splash screen layout.
 * For system-integrated splash approach, see SplashScreenActivity.kt
 */
class CustomLauncherActivity : AppCompatActivity() {
    private val TAG = "CustomLauncherActivity"
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCMP()
    }

    private fun initCMP() {
        DummyCMPManager.show(this, object : DummyCMPManager.OnConsentProvidedListener {
            override fun consentProvided() {
                this@CustomLauncherActivity.runOnUiThread { initializeBlueStackSDK() }
            }

            override fun consentFailed() {
                this@CustomLauncherActivity.runOnUiThread { initializeBlueStackSDK() }
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
                this@CustomLauncherActivity.runOnUiThread {
                    (application as MyApplication).loadAd(this@CustomLauncherActivity)
                }
            }
        })

        // Create a timer so that the CustomLauncherActivity is displayed for a fixed duration,
        // giving the SDK enough time to load and show the app‑open ad.
        createTimer()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
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
                        "App is done loading in: ${
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
                        }"
                    )
                }

                override fun onFinish() {
                    (application as MyApplication).showAdIfAvailable(
                        this@CustomLauncherActivity,
                        object : AppOpenAdManager.OnShowAdCompleteListener {
                            override fun onShowAdComplete() {
                                navigateToMainActivity()
                            }
                        },
                    )
                }
            }
        countDownTimer.start()
    }
}