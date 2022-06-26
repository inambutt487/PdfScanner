package me.sid.smartcropper.views.activities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.FeedbackDialog;
import me.sid.smartcropper.dialogs.PermissionDialog;
import me.sid.smartcropper.utils.PermissionUtils;
import me.sid.smartcropper.utils.StringUtils;

public class MainActivity extends BaseActivity implements PermissionDialog.PermissionGranted {

    ImageView menuImg, premiumImg, header_image, buy_btn;
    LinearLayout btn_scan_pdf, btn_cast_tv, btn_read_pdf;
    ConstraintLayout btn_translate;


    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;

    float ratingg = 0;
    ConstraintLayout header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header);
        if(havePurchase()){
            header.setVisibility(View.GONE);
        }

        showNotification();

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);


        if (nvDrawer.getHeaderCount() > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            View headerLayout = nvDrawer.getHeaderView(0);

            ConstraintLayout rootLayout1 = headerLayout.findViewById(R.id.rootLayout1);
            rootLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!havePurchase()){
                        if(!haveNetworkConnection()){
                            StringUtils.getInstance().showSnackbar(MainActivity.this, "No Internet Connection!");
                        }else{
                            startActivity(new Intent(MainActivity.this, PremiumActivity.class));
                        }
                    }else{
                        StringUtils.getInstance().showSnackbar(MainActivity.this, "Already Purchase!");
                    }
                    mDrawer.closeDrawers();

                }
            });
            ConstraintLayout rootLayout2 = headerLayout.findViewById(R.id.rootLayout2);
            rootLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!hasPermissionGranted(MainActivity.this)) {
                        requestRequiredPermissions();
                    } else {
                        startActivity(new Intent(MainActivity.this, TranslationActivity.class));
                    }

                    mDrawer.closeDrawers();
                }
            });
            ConstraintLayout rootLayout3 = headerLayout.findViewById(R.id.rootLayout3);
            rootLayout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!hasPermissionGranted(MainActivity.this)) {
                        requestRequiredPermissions();
                    } else {
                        startActivity(new Intent(MainActivity.this, CastActivity.class));
                    }

                    mDrawer.closeDrawers();
                }
            });

            //
            ConstraintLayout rootLayout5 = headerLayout.findViewById(R.id.rootLayout5);
            rootLayout5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FeedbackDialog dialog = new FeedbackDialog(MainActivity.this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    mDrawer.closeDrawers();

                    mDrawer.closeDrawers();
                }
            });

            ConstraintLayout rootLayout6 = headerLayout.findViewById(R.id.rootLayout6);
            rootLayout6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(TrashActivity.class, null);
                    mDrawer.closeDrawers();
                }
            });

            ConstraintLayout rootLayout7 = headerLayout.findViewById(R.id.rootLayout7);
            rootLayout7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                    mDrawer.closeDrawers();
                }
            });

            ConstraintLayout rootLayout8 = headerLayout.findViewById(R.id.rootLayout8);
            rootLayout8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateDialog();
                    mDrawer.closeDrawers();
                }
            });

            ConstraintLayout rootLayout9 = headerLayout.findViewById(R.id.rootLayout9);
            rootLayout9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ShareActivity.class).putExtra("share", 1));
                    mDrawer.closeDrawers();
                }
            });

            ConstraintLayout rootLayout10 = headerLayout.findViewById(R.id.rootLayout10);
            rootLayout10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf")));
                    mDrawer.closeDrawers();
                }
            });

            ConstraintLayout rootLayout11 = headerLayout.findViewById(R.id.rootLayout11);
            rootLayout11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.document.scanner.fast.scan.pdf.create.pdf.editor")));
                    mDrawer.closeDrawers();
                }
            });

            //
            ConstraintLayout layoutExit = headerLayout.findViewById(R.id.Exit);
            layoutExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawer.closeDrawers();
                    quitApp(MainActivity.this);
                }
            });

        }


        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("PDF SCANNER");

        premiumImg = findViewById(R.id.premiumImg);
        premiumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!havePurchase()){
                    if(!haveNetworkConnection()){
                        StringUtils.getInstance().showSnackbar(MainActivity.this, "No Internet Connection!");
                    }else{
                        startActivity(new Intent(MainActivity.this, PremiumActivity.class));
                    }
                }else{
                    StringUtils.getInstance().showSnackbar(MainActivity.this, "Already Purchase!");
                }
            }
        });

        header_image = findViewById(R.id.header_image);
        header_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!havePurchase()){
                    if(!haveNetworkConnection()){
                        StringUtils.getInstance().showSnackbar(MainActivity.this, "No Internet Connection!");
                    }else{
                        startActivity(new Intent(MainActivity.this, PremiumActivity.class));
                    }
                }else{
                    StringUtils.getInstance().showSnackbar(MainActivity.this, "Already Purchase!");
                }
            }
        });


        menuImg = findViewById(R.id.menuImg);
        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });


        buy_btn = findViewById(R.id.buy_btn);
        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!havePurchase()){
                    if(!haveNetworkConnection()){
                        StringUtils.getInstance().showSnackbar(MainActivity.this, "No Internet Connection!");
                    }else{
                        startActivity(new Intent(MainActivity.this, PremiumActivity.class));
                    }
                }else{
                    StringUtils.getInstance().showSnackbar(MainActivity.this, "Already Purchase!");
                }
            }
        });


        btn_scan_pdf = findViewById(R.id.btn_scan_pdf);
        btn_scan_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermissionGranted(MainActivity.this)) {
                    requestRequiredPermissions();
                } else {
                    startActivity(new Intent(MainActivity.this, GernalCameraActivity.class));
                }


            }
        });

        btn_cast_tv = findViewById(R.id.btn_cast_tv);
        btn_cast_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermissionGranted(MainActivity.this)) {
                    requestRequiredPermissions();
                } else {
                    startActivity(new Intent(MainActivity.this, CastActivity.class));
                }


            }
        });

        btn_read_pdf = findViewById(R.id.btn_read_pdf);
        btn_read_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermissionGranted(MainActivity.this)) {
                    requestRequiredPermissions();
                } else {
                    startActivity(new Intent(MainActivity.this, DocumentsActivity.class));
                }

            }
        });

        btn_translate = findViewById(R.id.btn_translate);
        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermissionGranted(MainActivity.this)) {
                    requestRequiredPermissions();
                } else {
                    startActivity(new Intent(MainActivity.this, TranslationActivity.class));
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(havePurchase()){
            header.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            //drawer is open
            mDrawer.closeDrawers();
        } else {
            quitApp(this);
        }

    }

    private void showNotification() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            RemoteMessage msg = (RemoteMessage) extras.get("msg");

            if (msg == null) {

                if (extras.getString(getString(R.string.messageAction)) != null && extras.getString(getString(R.string.messageLink)) != null && extras.getString(getString(R.string.messageRate)) != null) {

                    String messagetitle, messageBody, messageIcon, messageAction, messageLink, messageRate, messageImage;
                    messagetitle = messageBody = messageIcon = messageAction = messageLink = messageRate = messageImage = null;

                    if (extras.getString(getString(R.string.messagetitle)) != null) {
                        messagetitle = extras.getString(getString(R.string.messagetitle));
                    }

                    if (extras.getString(getString(R.string.messageBody)) != null) {
                        messageBody = extras.getString(getString(R.string.messageBody));
                    }

                    if (extras.getString(getString(R.string.messageIcon)) != null) {
                        messageIcon = extras.getString(getString(R.string.messageIcon));
                    }

                    if (extras.getString(getString(R.string.messageAction)) != null) {
                        messageAction = extras.getString(getString(R.string.messageAction));
                    }

                    if (extras.getString(getString(R.string.messageLink)) != null) {
                        messageLink = extras.getString(getString(R.string.messageLink));
                    }

                    if (extras.getString(getString(R.string.messageRate)) != null) {
                        messageRate = extras.getString(getString(R.string.messageRate));
                    }

                    if (extras.getString(getString(R.string.messageImage)) != null) {
                        messageRate = extras.getString(getString(R.string.messageImage));
                    }

                    notificationShow(messagetitle, messageBody, messageIcon, messageAction, messageLink, messageRate, messageRate);


                }


            } else {

                if (msg.getData().get("Action") != null && msg.getData().get("Link") != null && msg.getData().get("Rate") != null) {

                    String dialogMessage, dialogTitle, dialogIcon, dialogAction, dialogLink, dialogRate, dialogImage;

                    dialogMessage = msg.getData().get("Message");
                    if (dialogMessage == null || dialogMessage.length() == 0) {
                        dialogMessage = "";
                    }

                    dialogTitle = msg.getData().get("Title");
                    if (dialogTitle == null || dialogTitle.length() == 0) {
                        dialogTitle = "";
                    }

                    dialogIcon = msg.getData().get("Icon");
                    if (dialogIcon == null || dialogIcon.length() == 0) {
                        dialogIcon = "";
                    }

                    dialogAction = msg.getData().get("Action");
                    if (dialogAction == null || dialogAction.length() == 0) {
                        dialogAction = "";
                    }

                    dialogLink = msg.getData().get("Link");
                    if (dialogLink == null || dialogLink.length() == 0) {
                        dialogLink = "";
                    }

                    dialogRate = msg.getData().get("Rate");
                    if (dialogRate == null || dialogRate.length() == 0) {
                        dialogRate = "";
                    }


                    dialogImage = msg.getData().get("Image");
                    if (dialogImage == null || dialogImage.length() == 0) {
                        dialogImage = "";
                    }

                    notificationShow(dialogTitle, dialogMessage, dialogIcon, dialogAction, dialogLink, dialogRate, dialogImage);
                }

            }


        }


    }
    private void notificationShow(String Title, String Message, String Image, String Action, String Link, String Rate, String FeatureImage) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.notification_show);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (Title != null) {
            TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
            title.setText(Title);
        }

        if (Rate != null) {

            try {
                int i = Integer.parseInt(Rate);
                RatingBar rate = (RatingBar) dialog.findViewById(R.id.dialog_Rate);
                rate.setRating(Integer.valueOf(Rate));
            } catch (NumberFormatException ex) { // handle your exception
            }
        }

        if (Title != null) {
            TextView desc = (TextView) dialog.findViewById(R.id.dialog_desc);
            desc.setText(Message);
        }

        if (Image != null) {

            ImageView notificationIcon = (ImageView) dialog.findViewById(R.id.notification_icon);
            new AsyncTask<String, Integer, Drawable>() {

                @Override
                protected Drawable doInBackground(String... strings) {
                    Bitmap bmp = null;
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(Image).openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        bmp = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return new BitmapDrawable(bmp);
                }

                protected void onPostExecute(Drawable result) {

                    //Add image to ImageView

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notificationIcon.setImageDrawable(result);
                        }
                    });


                }


            }.execute();


            notificationIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            notificationIcon.setVisibility(View.VISIBLE);

        }


        if (FeatureImage != null) {

            ImageView dialog_image = (ImageView) dialog.findViewById(R.id.dialog_image);
            new AsyncTask<String, Integer, Drawable>() {

                @Override
                protected Drawable doInBackground(String... strings) {
                    Bitmap bmp = null;
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(FeatureImage).openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        bmp = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return new BitmapDrawable(bmp);
                }

                protected void onPostExecute(Drawable result) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog_image.setImageDrawable(result);
                        }
                    });


                }


            }.execute();


            dialog_image.setScaleType(ImageView.ScaleType.FIT_XY);
            dialog_image.setVisibility(View.VISIBLE);

        }

        Button dialog_go_to_link = (Button) dialog.findViewById(R.id.dialog_go_to_link);
        if (Action != null && Link != null && URLUtil.isValidUrl(Link)) {

            dialog_go_to_link.setText(Action);
            dialog_go_to_link.setVisibility(View.VISIBLE);
            dialog_go_to_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Link));
                    startActivity(browserIntent);
                    dialog.dismiss();


                }
            });


        }

        final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    public void requestRequiredPermissions() {

        PermissionDialog dialog = new PermissionDialog(this, this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

    }


    @Override
    public void granted() {
        PermissionUtils.checkAndRequestPermissions(
                this,
                new String[0],
                101
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.menuImg:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }


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
                } else if (ratingg == 0) {
                    Toast.makeText(MainActivity.this, "Kindly provide feedback!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Thank you for you feedback!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

}