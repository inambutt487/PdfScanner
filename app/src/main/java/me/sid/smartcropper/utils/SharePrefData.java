package me.sid.smartcropper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefData {
    private static final SharePrefData instance = new SharePrefData();
    private static final String INTRO_DONE = "intro_done";
    private static final String ADS_PREFS = "adprefs";

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public static synchronized SharePrefData getInstance() {
        return instance;
    }
    private Context context;
    public void setContext(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }

    public SharePrefData(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }

    private SharePrefData() {
    }


    public void setIntroScrenVisibility(boolean intro) {

        spEditor = sp.edit();
        spEditor.putBoolean(INTRO_DONE, intro);
        spEditor.apply();
        spEditor.commit();
    }

    public boolean getIntroScreenVisibility() {


        return sp.getBoolean(INTRO_DONE, false);
    }


    public Boolean getADS_PREFS() {
        return sp.getBoolean(ADS_PREFS, false);
    }

    public void setADS_PREFS(Boolean ADS_PREFS_) {
        spEditor = sp.edit();
        spEditor.putBoolean(ADS_PREFS, ADS_PREFS_);
        spEditor.apply();
        spEditor.commit();
    }


    @SuppressLint("CommitPrefEdits")
    public boolean destroyUserSession() {
        this.spEditor = this.sp.edit();
//        this.spEditor.remove(LOGGED_IN);
        this.spEditor.apply();
        return true;
    }



}
