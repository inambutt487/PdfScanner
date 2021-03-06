package me.sid.smartcropper.utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

@TargetApi(Build.VERSION_CODES.M)
public class PermissionUtils {

    // TODO: handle never ask use case
    // https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en

    // region PERMISSION_CONSTANTS
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1199;
    // calendar
    public static final int PERMISSION_READ_CALENDAR = 1;
    public static final int PERMISSION_WRITE_CALENDAR = 2;
    // camera
    public static final int PERMISSION_CAMERA = 3;
    // contacts
    public static final int PERMISSION_GET_ACCOUNTS = 4;
    public static final int PERMISSION_READ_CONTACTS = 5;
    public static final int PERMISSION_WRITE_CONTACTS = 6;
    // location
    public static final int PERMISSION_LOCATION = 7;
    // microphone
    public static final int PERMISSION_RECORD_AUDIO = 8;
    // phone
    public static final int PERMISSION_READ_PHONE_STATE = 9;
    public static final int PERMISSION_CALL_PHONE = 10;
    public static final int PERMISSION_READ_CALL_LOG = 11;
    public static final int PERMISSION_WRITE_CALL_LOG = 12;
    public static final int PERMISSION_ADD_VOICE_EMAIL = 13;
    public static final int PERMISSION_USE_SIP = 14;
    public static final int PERMISSION_PROCESS_OUTGOING_CALLS = 15;
    // sensors
    public static final int PERMISSION_BODY_SENSORS = 16;
    // sms
    public static final int PERMISSION_SEND_SMS = 17;
    public static final int PERMISSION_RECEIVE_SMS = 18;
    public static final int PERMISSION_READ_SMS = 19;
    public static final int PERMISSION_RECEIVE_WAP_PUSH = 20;
    public static final int PERMISSION_RECEIVE_MMS = 21;
    // storage
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 22;
    public static final int PERMISSION_EXTERNAL_STORAGE = 23;
    // endregion


    /*public static boolean hasPermissionGranted(Context context, String[] permissions) {
        boolean hasGranted = false;
        for (String permission : permissions) {
            hasGranted = (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
            if (!hasGranted)
                return false;
        }
        return hasGranted;
    }*/

    public static boolean hasPermissionGranted(Context context, String[] permissions) {
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


    public static boolean shouldShowPermissionRationale(Activity activity, String[] permissions) {
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : permissions) {
            shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (!shouldShowRequestPermissionRationale)
                return false;
        }
        return shouldShowRequestPermissionRationale;
    }

    public static void requestPermission(Object object, int permissionId, String[] permission) {
        if (object instanceof Activity)
            ActivityCompat.requestPermissions((Activity) object, permission, permissionId);
        else {
            Fragment fragment = ((Fragment) object);
            if (fragment.isAdded() && fragment.getActivity() != null)
                fragment.requestPermissions(permission, permissionId);
        }
    }

    public static boolean verifyPermission(int[] grantResults) {
        // at least one result must be checked.
        if (grantResults.length < 1)
            return false;
        // verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static boolean requestPermission(Context context, String permission, int requestCode) {
        if (SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            return true;
        else {
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).requestPermissions(new String[]{permission}, requestCode);
            }
        }
        return false;
    }

    /**
     * Check and ask for disabled permissions
     *
     * @param activity    Activity calling the method
     * @param permissions permissions array needed to be checked
     * @param requestCode request code associated with the request call
     * @return flag specifying permission are enabled or not
     */
    public static void checkAndRequestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (SDK_INT >= Build.VERSION_CODES.R) {

            int result3 = ContextCompat.checkSelfPermission(activity, CAMERA);
            if (!Environment.isExternalStorageManager() || result3 != PackageManager.PERMISSION_GRANTED) {

                if (result3 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{CAMERA}, requestCode);
                }


                if (!Environment.isExternalStorageManager()) {

                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
                        activity.startActivityForResult(intent, requestCode);
                    } catch (Exception e) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        activity.startActivityForResult(intent, requestCode);
                    }

                }

            }


        } else {
            //below android 11
            ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA}, requestCode);
        }
    }

    public static boolean hasLocationPermissionGranted(Context context) {
        return SDK_INT < Build.VERSION_CODES.M ||
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }


}
