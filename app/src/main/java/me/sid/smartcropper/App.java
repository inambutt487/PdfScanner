package me.sid.smartcropper;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import me.sid.smartcropper.utils.SharePrefData;
import me.sid.smartcropper.utils.Specker.ApplicationEx;
import me.sid.smartcropper.views.activities.SplashIconActivity;

public class App extends ApplicationEx {

    private FirebaseAnalytics mFirebaseAnalytics;
    Activity mCurrentActivity;
    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        INSTANCE = this;
        SharePrefData.getInstance().setContext(getApplicationContext());

        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }


    }

    public static synchronized App getInstance() {
        return INSTANCE;
    }

    public void setmCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public Activity getmCurrentActivity() {
        return mCurrentActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel(){

        String channelId  = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);
        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW));

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Toast.makeText(this, "Config Changes", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SplashIconActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void resetFontScale(Configuration configuration, Context context) {
        configuration.fontScale = 1f;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(AppCompatActivity.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        context.getResources().updateConfiguration(configuration, metrics);
        Log.d("APP", "Configurations updated");

    }
}
