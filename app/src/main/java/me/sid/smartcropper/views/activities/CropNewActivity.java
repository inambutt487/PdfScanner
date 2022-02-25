package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.Crop.BitmapUtils;
import me.sid.smartcropper.utils.Crop.PolygonView;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageUtils;
import me.sid.smartcropper.utils.OCRUtils;


public class CropActivity extends BaseActivity implements View.OnClickListener {

    private ImageView sourceImageView;
    private FrameLayout sourceFrame;
    private PolygonView polygonView;
    private Bitmap original;
    private Bitmap Crop;

    String source;
    DirectoryUtils mDirectory;

    ImageView leftPage, rightPage;
    ImageButton back_btn, settin_btn, pdf_btn;
    TextView tv_crop_count;
    Button btn_crop, btn_confirm, btn_undo, btn_redo;
    Bitmap selectedBitmap = null;
    private ArrayList<File> arrayListfile = null;
    ProgressDialog dialog;
    int arrayCount = 0;
    int count = 0;
    int scrollPos = 0;
    boolean againCrop = false;
    boolean cropBtnStatus = false;
    boolean cnfrmBtnStatus = false;
    Bitmap originalBitmap;
    Bitmap croppedBitmp;
    ArrayList<File> originalArrayFile;
    ArrayList<Bitmap> currentBitmapArray;

    boolean isMulti = false;


    int pos = 0;
    int size = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading data");
        mDirectory = new DirectoryUtils(this);
        arrayListfile = new ArrayList<>();
        originalArrayFile = new ArrayList<>();
        if (getIntent() != null) {
            againCrop = getIntent().getBooleanExtra("againCrop", false);//check if comes again for crop
            arrayListfile = (ArrayList<File>) getIntent().getSerializableExtra("FILES_TO_SEND");
            originalArrayFile = (ArrayList<File>) getIntent().getSerializableExtra("FILES_TO_SEND");

            if (arrayListfile != null)
                arrayCount = arrayListfile.size();
            size = arrayCount;
        }

        init();

    }

    private void init() {
        source = getIntent().getStringExtra("source");

        polygonView = (PolygonView) findViewById(R.id.polygonView);
        sourceImageView = (ImageView) findViewById(R.id.sourceImageView);
        sourceFrame = (FrameLayout) findViewById(R.id.sourceFrame);


        btn_undo = findViewById(R.id.btn_crop_cancel);
        btn_redo = findViewById(R.id.btn_redo);
        btn_crop = findViewById(R.id.btn_crop);
        btn_confirm = findViewById(R.id.btn_confirm);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);
        pdf_btn = findViewById(R.id.pdf_btn);
        pdf_btn.setOnClickListener(this);
        currentBitmapArray = new ArrayList<Bitmap>();


        tv_crop_count = findViewById(R.id.tv_crop_count);

        leftPage = findViewById(R.id.left_page);
        rightPage = findViewById(R.id.right_page);
        originalBitmap = selectedBitmap;
        croppedBitmp = selectedBitmap;


        if (arrayCount > 0) {
            if (arrayCount > 1) {
                isMulti = true;

                tv_crop_count.setVisibility(View.VISIBLE);
                leftPage.setVisibility(View.VISIBLE);
                rightPage.setVisibility(View.VISIBLE);
            } else {
                isMulti = false;
            }


            originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(pos).getAbsolutePath());
            new CropImagee(originalBitmap).execute();


        }


        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(pos).getAbsolutePath());
                new CropImagee(originalBitmap).execute();

            }
        });

        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (croppedArrayBitmap.size() > pos) {
                    new CropImagee(croppedArrayBitmap.get(pos)).execute();
                }

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getCropImage()) {
                    dialog.show();
                    doCrop();
                }

            }
        });

        rightPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getCropImage()) {
                    dialog.show();
                    doCrop();
                }


            }
        });

        leftPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pos > 0) {
                    pos--;
                    croppedArrayBitmap.remove(pos);
                    originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(pos).getAbsolutePath());
                    new CropImagee(originalBitmap).execute();
                } else {
                    croppedArrayBitmap.clear();
                }

            }
        });


    }

    void goNext() {


        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        if (source != null) {
            intent.putExtra("source", source);
        }

        startActivity(intent);

    }

    public void refreshArraylistData(int innerCount) {

        new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();

    }

    public void setData(int innerCount) {


        if (arrayCount > 1) {
            originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath());
            new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();
            count = innerCount + 1;

            if (count == arrayCount) {
                btn_confirm.setText("Done");
            }
            int k = scrollPos + 1;
            tv_crop_count.setText("Page " + k + "/" + arrayCount);
        } else {
            count = 1;
            originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath());
            new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();
        }
        croppedArrayBitmap.add(originalBitmap);
        //  currentBitmapArray.add(originalBitmap);
        //   mDirectory.deleteFile(new File(arrayListfile.get(innerCount).getAbsolutePath()));

    }

    public void mSetData(int pageDirection) {
        if (pageDirection == 0) //left click
        {
            scrollPos = scrollPos - 1;
            if (arrayCount > 1) {
                originalBitmap = croppedArrayBitmap.get(scrollPos);
                if (originalBitmap != null) {
                    new CropImagee(originalBitmap).execute();
                }
                if (count == arrayCount) {
                    btn_confirm.setText("Done");
                }
                int k = scrollPos + 1;
                tv_crop_count.setText("Page " + k + "/" + arrayCount);
            } else {
                count = 1;
                originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
                new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath())).execute();
            }
            //   mDirectory.deleteFile(new File(arrayListfile.get(scrollPos).getAbsolutePath()));
        } else if (pageDirection == 1) //right click
        {
            if (arrayCount > 1) {
                scrollPos = scrollPos + 1;
                if (scrollPos < croppedArrayBitmap.size()) { //
                    originalBitmap = croppedArrayBitmap.get(scrollPos);
                    new CropImagee(originalBitmap).execute();
                } else { //have to laod new image
                    originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
                    croppedArrayBitmap.add(originalBitmap);

                    if (originalBitmap != null) {
                        new CropImagee(originalBitmap).execute();
                    } else {
//                   showToast("bit is null",getApplicationContext());

                    }

                }


                if (count == arrayCount) {
                    btn_confirm.setText("Done");
                }
                int k = scrollPos + 1;
                tv_crop_count.setText("Page " + k + "/" + arrayCount);
            } else {
                count = 1;
                originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
                new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath())).execute();
            }
            //  mDirectory.deleteFile(new File(arrayListfile.get(scrollPos).getAbsolutePath()));

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        if (requestCode == 999 && data != null) {
            ArrayList<File> file = (ArrayList<File>) data.getSerializableExtra("file");
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.back_btn) {
            onBackPressed();
        } else if (view.getId() == R.id.settin_btn) {
            startActivity(SettingActivity.class, null);
        } else if (view.getId() == R.id.pdf_btn) {

            final String appPackageName = "com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    @Override
    public void onBackPressed() {
        showSaveDialog(this, "");
    }

    private class CropImagee extends AsyncTask<Bitmap, Void, Bitmap> {


        public CropImagee(Bitmap mselectedBitmap) {
            selectedBitmap = mselectedBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                    showImageCrop(selectedBitmap);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dialog.dismiss();

        }
    }


    public void showImageCrop(Bitmap bitmap) {
        sourceFrame.post(new Runnable() {
            @Override
            public void run() {
                setBitmap(bitmap);
            }
        });

    }

    private void setBitmap(Bitmap original) {
        Bitmap scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        sourceImageView.setImageBitmap(scaledBitmap);
        Bitmap tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {

        float[] points = new float[8];
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];

        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    public Boolean getCropImage() {

        Map<Integer, PointF> points = polygonView.getPoints();
        if (isScanPointsValid(points)) {
            return true;
        } else {
            Toast.makeText(this, "Please Select Crop Points as Square", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void doCrop() {

        try {
            Map<Integer, PointF> points = polygonView.getPoints();
            new ScanAsyncTask(originalBitmap, points).execute();
        }catch (Exception e){

        }
    }


    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;
        private Bitmap orginal;

        public ScanAsyncTask(Bitmap bitmap, Map<Integer, PointF> points) {
            this.points = points;
            this.orginal = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            if (isMulti) {

                croppedArrayBitmap.add(getScannedBitmap(orginal, points));

                pos++;
                if (pos < size) {
                    originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(pos).getAbsolutePath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImageCrop(originalBitmap);
                            dialog.dismiss();
                        }
                    });

                } else {
                    goNext();
                }

                Log.d("croppedArrayBitmap ", ""+croppedArrayBitmap.size() +" position "+ pos);
            } else {
                croppedArrayBitmap.add(getScannedBitmap(orginal, points));
                goNext();
            }
            return getScannedBitmap(orginal, points);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {

        float xRatio = (float) original.getWidth() / sourceImageView.getWidth();
        float yRatio = (float) original.getHeight() / sourceImageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;

        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;
        Log.d("CropActivity", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        //Bitmap _bitmap = ((ScanActivity) getActivity()).getScannedBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4);
        Bitmap _bitmap = null;
        //        new ScanAsyncTask(points).execute();
        float[] cropPoints = new float[8];

        cropPoints[0] = x1;
        cropPoints[1] = y1;
        cropPoints[2] = x2;
        cropPoints[3] = y2;

        cropPoints[4] = x3;
        cropPoints[5] = y3;
        cropPoints[6] = x4;
        cropPoints[7] = y4;

        File path = saveToInternalStorage(original);

        Rect rect = BitmapUtils.getRectFromPoints(cropPoints, original.getWidth(), original.getHeight(), false, 0, 0);

        BitmapUtils.BitmapSampled bitmapSampled = BitmapUtils.decodeSampledBitmapRegion(this, Uri.fromFile(path), rect, original.getWidth(), original.getHeight(), 1);
        _bitmap = bitmapSampled.bitmap;
        return _bitmap;
    }

    private File saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath;
    }
}
