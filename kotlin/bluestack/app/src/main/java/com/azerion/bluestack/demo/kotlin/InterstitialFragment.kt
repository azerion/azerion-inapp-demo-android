package com.azerion.bluestack.demo.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.azerion.bluestack.demo.kotlin.databinding.FragmentInterstitialBinding
import com.azerion.bluestack.interstitial.InterstitialAd
import com.azerion.bluestack.interstitial.InterstitialAdListener

class InterstitialFragment : Fragment(), InterstitialAdListener {
    private val TAG = "InterstitialFragment"
    private var _binding: FragmentInterstitialBinding? = null
    private val binding get() = _binding!!
    private var interstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterstitialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateAd()

        binding.llActions.btnLoad.setOnClickListener {
            loadAd()
        }

        binding.llActions.btnShow.setOnClickListener {
            showAd()
        }
    }

    private fun initiateAd() {
        interstitialAd =
            InterstitialAd(requireActivity(), Constants.INTERSTITIAL_PLACEMENT_ID).apply {
                setInterstitialAdListener(this@InterstitialFragment)
            }
    }

    private fun loadAd() {
        if (interstitialAd?.isReady() == true) {
            Logger.i(TAG, "Interstitial ad is already ready to display")
            enableShowButton()
            return
        }

        interstitialAd?.load()
    }

    private fun showAd() {
        if (interstitialAd?.isReady() == true) {
            interstitialAd?.show()
        } else {
            Logger.d(TAG, "Interstitial ad not ready to display")
        }
        disableShowButton()
    }

    private fun disableShowButton() {
        binding.llActions.btnShow.disable()
    }

    private fun enableShowButton() {
        binding.llActions.btnShow.enable()
    }

    override fun onDestroyView() {
        interstitialAd?.destroy()
        interstitialAd = null

        _binding = null

        super.onDestroyView()
    }

    override fun onAdFailedToLoad(exception: Exception) {
        Logger.e(TAG, "Failed to load interstitial ad", exception)
        disableShowButton()
    }

    override fun onAdLoaded() {
        Logger.i(TAG, "Interstitial loaded")
        enableShowButton()
    }

    override fun onAdClicked() {
        Logger.i(TAG, "Interstitial ad clicked")
    }

    override fun onAdDismissed() {
        Logger.i(TAG, "Interstitial dismissed")
    }

    override fun onAdDisplayed() {
        Logger.i(TAG, "Interstitial displayed")
        disableShowButton()
    }

    override fun onAdFailedToDisplay(exception: Exception) {
        Logger.e(TAG, "Failed to display interstitial ad", exception)
    }
}
