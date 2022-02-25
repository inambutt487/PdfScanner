package me.sid.smartcropper.views.activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.sid.smartcropper.BuildConfig;
import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.ExitDialog;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.TinyDB;

public class BaseActivity extends AppCompatActivity {

    public static String Token = null;

    ArrayList<FileInfoModel> temp_pdfArrayList = new ArrayList<>();
    ArrayList<FileInfoModel> temp_scannedArrayList = new ArrayList<>();
    ArrayList<FileInfoModel> temp_importArrayList = new ArrayList<>();

    public static ArrayList<File> retakeFiles = new ArrayList<>();

    private String TAG = "BASECLASS";
    private int responseCode;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    public static BillingClient billingClient;
    private SkuDetails ConsoleProductDeatils = null;
    public static TinyDB tinyDB;

    //    AppContro appContro;
    public static int againCropIndex = 0;
    public static ArrayList<Bitmap> croppedArrayBitmap = new ArrayList<>();
    public static ArrayList<Bitmap> mutliCreatedArrayBitmap = new ArrayList<>();



    static {
        // System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onStart() {
        super.onStart();
        tinyDB = new TinyDB(this);
        resetFontScale(getResources().getConfiguration(), this);
    }

    public static void setStatusBar(Activity activity, int statusBarColorId, int navBarColorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(statusBarColorId));
            window.setNavigationBarColor(activity.getResources().getColor(navBarColorId));
        }
    }

    public void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public void initializeBilling(final Context context) {

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                // To be implemented in a later section.

                if (responseCode == BillingClient.BillingResponseCode.OK
                        && purchases != null) {


                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase, context);
                    }


                } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {


                } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

                    PurchaseHistory_Record();

                } else {


                }


            }
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


    }


    public void BillingConnection(final Context context) {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showProducts(context);
                }

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_TIMEOUT) {
                    BillingConnection(context);
                }

            }

            @Override
            public void onBillingServiceDisconnected() {

                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                BillingConnection(context);

            }
        });

    }

    public void showProducts(Context context) {

        ArrayList<String> skuList = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            skuList.add("android.test.purchased");
        } else {
            skuList.add(BuildConfig.APPLICATION_ID);
        }


        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);


        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {


                        Log.d(TAG, "onSkuDetailsResponse: Products List " + skuDetailsList + "Response Code" + billingResult.getResponseCode());

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                            ConsoleProductDeatils = skuDetailsList.get(0);

                        }

                    }
                });

    }

    public void lunchPruchaseFlow(Activity activity) {

        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(getConsoleProductDeatils())
                .build();

        responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();
    }

    public void handlePurchase(final Purchase purchase, final Context context) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.

                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

                        tinyDB.putBoolean("isPurchased", true);

                        tinyDB.putString("getOrderId", purchase.getOrderId());
                        tinyDB.putString("getPackageName", purchase.getPackageName());
                        tinyDB.putString("getPurchaseTime", "" + purchase.getPurchaseTime());

                        startActivity(new Intent(context, MainActivity.class));

                        //Update Ads Show False
                        if (!purchase.isAcknowledged()) {
                            AcknowledgePurchaseParams acknowledgePurchaseParams =
                                    AcknowledgePurchaseParams.newBuilder()
                                            .setPurchaseToken(purchase.getPurchaseToken())
                                            .build();
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                        }
                    }


                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    public void setConsoleProductDeatils(SkuDetails consoleProductDeatils) {
        ConsoleProductDeatils = consoleProductDeatils;
    }

    public void PurchaseHistory_Record() {

        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(BillingResult billingResult,
                                                          List<PurchaseHistoryRecord> purchasesList) {

                        if (purchasesList.size() > 0 && purchasesList != null) {

                            for (PurchaseHistoryRecord purchase : purchasesList) {

                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                    ArrayList<String> arrayList = purchase.getSkus();

                                    for (String sku : arrayList) {

                                        if (sku.matches(BuildConfig.APPLICATION_ID)) {

                                            //Update Ads Show False
                                            tinyDB.putBoolean("isPurchased", true);
                                            tinyDB.putBoolean("ITEM_ALREADY_OWNED", true);

                                        }


                                    }

                                }

                            }


                        }


                    }
                });
    }

    public SkuDetails getConsoleProductDeatils() {
        if (ConsoleProductDeatils != null)
            return ConsoleProductDeatils;
        else return ConsoleProductDeatils = null;

    }

    public void startActivity(Class<?> calledActivity, Bundle bundle) {
        Intent myIntent = new Intent(this, calledActivity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null)
            myIntent.putExtras(bundle);
        this.startActivity(myIntent);
    }

    public void showToast(String toast, Context context) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        appContro.setmCurrentActivity(this);
    }

    public Bitmap scaledBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, 840, 840, false);
    }

    public void showSaveDialog(Activity activity, String source) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!");
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                croppedArrayBitmap.clear();
                mutliCreatedArrayBitmap.clear();
//                Toast.makeText(activity, source + "in base", Toast.LENGTH_SHORT).show();
                if (activity instanceof EditActivity) {

                    if (source != null && source.equals("MultiScanActivity")) {

                        startActivity(MainActivity.class, null);
//                        Intent intent=new Intent(activity,DocumentsActivity.class);
//                        showInterAd(activity,intent);
                    } else if (source != null && source.equals("PDFViewerActivity")) {
                        startActivity(MainActivity.class, null);
//                        Intent intent=new Intent(activity,DocumentsActivity.class);
//                        showInterAd(activity,intent);
                    } else if (source != null && source.equals("GernalCameraActivity")) {
                        startActivity(GernalCameraActivity.class, null);
//                        Intent intent=new Intent(activity,GernalCameraActivity.class);
//                        showInterAd(activity,intent);
                    } else {
                        startActivity(MainActivity.class, null);
//                        Intent intent=new Intent(activity,GernalCameraActivity.class);
//                        showInterAd(activity,intent);
                    }
                } else if (activity instanceof CropOldActivity || activity instanceof MultiScanActivity) {

                    startActivity(new Intent(activity, MainActivity.class));
                    finish();


                    startActivity(GernalCameraActivity.class, null);
//                    Intent intent = new Intent(activity, GernalCameraActivity.class);
//                    showInterAd(activity,intent);
                } else if (activity instanceof GernalCameraActivity) {
                    startActivity(MainActivity.class, null);
//                    Intent intent = new Intent(activity, DocumentsActivity.class);
//                    showInterAd(activity,intent);
                }
                finish();
            }
        });

        builder.setNegativeButton("Keep", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.create().show();

    }

    public void quitApp(Activity activity) {
        if (activity instanceof MainActivity) {
            ExitDialog dialog = new ExitDialog(activity);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }


    }


    public String creatTempImg(Bitmap bitmap, int id) {


        String filename = "temp" + id + ".jpeg";
        File dest = getOutputMediaFile(filename);
        try {
            dest.createNewFile();
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public File getOutputMediaFile(String filename) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + filename + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public boolean isConnected() {
        try {
            String command = "ping -c 1 google.com";
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (Exception e) {
            return false;
        }

    }

    public void FirebaseToken(final Context context) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        setToken(task.getResult());

                        // Log and toast
                        Log.d("MyToken", getToken());

                    }

                });
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }


    public static boolean hasPermissionGranted(Activity context) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            int result3 = ContextCompat.checkSelfPermission(context, CAMERA);
            if (Environment.isExternalStorageManager() && result3 == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else return false;
        } else {
            int result = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
            int result3 = ContextCompat.checkSelfPermission(context, CAMERA);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
        }
    }
    public static void requestPermission(Activity context) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",context.getApplicationContext().getPackageName())));
                context.startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                context.startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(context, new String[]{WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSnackbar(Activity context, int resID) {
        Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnectedOrConnecting())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnectedOrConnecting())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean havePurchase() {
        try {
            tinyDB = new TinyDB(this);
            if(tinyDB.getBoolean("isPurchased")){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }

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