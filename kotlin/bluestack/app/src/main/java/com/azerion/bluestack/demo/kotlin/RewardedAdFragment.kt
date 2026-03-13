package com.azerion.bluestack.demo.kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.azerion.bluestack.demo.kotlin.databinding.FragmentRewardedAdBinding
import com.azerion.bluestack.rewarded.Reward
import com.azerion.bluestack.rewarded.RewardedAd
import com.azerion.bluestack.rewarded.RewardedAdListener

class RewardedAdFragment : Fragment(), RewardedAdListener {
    private val TAG = "RewardedAdFragment"
    private var _binding: FragmentRewardedAdBinding? = null
    private val binding get() = _binding!!

    private var rewardedAd: RewardedAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardedAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateViews()
        initiateAd()
    }

    private fun initiateViews() {
        binding.llActions.btnLoad.setOnClickListener {
            loadAd()
        }
        binding.llActions.btnShow.setOnClickListener {
            showAd()
        }
    }

    private fun initiateAd() {
        rewardedAd = RewardedAd(requireActivity(), Constants.REWARDED_VIDEO_PLACEMENT_ID).apply {
            setRewardedAdListener(this@RewardedAdFragment)
        }
    }

    private fun loadAd() {
        if (rewardedAd?.isReady() == true) {
            Logger.i(TAG, "Rewarded Ad is already ready to display")
            enableShowButton()
            return
        }

        rewardedAd?.load()
    }

    private fun showAd() {
        if (rewardedAd?.isReady() == true) {
            rewardedAd?.show()
        } else {
            Logger.d(TAG, "Rewarded Ad not ready to display")
        }
        enableShowButton()
    }

    private fun disableShowButton() {
        binding.llActions.btnShow.disable()
    }

    private fun enableShowButton() {
        binding.llActions.btnShow.enable()
    }

    override fun onDestroyView() {
        rewardedAd?.destroy()
        rewardedAd = null

        _binding = null

        super.onDestroyView()
    }

    override fun onAdFailedToLoad(exception: Exception) {
        Logger.e(TAG, "Failed to load rewarded ad", exception)
        disableShowButton()
    }

    override fun onAdLoaded() {
        Logger.i(TAG, "Rewarded loaded")
        enableShowButton()
    }

    override fun onEarnedReward(reward: Reward?) {
        Logger.i(TAG, "Earned reward: ${reward?.type} - ${reward?.amount}")
    }

    override fun onAdClicked() {
        Logger.i(TAG, "Rewarded ad clicked")
    }

    override fun onAdDismissed() {
        Logger.i(TAG, "Rewarded dismissed")
    }

    override fun onAdDisplayed() {
        Logger.i(TAG, "Rewarded displayed")
        disableShowButton()
    }

    override fun onAdFailedToDisplay(exception: Exception) {
        Logger.e(TAG, "Failed to display rewarded ad", exception)
    }
}