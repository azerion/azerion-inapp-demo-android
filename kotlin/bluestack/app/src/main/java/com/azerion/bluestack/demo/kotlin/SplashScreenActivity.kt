package com.azerion.bluestack.demo.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.azerion.bluestack.BlueStack
import com.azerion.bluestack.initialization.InitializationListener
import com.azerion.bluestack.initialization.InitializationStatus

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
        BlueStack.initialize(this, Constants.APP_ID, object : InitializationListener {
            override fun onInitialized(status: InitializationStatus) {
                status.adapterStatusMap.forEach { (adNetworkName, adapterStatus) ->
                    Logger.d(
                        TAG,
                        "AdNetwork: $adNetworkName, " +
                                "Name: ${adapterStatus.name}, " +
                                "State: ${adapterStatus.state}, " +
                                "Description: ${adapterStatus.description}"
                    )
                }

                navigateToMainActivity()
            }
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}