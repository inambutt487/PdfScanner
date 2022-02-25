package me.sid.smartcropper_testing.NativeAd


import android.content.Context

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import me.sid.smartcropper_testing.R


open class AdmobNativeAd {
    companion object {
        var  nativeAd: UnifiedNativeAd? = null
        var adStatus:String?="No Request"


        fun loadAdAdmobNativeAd(context: Context) {
           adStatus="loading"
            val builder = AdLoader.Builder(context, context.getString(R.string.admob_native))
                    .forUnifiedNativeAd { unifiedNativeAd ->
                        if (nativeAd != null) {
                            nativeAd?.destroy()
                        }
                        nativeAd = unifiedNativeAd

                    }
            builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: Int) {
                    super.onAdFailedToLoad(p0)
                    adStatus="failed"



                }

                override fun onAdClosed() {
                    super.onAdClosed()

                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adStatus="loaded"



                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    // Toast.makeText(context, "Admob id1 impression", Toast.LENGTH_SHORT).show()
                }
            })
            var adLoader: AdLoader = builder.build()
            adLoader.loadAd(AdRequest.Builder().build())
        }


    }
}