package com.azerion.bluestack.demo.kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.azerion.bluestack.BlueStack
import com.azerion.bluestack.demo.kotlin.databinding.ActivitySplashBinding
import com.azerion.bluestack.initialization.InitializationListener
import com.azerion.bluestack.initialization.InitializationStatus

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