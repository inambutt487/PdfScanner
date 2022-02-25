package me.sid.smartcropper_testing.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import me.sid.smartcropper_testing.BuildConfig;
import me.sid.smartcropper_testing.R;

public class RemoteConfigSetup {
    FirebaseRemoteConfig remoteConfig;
   boolean tempStatus=false;
    public boolean setUpRemoteConfig(Context context) {

        remoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();

        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.setDefaultsAsync(R.xml.remoteapp);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if (task.isSuccessful()&& !BuildConfig.DEBUG) {  //&& !BuildConfig.DEBUG
                    Constants.remoteStatus="success";
                    tempStatus=true;
                    remoteConfig.activate();
                    FirebaseRemoteConfig.getInstance().activate();
                    remoteConfig.activate();


                    RemoteKeysPdf.INSTANCE.setADMOB_NATIVE(remoteConfig.getString("scanner_admob_native"));
                    RemoteKeysPdf.INSTANCE.setADMOB_INTER(remoteConfig.getString("scanner_admob_inter"));
                    RemoteKeysPdf.INSTANCE.setFB_NATIVE(remoteConfig.getString("scanner_fb_native"));
                    RemoteKeysPdf.INSTANCE.setFB_INTER(remoteConfig.getString("scanner_fb_inter"));

                    RemoteKeysPdf.INSTANCE.setSPLASH_SCANNER_NATIVE(remoteConfig.getString("scanner_native_splash"));
                    RemoteKeysPdf.INSTANCE.setSPLASH_SCANNER_INTER(remoteConfig.getString("scanner_splash_inter"));
                    RemoteKeysPdf.INSTANCE.setCAPTURE_SCANNER_INTER(remoteConfig.getString("scanner_capture_inter"));
                    RemoteKeysPdf.INSTANCE.setRETAKE_SCANNER_INTER(remoteConfig.getString("scanner_retake_inter"));
                    RemoteKeysPdf.INSTANCE.setOCR_SCANNER_INTER(remoteConfig.getString("scanner_ocr_inter"));
                    RemoteKeysPdf.INSTANCE.setDELETE_SCANNER_INTER(remoteConfig.getString("scanner_delete_inter"));
                    RemoteKeysPdf.INSTANCE.setSAVE_SCANNER_INTER(remoteConfig.getString("scanner_save_inter"));
                    RemoteKeysPdf.INSTANCE.setOPENFILE_SCANNER_INTER(remoteConfig.getString("scanner_openfile_inter"));
                    RemoteKeysPdf.INSTANCE.setQUITFILE_SCANNER_INTER(remoteConfig.getString("scanner_quitfile_inter"));
                    RemoteKeysPdf.INSTANCE.setTRASHFILE_SCANNER_INTER(remoteConfig.getString("scanner_trashfile_inter"));
                    RemoteKeysPdf.INSTANCE.setRESTOREFILE_SCANNER_INTER(remoteConfig.getString("scanner_restorefile_inter"));
                    RemoteKeysPdf.INSTANCE.setPREMIUM_SCANNER_INTER(remoteConfig.getString("scanner_premium_inter"));
                    RemoteKeysPdf.INSTANCE.setSHARE_SCANNER_INTER(remoteConfig.getString("scanner_share_inter"));
                    RemoteKeysPdf.INSTANCE.setTHREECLICK_SCANNER_INTER(remoteConfig.getString("scanner_threeclick_inter"));
                    RemoteKeysPdf.INSTANCE.setOCR_SCANNER_NATIVE(remoteConfig.getString("scanner_ocr_native"));
                    RemoteKeysPdf.INSTANCE.setOCR_SAVE_SCANNER_NATIVE(remoteConfig.getString("scanner_ocr_save_native"));
                    RemoteKeysPdf.INSTANCE.setSETTING_SCANNER_NATIVE(remoteConfig.getString("scanner_setting_native"));
                    RemoteKeysPdf.INSTANCE.setRENAME_SCANNER_NATIVE(remoteConfig.getString("scanner_rename_native"));
                    RemoteKeysPdf.INSTANCE.setQUIT_SCANNER_NATIVE(remoteConfig.getString("scanner_quit_native"));
                    RemoteKeysPdf.INSTANCE.setSHARE_SCANNER_NATIVE(remoteConfig.getString("scanner_share_native"));
                    RemoteKeysPdf.INSTANCE.setDASHBOARD_SCANNER_NATIVE(remoteConfig.getString("scanner_dash_native"));

                    InterstitalAdsInner.Companion.loadInterstitialAd(context);

                } else {

                    RemoteKeysPdf.INSTANCE.setADMOB_NATIVE(context.getString(R.string.admob_native));
                    RemoteKeysPdf.INSTANCE.setADMOB_INTER(context.getString(R.string.admob_interstitial));
                    RemoteKeysPdf.INSTANCE.setFB_NATIVE(context.getString(R.string.fb_native));
                    RemoteKeysPdf.INSTANCE.setFB_INTER(context.getString(R.string.fb_interstitial));

                    RemoteKeysPdf.INSTANCE.setSPLASH_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setSPLASH_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setCAPTURE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setRETAKE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getFB());
                    RemoteKeysPdf.INSTANCE.setOCR_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getFB());
                    RemoteKeysPdf.INSTANCE.setDELETE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setSAVE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setOPENFILE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setQUITFILE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setTRASHFILE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setRESTOREFILE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setPREMIUM_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setSHARE_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setTHREECLICK_SCANNER_INTER(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setOCR_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setOCR_SAVE_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getFB_P());
                    RemoteKeysPdf.INSTANCE.setSETTING_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getAM());
                    RemoteKeysPdf.INSTANCE.setRENAME_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getFB_P());
                    RemoteKeysPdf.INSTANCE.setQUIT_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getFB_P());
                    RemoteKeysPdf.INSTANCE.setSHARE_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getFB_P());
                    RemoteKeysPdf.INSTANCE.setDASHBOARD_SCANNER_NATIVE(RemoteKeysPdf.INSTANCE.getFB_P());

                    Constants.remoteStatus="failure";
                    tempStatus=true;
                }

            }

        });
        return tempStatus;

    }
}
