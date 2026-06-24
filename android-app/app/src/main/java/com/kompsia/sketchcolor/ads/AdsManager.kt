package com.kompsia.sketchcolor.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kompsia.sketchcolor.R

object AdsManager {
    fun initialize(context: Context) {
        MobileAds.initialize(context) {}
    }

    fun createBanner(context: Context): AdView {
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = context.getString(R.string.admob_banner_unit_id)
            loadAd(AdRequest.Builder().build())
        }
    }
}
