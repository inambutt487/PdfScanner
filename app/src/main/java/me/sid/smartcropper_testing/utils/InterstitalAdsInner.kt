package me.sid.smartcropper_testing.utils

import android.content.Context
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import me.sid.smartcropper_testing.App
import me.sid.smartcropper_testing.R
import me.sid.smartcropper_testing.interfaces.OnAdCloseInterface


public class InterstitalAdsInner {
    public var onAdCloseInterface: OnAdCloseInterface? = null

    companion object {
        public var mInterstitialAd: InterstitialAd? = null
        public var splashInterstita: InterstitialAd? = null
        public var interstitialAd_fb: com.facebook.ads.InterstitialAd? = null


        fun loadInterstitialAd(context: Context) {

            if (mInterstitialAd == null) {

                Log.d("mInterstit", "Already loaded")

                mInterstitialAd = InterstitialAd(context)
                mInterstitialAd!!.adUnitId = App.INSTANCE.resources.getString(R.string.admob_interstitial)


            }

            if (!SharePrefData.getInstance().adS_PREFS) {

                if (mInterstitialAd!!.isLoaded) {
                    Log.d("mInterstit", "Already loaded")

                } else {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                        Log.d("mInterstit", " newReq")
                    }
                }

            }


        }


        fun loadInterstitialAd_fb(context: Context): com.facebook.ads.InterstitialAd {

            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                }

                override fun onInterstitialDismissed(ad: Ad) {
                }

                override fun onError(ad: Ad, adError: AdError) {
                    Log.d("cheeckfb", "error"
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    Log.d("cheeckfb", "loaded"
                    )
                }

                override fun onAdClicked(ad: Ad) {
                }

                override fun onLoggingImpression(ad: Ad) {
                }
            }

            if (interstitialAd_fb == null) {

                interstitialAd_fb = com.facebook.ads.InterstitialAd(
                    context,
                    context.getString(R.string.fb_interstitial)
                )

                if (!SharePrefData.getInstance().adS_PREFS) {
                    interstitialAd_fb?.loadAd(
                        interstitialAd_fb?.buildLoadAdConfig()
                            ?.withAdListener(interstitialAdListener)
                            ?.build());
                }

            } else {
                if (interstitialAd_fb!!.isAdLoaded) {

                } else {
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb?.loadAd(
                            interstitialAd_fb?.buildLoadAdConfig()
                                ?.withAdListener(interstitialAdListener)
                                ?.build());
                    }
                }
            }

            return interstitialAd_fb as com.facebook.ads.InterstitialAd
        }

    }

    fun adMobShowOnlyCallBack(onAdCloseInterface: OnAdCloseInterface, mediation: String) {
        this.onAdCloseInterface = onAdCloseInterface
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded && !SharePrefData.getInstance().adS_PREFS) {
            if (!SharePrefData.getInstance().adS_PREFS) {
                mInterstitialAd!!.show()
                mInterstitialAd!!.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        onAdCloseInterface.onAdClose()
                        if (!SharePrefData.getInstance().adS_PREFS) {
                            mInterstitialAd!!.loadAd(AdRequest.Builder().build())
                        }

                    }

                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        onAdCloseInterface.onAdClose()
                        if (mediation.equals(RemoteKeysPdf.AM_P)) {
                            fbShowOnlyCallBack(onAdCloseInterface, mediation)
                        } else {
                            onAdCloseInterface.onAdClose()
                        }
                    }
                }
            }


        } else {
//            onAdCloseInterface.onAdClose()
            if (mediation.equals(RemoteKeysPdf.AM_P)) {
                fbShowOnlyCallBack(onAdCloseInterface, mediation)
            } else {
                onAdCloseInterface.onAdClose()
            }
        }
    }


    fun fbShowOnlyCallBack(onAdCloseInterface: OnAdCloseInterface, mediation: String) {
        if (!SharePrefData.getInstance().adS_PREFS && interstitialAd_fb != null && interstitialAd_fb!!.isAdLoaded && !interstitialAd_fb!!.isAdInvalidated) {
            interstitialAd_fb!!.show()

            var interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad) {
                    // Interstitial ad displayed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad displayed.")
                }

                override fun onInterstitialDismissed(ad: Ad) {


                    onAdCloseInterface.onAdClose()
                    if (!SharePrefData.getInstance().adS_PREFS) {
                        interstitialAd_fb!!.loadAd()
                    }


                    // Interstitial dismissed callback
                    Log.e("FacebookAdsLog1", "Interstitial ad dismissed.")
                }

                override fun onError(ad: Ad, adError: AdError) {
//                    onAdCloseInterface.onAdClose()
                    if (mediation.equals(RemoteKeysPdf.FB_P)) {
                        adMobShowOnlyCallBack(onAdCloseInterface, mediation)
                    } else {
                        onAdCloseInterface.onAdClose()
                    }
                    // Ad error callback
                    Log.e(
                        "FacebookAdsLog1",
                        "Interstitial ad failed to load: " + adError.getErrorMessage()
                    )
                }

                override fun onAdLoaded(ad: Ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d("FacebookAdsLog1", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad

                }

                override fun onAdClicked(ad: Ad) {
                    // Ad clicked callback
                    Log.d("FacebookAdsLog1", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad) {
                    // Ad impression logged callback
                    Log.d("FacebookAdsLog1", "Interstitial ad impression logged!")
                }
            }

            interstitialAd_fb?.buildLoadAdConfig()?.withAdListener(interstitialAdListener)


        } else {
//            onAdCloseInterface.onAdClose()
            if (mediation.equals(RemoteKeysPdf.FB_P)) {
                adMobShowOnlyCallBack(onAdCloseInterface, mediation)
            } else {
                onAdCloseInterface.onAdClose()
            }
        }

    }

}
