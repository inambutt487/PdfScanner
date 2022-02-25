package me.sid.smartcropper.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class ImageUtils {

    static public Bitmap loadCapturedBitmap(String fileName) {
        int targetW = 1100;
        int targetH = 1100;

        int degree = getRotateDegreeFromExif(fileName);
        Matrix matrix = new Matrix();

        Log.d("Rotation", "degree: "+degree);

        if(degree == 90){
            matrix.postRotate(90);
        }
        else if(degree == 180){
            matrix.postRotate(180);
        }
        else if(degree == 270){
            matrix.postRotate(270);
        }
        else if(degree == 360){
            matrix.postRotate(360);
        }
        else{
            matrix.postRotate(0);
        }



        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName, bmOptions);
        if (bitmap == null)
            return null;
        Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

       /* Matrix rotationMatrix = new Matrix();
        if(rotatedImage.getWidth() >= rotatedImage.getHeight()){
            rotationMatrix.setRotate(-90);

            Log.d("Rotation", "landscape");
        }else{
            rotationMatrix.setRotate(0);

            Log.d("Rotation", "Portrait");
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(rotatedImage,0,0,rotatedImage.getWidth(),rotatedImage.getHeight(),rotationMatrix,true);*/

        return rotatedImage;
    }


    static private int getRotateDegreeFromExif(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                degree = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                degree = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                degree = 270;
            }
            if (degree != 0) {
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                        "0");
                exifInterface.saveAttributes();
            }
        } catch (IOException e) {
            degree = -1;
            e.printStackTrace();
        }

        return degree;
    }


    static public  Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    static public Bitmap scaledBitmap(Bitmap fileName) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */
        /* Get the size of the ImageView */
        int targetW = 1100;
        int targetH = 1100;

        Bitmap bitmap = Bitmap.createScaledBitmap(fileName, targetW, targetH, true);

        return bitmap;

    }


    static public Bitmap rotatedBitmap(float angle,Bitmap bitmapOrg) {

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int newWidth = 1100;
        int newHeight = 1100;

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        matrix.postRotate(angle);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);
        return resizedBitmap;
    }


}
