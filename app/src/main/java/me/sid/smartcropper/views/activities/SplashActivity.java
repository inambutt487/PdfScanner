package me.sid.smartcropper.views.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.GetFilesUtility;
import me.sid.smartcropper.utils.SharePrefData;

public class SplashActivity extends BaseActivity implements GetFilesUtility.getFilesCallback {

    private static final int PERMISSION_REQUEST_CODE = 2296;

    TextView tv1;
    String[] array = new String[3];
    int PHONE_STATE_CODE = 101;

    Button continueBtn;
    CheckBox checkBox;
    TextView privacyText, termstxt;
    String adStatus = "";
    ConstraintLayout ads;
    boolean isEnabled = false;

    LottieAnimationView splash;
    TextView loading;
    ConstraintLayout footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetFontScale(getResources().getConfiguration(), this);
        makeFullScreen();
        setStatusBar(this, R.color.white, R.color.white);

        setContentView(R.layout.activity_splash);

        splash = findViewById(R.id.splash_animation);
        loading = findViewById(R.id.loading);
        footer = findViewById(R.id.footer);


        initializeBilling(SplashActivity.this);

        array[0] = READ_EXTERNAL_STORAGE;
        array[1] = WRITE_EXTERNAL_STORAGE;
        array[2] = Manifest.permission.CAMERA;


        ads = findViewById(R.id.ads);

        startTimerFiveSecond();

        if (!isConnected()) {
            ads.setVisibility(View.GONE);
        }
        continueBtn = findViewById(R.id.continueBtn);
        checkBox = findViewById(R.id.checkboxnew);
        privacyText = findViewById(R.id.privacytext);
        termstxt = findViewById(R.id.termstext);

        if (SharePrefData.getInstance().getIntroScreenVisibility()) {
            checkBox.setChecked(true);
            isEnabled = true;
//            continueBtn.setEnabled(true);
            continueBtn.setBackground(getDrawable(R.drawable.button_seagreen));
        } else {
            isEnabled = false;
            checkBox.setChecked(false);
//            continueBtn.setEnabled(false);
            continueBtn.setBackground(getDrawable(R.drawable.button_lightgray));
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEnabled = true;
//                    continueBtn.setEnabled(true);
                    continueBtn.setBackground(getDrawable(R.drawable.button_seagreen));
                } else {
                    isEnabled = false;
//                    continueBtn.setEnabled(false);
                    continueBtn.setBackground(getDrawable(R.drawable.button_lightgray));
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    SharePrefData.getInstance().setIntroScrenVisibility(true);

                    Intent intent;


                    if(haveNetworkConnection() && !havePurchase()){
                        intent = new Intent(SplashActivity.this, PremiumActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("SplashActive", "Start free trail");
                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }

                    startActivity(intent);

                } else {
                    showToast("Please check our Privacy Policy in order to continue", SplashActivity.this);
                }
            }
        });

        privacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, PrivacyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        termstxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, TermsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        String text = "Read our Privacy Policy.";

        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)),
                8, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyText.setText(spannable, TextView.BufferType.SPANNABLE);

        setTerms();
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            int result3 = ContextCompat.checkSelfPermission(SplashActivity.this, CAMERA);
            if (Environment.isExternalStorageManager() && result3 == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else return false;
        } else {
            int result = ContextCompat.checkSelfPermission(SplashActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(SplashActivity.this, WRITE_EXTERNAL_STORAGE);
            int result3 = ContextCompat.checkSelfPermission(SplashActivity.this, CAMERA);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
        }
    }

    void setTerms() {

        String text = "Tap to Start to accept the Terms Of Service.";

        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)),
                27, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        termstxt.setText(spannable, TextView.BufferType.SPANNABLE);
    }





    private void goToCamera() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 23) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (!checkPermission()) {
                    requestPermission();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

/*
                Intent intent = new Intent(SplashActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //Displaying a toast
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();*/
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Permission Needed To Run The App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PHONE_STATE_CODE) {
            Map<String, Integer> perms = new HashMap<String, Integer>();

            perms.put(READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            if (perms.get(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                goToCamera();

            } else {
                // Permission Denied
                Toast.makeText(SplashActivity.this, "Permission Needed To Run The App", Toast.LENGTH_SHORT)
                        .show();

                finish();
            }

        }
    }

    CountDownTimer cTimer = null;

    //start timer function
    void startTimerFiveSecond() {
        cTimer = new CountDownTimer(7000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                footer.setVisibility(View.VISIBLE);
                splash.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                continueBtn.setVisibility(View.VISIBLE);
                cancelTimer();
            }
        };
        cTimer.start();
    }

    void startTimerTwoSecond() {
        cTimer = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                footer.setVisibility(View.VISIBLE);
                splash.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                continueBtn.setVisibility(View.VISIBLE);
                cancelTimer();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }
    @Override
    public void getFiles(ArrayList<File> arrayList) {

    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


}