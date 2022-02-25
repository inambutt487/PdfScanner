package me.sid.smartcropper.views.activities;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import me.sid.smartcropper.R;

public class SplashIconActivity extends BaseActivity {

    private Handler handler;
    private Runnable runnable;

    private ConstraintLayout splash;
    private ImageView splash_logo;
    private TextView tv0, tv1;

    Animation bottom_top, left_center, right_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseToken(SplashIconActivity.this);
        getNotification();

        resetFontScale(getResources().getConfiguration(), this);
        makeFullScreen();
        setStatusBar(this, R.color.white, R.color.white);
        setContentView(R.layout.activity_splash_icon);

        handler = new Handler();

        splash = findViewById(R.id.splash);
        bottom_top = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_to_original);
        splash .setAnimation(bottom_top);

    }

    private void getNotification() {

        if (getIntent().getExtras() != null) {
            String Title, Message, Icon, Action, Link, Rate, Image;
            Title = Message = Icon = Action = Link = Rate = Image = null;
            try {

                if (getIntent().getExtras().get("Action") != null && getIntent().getExtras().get("Link") != null) {

                    if (getIntent().getExtras().get("Title") != null) {
                        Title = getIntent().getExtras().get("Title").toString();
                        if (Title == null || Title.length() == 0) {
                            Title = null;
                        }
                    }

                    if (getIntent().getExtras().get("Message") != null) {
                        Message = getIntent().getExtras().get("Message").toString();
                        if (Message == null || Message.length() == 0) {
                            Message = null;
                        }
                    }

                    if (getIntent().getExtras().get("Icon") != null) {
                        Icon = getIntent().getExtras().get("Icon").toString();
                        if (Icon == null || Icon.length() == 0) {
                            Icon = null;
                        }
                    }

                    if (getIntent().getExtras().get("Action") != null) {
                        Action = getIntent().getExtras().get("Action").toString();
                        if (Action == null || Action.length() == 0) {
                            Action = null;
                        }
                    }

                    if (getIntent().getExtras().get("Link") != null) {
                        Link = getIntent().getExtras().get("Link").toString();
                        if (Link == null || Link.length() == 0) {
                            Link = null;
                        }
                    }

                    if (getIntent().getExtras().get("Rate") != null) {
                        Rate = getIntent().getExtras().get("Rate").toString();
                        if (Rate == null || Rate.length() == 0) {
                            Rate = null;
                        }
                    }


                    if (getIntent().getExtras().get("Image") != null) {
                        Image = getIntent().getExtras().get("Image").toString();
                        if (Image == null || Image.length() == 0) {
                            Image = null;
                        }
                    }

                    Log.d("SplashActivity", Title + Message + Icon + Action + Link + Rate);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();

                    bundle.putString(getString(R.string.messagetitle), Title);
                    bundle.putString(getString(R.string.messageBody), Message);
                    bundle.putString(getString(R.string.messageIcon), Icon);
                    bundle.putString(getString(R.string.messageAction), Action);
                    bundle.putString(getString(R.string.messageLink), Link);
                    bundle.putString(getString(R.string.messageRate), Rate);
                    bundle.putString(getString(R.string.messageImage), Image);


                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();

                }


            } catch (Exception e) {
                return;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashIconActivity.this, SplashActivity.class));
                finish();
            }
        };
        handler.postDelayed(runnable, 3000);

    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

}