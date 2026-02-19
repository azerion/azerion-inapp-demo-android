package com.azerion.bluestack.demo.kotlin;

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.azerion.bluestack.demo.kotlin.Constants.IABTCF_GDPRAPPLIES
import com.azerion.bluestack.demo.kotlin.Constants.IABTCF_PublisherRestrictions1
import com.azerion.bluestack.demo.kotlin.Constants.IABTCF_PublisherRestrictions2
import com.azerion.bluestack.demo.kotlin.Constants.IABTCF_PurposeConsents
import com.azerion.bluestack.demo.kotlin.Constants.IABTCF_SpecialFeaturesOptIns
import com.azerion.bluestack.demo.kotlin.Constants.IABTCF_TCString

object DummyCMPManager {
    interface OnConsentProvidedListener {
        fun consentProvided()
        fun consentFailed()
    }

    fun show(activity: Activity, onConsentProvidedListener: OnConsentProvidedListener) {
        if (getConsentString(activity)?.isNotEmpty() == true) {
            onConsentProvidedListener.consentProvided()
            return;
        }
        showCMPDialog(activity, onConsentProvidedListener)
    }

    fun openCMP(activity: Activity) {
        showCMPDialog(activity, null)
    }

    private fun showCMPDialog(
        activity: Activity, onConsentProvidedListener: OnConsentProvidedListener?
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.consent_manager).setMessage(R.string.consent_message)
            .setCancelable(true).setPositiveButton(
                R.string.yes
            ) { _, _ ->
                writeConsentString(
                    activity,
                    IABTCF_TCString,
                    "CP3CBVhP3CBVhBaIOBFRATEsAP_gAH_gAAqIg1NX_H__bX9v-Xr36ft0eY1f99j77sQxBhfJs-4FyLvW_JwX32EyNE26tqYKmRIEu3ZBIQFtHJnURVihaogVrzHsYkGcgTNKJ-BkgHMRe2dYCF5vmYtj-QKZ5_p_d3f52T_9_dv-3dzzz9Vnv3e9fudlcIida59tH_n_bRKb-7Ie9_7-_4v09N_rk2_eTVv_9evv71-u_t____9_9__-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAEQamr_j__tr-3_L179P26PMav--x992IYgwvk2fcC5F3rfk4L77CZGibdW1MFTIkCXbsgkIC2jkzqIqxQtUQK15j2MSDOQJmlE_AyQDmIvbOsBC83zMWx_IFM8_0_u7v87J_-_u3_bu555-qz37vev3OyuEROtc-2j_z_tolN_dkPe_9_f8X6em_1ybfvJq3_-vX396_Xf2____-_-___AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAACAA"
                )
                onConsentProvidedListener?.consentProvided()
            }.setNegativeButton(
                R.string.no
            ) { _, _ ->
                writeConsentString(
                    activity,
                    IABTCF_TCString,
                    "CP20-YAP20-YAAHABAENAeEgAAAAAAAAAAAAAAAAAAAA"
                )
                onConsentProvidedListener?.consentFailed()
            }
        writeConsentString(
            activity,
            IABTCF_PurposeConsents,
            "1111111111"
        )
        writeConsentString(
            activity,
            IABTCF_SpecialFeaturesOptIns,
            "11"
        )
        writeConsentString(
            activity,
            IABTCF_PublisherRestrictions1,
            "_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________1________________________________________________________________________________________________1_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________1"
        )
        writeConsentString(
            activity,
            IABTCF_PublisherRestrictions2,
            "_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________1________________________________________________________________________________________________1_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________1"
        )
        PreferenceManager.getDefaultSharedPreferences(activity).edit {
            putInt(IABTCF_GDPRAPPLIES, 1)
        }
        builder.setCancelable(false)
        builder.create().show()
    }

    private fun writeConsentString(context: Context, consentKey: String, consentValue: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(consentKey, consentValue)
        }
    }

    private fun getConsentString(
        context: Context
    ): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(IABTCF_TCString, "")
    }
}
