package me.sid.smartcropper.views.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_share.*
import me.sid.smartcropper.BuildConfig
import me.sid.smartcropper.R
import me.sid.smartcropper.utils.Constants
import me.sid.smartcropper.utils.StringUtils
import org.jetbrains.anko.toast
import java.util.*

class ShareActivity : BaseActivity() {

    var paused = false
    var value: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val intent = intent
        if (intent != null) {

            value = getIntent().getIntExtra("share", 0)
            if(value == 0){

                shareImg.setOnClickListener {

                    if (Constants.shareFile != null) {
                        Constants.shareFile(this, Constants.shareFile)
                    } else {
                        toast("Please Select File")
                    }

                }

                instaImg.setOnClickListener {

                    if (Constants.shareFile != null) {
                        Constants.shareFile(this, Constants.shareFile, "com.skype.raider")
                    } else {
                        toast("Please Select File")
                    }


                }

                gmailImg.setOnClickListener {

                    if (Constants.shareFile != null) {
                        Constants.shareFile(this, Constants.shareFile, "com.google.android.gm")
                    } else {
                        toast("Please Select File")
                    }

                }

            }else{

                shareImg.setOnClickListener {

                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        var shareMessage = "\nLet me recommend you this application\n\n"
                        shareMessage =
                            """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: Exception) {
                    }

                }

                instaImg.setOnClickListener {

                    try {

                        var shareMessage = "\nLet me recommend you this application\n\n"
                        shareMessage =
                            """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                        shareSkype(this@ShareActivity, shareMessage);
                    } catch (e: Exception) {
                    }


                }

                gmailImg.setOnClickListener {

                    try {

                        var shareMessage = "\nLet me recommend you this application\n\n"
                        shareMessage =
                            """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                        
                        val emailIntent = Intent(
                            Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "", null
                            )
                        )
                        emailIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(emailIntent, "Send Email..."))


                    } catch (e: Exception) {
                    }

                }

            }

        }

        backImg.setOnClickListener {
            finish()
        }

        if(!havePurchase()) {
            premiumImg.visibility = View.VISIBLE
            premiumImg.setOnClickListener {

                if (!havePurchase()) {
                    if (!haveNetworkConnection()) {
                        StringUtils.getInstance()
                            .showSnackbar(this, "No Internet Connection!")
                    } else {
                        startActivity(Intent(this, PremiumActivity::class.java))
                    }
                } else {
                    StringUtils.getInstance().showSnackbar(this, "Already Purchase!")
                }

            }
        }

        if(!havePurchase()) {
            pdfImg.visibility = View.GONE
            pdfImg.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf")
                    )
                )
            }
        }

        /*if (RemoteKeysPdf.SHARE_SCANNER_NATIVE.contains(AM) && !SharePrefData.getInstance().adS_PREFS) {
            loadAdmobNativeAd()
        } else if (RemoteKeysPdf.SHARE_SCANNER_NATIVE.contains(FB) && !SharePrefData.getInstance().adS_PREFS) {
            loadNativeAd()
        } else {
            admobNativeView.visibility = View.GONE
            native_ad_container.setVisibility(View.GONE)
            adlayout.visibility = View.GONE
            adlayout2.visibility = View.GONE
        }*/

    }


    override fun onResume() {
        super.onResume()
        /*if (paused) {
            val ads = InterstitalAdsInner()
            if (RemoteKeysPdf.SHARE_SCANNER_INTER.contains(AM)) {
                ads.adMobShowOnlyCallBack(
                    OnAdCloseInterface { paused = false },
                    RemoteKeysPdf.SHARE_SCANNER_INTER
                )
            } else if (RemoteKeysPdf.SHARE_SCANNER_INTER.contains(FB)) {
                ads.fbShowOnlyCallBack(
                    OnAdCloseInterface { paused = false },
                    RemoteKeysPdf.SHARE_SCANNER_INTER
                )
            } else {
                paused = false
            }
        }*/
    }

    fun shareSkype(context: Context, message: String) {
        val intent = context.packageManager.getLaunchIntentForPackage("com.skype.raider")
        intent!!.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        context.startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

}