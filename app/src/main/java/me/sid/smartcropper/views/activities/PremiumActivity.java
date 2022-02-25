package me.sid.smartcropper.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;

import me.sid.smartcropper.R;

public class PremiumActivity extends BaseActivity {

    private ImageView close;
    TextView privacy_policy, terms_condition;
    private Button continueBtn;

    private Handler adHandler;
    private Runnable runnable;

    private Boolean status = false;
    private String buttonValue = "Continue";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeFullScreen();

        setContentView(R.layout.activity_premium);

        initializeBilling(PremiumActivity.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            buttonValue = extras.getString("SplashActive");
        }


        close = findViewById(R.id.close);
        close.setVisibility(View.GONE);

        privacy_policy = findViewById(R.id.privacy_policy);
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PremiumActivity.this, PrivacyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        terms_condition = findViewById(R.id.terms_condition);
        terms_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PremiumActivity.this, TermsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setText(buttonValue);


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SkuDetails skuDetails = getConsoleProductDeatils();
                if (skuDetails != null) {
                    tinyDB.putBoolean("isPurchased", true);
                    lunchPruchaseFlow(PremiumActivity.this);
                }


            }
        });


    }

    @Override
    protected void onResume() {

        adHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                close.setVisibility(View.VISIBLE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(PremiumActivity.this, MainActivity.class));
                        finish();
                    }
                });

            }
        };
        adHandler.postDelayed(runnable, 3000);

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (billingClient.getConnectionState() != 2) {
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                        // The BillingClient is ready. You can query purchases here.
                        showProducts(PremiumActivity.this);


                    }
                }

                @Override
                public void onBillingServiceDisconnected() {

                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    BillingConnection(PremiumActivity.this);

                }
            });
        }


    }

    @Override
    protected void onPause() {
        adHandler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();

    }

    @Override
    protected void onStop() {
        adHandler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if(status){
            startActivity(new Intent(PremiumActivity.this, MainActivity.class));
            finish();
        }

    }
}
