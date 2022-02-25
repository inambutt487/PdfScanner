package me.sid.smartcropper_testing.NativeAd

import android.content.Context
import android.util.Log

import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdListener
import me.sid.smartcropper_testing.R


open class FacebookNativeAd {
    companion object{
        var  nativeAd: NativeAd? = null

        var adStatus:String?="No Request"

        fun loadNativeFacebookAd(context:Context) {
           adStatus="loading"
            nativeAd = NativeAd(context,context.getString(R.string.fb_native))
            val nativeAdListener: NativeAdListener = object : NativeAdListener {
                override fun onMediaDownloaded(ad: Ad) {
                    // Native ad finished downloading all assets

                }
                override fun onError(ad: Ad?, adError: AdError) {
                    Log.d("d","d")
                    adStatus ="failed"

                }

                override fun onAdLoaded(ad: Ad) {

                    if (nativeAd == null || nativeAd != ad) {
                        return
                    }
                    adStatus ="loaded"

                    // Inflate Native Ad into Container

                }
                override fun onAdClicked(ad: Ad) {
                    // Native ad clicked

                }
                override fun onLoggingImpression(ad: Ad) {
                    // Native ad impression
                    //   Toast.makeText(context, "fb id1 impression", Toast.LENGTH_SHORT).show()
                }
            }
            // Request an ad
            nativeAd!!.loadAd(
                    nativeAd!!.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build())
        }


    }
}