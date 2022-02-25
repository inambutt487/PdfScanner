package me.sid.smartcropper_testing.views.activities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

import me.sid.smartcropper_testing.R;
import me.sid.smartcropper_testing.adapters.SettingsAdapter;
import me.sid.smartcropper_testing.dialogs.FeedbackDialog;
import me.sid.smartcropper_testing.interfaces.GenericCallback;
import me.sid.smartcropper_testing.models.SettingsData;
import me.sid.smartcropper_testing.utils.Constants;
import me.sid.smartcropper_testing.utils.PermissionUtils;
import me.sid.smartcropper_testing.utils.RemoteKeysPdf;
import me.sid.smartcropper_testing.utils.SharePrefData;

public class SettingActivity extends BaseActivity implements View.OnClickListener, GenericCallback {

    ImageView setting_back_btn, setting_exit_btn;
    RecyclerView recyclerView_setting;
    SettingsAdapter settingsAdapter;
    ArrayList<SettingsData> settingsData = new ArrayList<>();

    FrameLayout admobNativeView;
    NativeAdLayout nativeAdContainer;
    ConstraintLayout adlayout;
    View adlayout2;

    private ConstraintLayout rootLayout1, rootLayout2, rootLayout3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {

        rootLayout1 = findViewById(R.id.rootLayout1);
        rootLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.hasPermissionGranted(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
                    startActivity(new Intent(SettingActivity.this, PremiumActivity.class));
                }else{
                    PermissionUtils.checkAndRequestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_EXTERNAL_STORAGE);
                }

            }
        });
        rootLayout2 = findViewById(R.id.rootLayout2);
        rootLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TranslatePDFActivity.class));
            }
        });
        rootLayout3 = findViewById(R.id.rootLayout3);
        rootLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, CastActivity.class));
            }
        });

        setting_back_btn = findViewById(R.id.setting_back_btn);
        setting_back_btn.setOnClickListener(this);
        setting_exit_btn = findViewById(R.id.setting_exit_btn);
        setting_exit_btn.setOnClickListener(this);

        recyclerView_setting = findViewById(R.id.recyclerView_setting);
        recyclerView_setting.setLayoutManager(new LinearLayoutManager(this));
        settingsData.add(new SettingsData(R.drawable.email_s, getString(R.string.contact_us), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.trash_s, getString(R.string.trash), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.terms_s, getString(R.string.policy), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.rate_s, getString(R.string.rate), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.share_s, getString(R.string.share_app), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.app_translated, getString(R.string.pdf_app), R.drawable.ic_arrow_s));
        settingsData.add(new SettingsData(R.drawable.app_pdf_reader, getString(R.string.doc_app), R.drawable.ic_arrow_s));

        settingsAdapter = new SettingsAdapter(settingsData, SettingActivity.this, this);
        recyclerView_setting.setAdapter(settingsAdapter);

        adlayout2 = findViewById(R.id.adlayout2);
        admobNativeView = findViewById(R.id.admobNativeView);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        adlayout = findViewById(R.id.adlayout);

        if (RemoteKeysPdf.INSTANCE.getSETTING_SCANNER_NATIVE().contains(RemoteKeysPdf.INSTANCE.getAM()) && !SharePrefData.getInstance().getADS_PREFS()) {
            loadAdmobNativeAd();
        } else if (RemoteKeysPdf.INSTANCE.getSETTING_SCANNER_NATIVE().contains(RemoteKeysPdf.INSTANCE.getFB())  && !SharePrefData.getInstance().getADS_PREFS()) {
            loadNativeAd();
        } else {
            admobNativeView.setVisibility(View.GONE);
            nativeAdContainer.setVisibility(View.GONE);
            adlayout.setVisibility(View.GONE);
            adlayout2.setVisibility(View.GONE);
        }
        if (!isConnected()) {
            adlayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setting_back_btn) {
            finish();
        } else if (view.getId() == R.id.setting_exit_btn) {
            quitApp(this);
        }
    }

    @Override
    public void callback(Object o) {

        final String whereTo = (String) o;

        if (whereTo.equals(getResources().getString(R.string.rate))) {
      /*      Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(SettingActivity.this, " unable to find market app", Toast.LENGTH_LONG).show();
            }*/
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            rateDialog();

        } else if (whereTo.equals(getResources().getString(R.string.share_app))) {
            startActivity(new Intent(this, ShareActivity.class));
        } else if (whereTo.equals(getResources().getString(R.string.trash))) {
            startActivity(TrashActivity.class, null);
        } else if (whereTo.equals(getString(R.string.policy))) {
            startActivity(new Intent(this, PrivacyActivity.class));
        } else if (whereTo.equals(getString(R.string.contact_us))) {

            FeedbackDialog dialog = new FeedbackDialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);


        } else if (whereTo.equals(getString(R.string.pdf_app))) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.language.translator.golingo.memriselingo.translation.translateall")));
        } else if (whereTo.equals(getString(R.string.doc_app))) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf")));
        }
    }

    float ratingg = 0;

    private void rateDialog() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.rate_dialog);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        RatingBar ratingBar = dialog.findViewById(R.id.rate);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingg = rating;
            }
        });
        dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingg > 4) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    dialog.dismiss();
                } else if(ratingg==0){
                    Toast.makeText(SettingActivity.this, "Kindly provide feedback!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SettingActivity.this, "Thank you for you feedback!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    NativeAd fbNativead;
    NativeAdLayout fbNativeAdlayout;
    ConstraintLayout fbAdview;

    private void loadNativeAd() {

        fbNativead = new NativeAd(this,RemoteKeysPdf.INSTANCE.getFB_NATIVE());

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {

//                binding.admobNativeView.setVisibility(View.VISIBLE);
                nativeAdContainer.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.GONE);
                adlayout2.setVisibility(View.GONE);

                if(RemoteKeysPdf.INSTANCE.getSETTING_SCANNER_NATIVE().equals(RemoteKeysPdf.INSTANCE.getFB_P())){
                    loadAdmobNativeAd();
                }
//                loadAdmobNativeAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (fbNativead == null || fbNativead != ad) {
                    return;
                }

                adlayout2.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.GONE);
                nativeAdContainer.setVisibility(View.VISIBLE);
                // Inflate Native Ad into Container
                inflateAd(fbNativead);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        fbNativead.loadAd(
                fbNativead.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }


    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        fbNativeAdlayout = findViewById(R.id.native_ad_container);
        fbAdview =
                (ConstraintLayout) getLayoutInflater().inflate(R.layout.loading_fb_native, fbNativeAdlayout, false);
        fbNativeAdlayout.addView(fbAdview);

        LinearLayout adChoicesContainer = fbAdview.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, fbNativeAdlayout);
        adOptionsView.setIconColor(Color.parseColor("#271337"));
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);


        com.facebook.ads.MediaView nativeAdIcon = fbAdview.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = fbAdview.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = fbAdview.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = fbAdview.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdSocialContext = fbAdview.findViewById(R.id.native_ad_social_context);
        Button nativeAdCallToAction = fbAdview.findViewById(R.id.native_ad_call_to_action);
        ConstraintLayout contentsFb = fbAdview.findViewById(R.id.contentfb);

        com.facebook.ads.MediaView nativeAdMedia = fbAdview.findViewById(R.id.native_ad_media);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        if (nativeAd.hasCallToAction()) {
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        } else {
            nativeAdCallToAction.setVisibility(View.INVISIBLE);
        }
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();

        clickableViews.add(nativeAdCallToAction);
        clickableViews.add(nativeAdMedia);
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdBody);
        clickableViews.add(nativeAdIcon);

        nativeAd.registerViewForInteraction(
                fbAdview,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews
        );


    }


    UnifiedNativeAd nativeAd;

    private void loadAdmobNativeAd() {


        AdLoader.Builder builder = new AdLoader.Builder(this, RemoteKeysPdf.INSTANCE.getADMOB_NATIVE());

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                if (nativeAd != null) {
                    nativeAd.destroy();
                }


                adlayout2.setVisibility(View.GONE);
                admobNativeView.setVisibility(View.VISIBLE);
                nativeAdContainer.setVisibility(View.GONE);
                nativeAd = unifiedNativeAd;
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.loading_admob_native, null);
                populateUnifiedNativeAdView(nativeAd, adView);
                admobNativeView.removeAllViews();
                admobNativeView.addView(adView);

            }


        });

        if(RemoteKeysPdf.INSTANCE.getSETTING_SCANNER_NATIVE().equals(RemoteKeysPdf.INSTANCE.getAM_P())){
            loadNativeAd();
        }


        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());


    }


    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        mediaView.setImageScaleType(ImageView.ScaleType.FIT_XY);
        adView.setMediaView(mediaView);
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        /* adView.setPriceView(adView.findViewById(R.id.ad_price))
         adView.setStarRatingView(adView.findViewById(R.id.ad_stars))
         adView.setStoreView(adView.findViewById(R.id.ad_store))
         adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser))*/
        // The headline is guaranteed to be in every UnifiedNativeAd.

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable()
            );
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

    }


}