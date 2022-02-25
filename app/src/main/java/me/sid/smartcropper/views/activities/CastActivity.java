package me.sid.smartcropper.views.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.StringUtils;

public class CastActivity extends BaseActivity {

    private ImageView backimg, premiumImg;
    private Button btn_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Cast PDF");

        backimg = findViewById(R.id.backimg);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(!havePurchase()) {
            premiumImg = findViewById(R.id.premiumImg);
            premiumImg.setVisibility(View.VISIBLE);
            premiumImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!havePurchase()) {
                        if (!haveNetworkConnection()) {
                            StringUtils.getInstance().showSnackbar(CastActivity.this, "No Internet Connection!");
                        } else {
                            startActivity(new Intent(CastActivity.this, PremiumActivity.class));
                        }
                    } else {
                        StringUtils.getInstance().showSnackbar(CastActivity.this, "Already Purchase!");
                    }


                }
            });
        }

        btn_confirm = findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    try {
                        startActivity(new Intent("com.samsung.wfd.LAUNCH_WFD_PICKER_DLG"));
                    } catch (Exception e2) {
                        try {
                            startActivity(new Intent("android.settings.CAST_SETTINGS"));
                        } catch (Exception e3) {
                            Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
        });

    }
}