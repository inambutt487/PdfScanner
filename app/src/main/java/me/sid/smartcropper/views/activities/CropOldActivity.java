package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageUtils;
import me.sid.smartcropperlib.IvGenericCallback;
import me.sid.smartcropperlib.SmartCropper;
import me.sid.smartcropperlib.view.CropImageView;

/*public class CropOldActivity extends BaseActivity {
}*/
public class CropOldActivity extends BaseActivity implements View.OnClickListener, IvGenericCallback {

    String source;
    DirectoryUtils mDirectory;
    CropImageView ivCrop;
    ImageView leftPage, rightPage;
    ImageButton back_btn, settin_btn, pdf_btn;
    TextView tv_crop_count;
    /*Button  btn_crop, btn_confirm,btn_undo,btn_redo;*/
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

    boolean isMulti = false;

    boolean enableScaned = true;


    private TextView deleteCrop, cropRetake, btn_crop, btn_confirm;

    int retake = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_old_crop);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading data");
        mDirectory = new DirectoryUtils(this);
        arrayListfile = new ArrayList<>();
        arrayListfile.clear();

        if (getIntent() != null) {
            againCrop = getIntent().getBooleanExtra("againCrop", false);//check if comes again for crop


            retake = getIntent().getIntExtra("retake", -1);

            if(retake >= 0) {

                try {
                    arrayListfile.addAll(retakeFiles);
                    arrayCount = arrayListfile.size();
                    retakeFiles.clear();

                    File retake_bitmap = (File) getIntent().getSerializableExtra("retakeData");
                    arrayListfile.set(retake, retake_bitmap);
                }catch (Exception e){
                    Log.d("Issue", e.getMessage());
                }

            }else{
                arrayListfile = (ArrayList<File>) getIntent().getSerializableExtra("FILES_TO_SEND");
                arrayCount = arrayListfile.size();
            }

            Log.d("Retake", "arrayCount: "+arrayCount);


        }

        init();

    }

    private void init() {

        source = getIntent().getStringExtra("source");

        ivCrop = findViewById(R.id.iv_crop);
        ivCrop.setOnClickListener(this);
        btn_crop = findViewById(R.id.cropBtn);
        btn_confirm = findViewById(R.id.btn_ok);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);
        pdf_btn = findViewById(R.id.pdf_btn);

        pdf_btn.setOnClickListener(this);


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

            if (retake >= 0) {
                scrollPos = retake;
                setData(scrollPos);
            }else{
                setData(0);
            }

        } else {
            isMulti = false;
        }

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isMulti) {

                    cropBtnStatus = true;

                    croppedBitmp = ivCrop.crop();
                    croppedArrayBitmap.set(scrollPos, croppedBitmp);

                    if (scrollPos + 1 < arrayCount) {
                        mSetData(1);
                    } else {
                        goNext();
                    }


                } else {
                    cnfrmBtnStatus = true;
                    cropBtnStatus = true;


                    croppedBitmp = ivCrop.crop();
                    croppedArrayBitmap.set(scrollPos, croppedBitmp);


                    new CropImagee(croppedBitmp).execute();
                }
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Point[] mCropPoints = ivCrop.getCropPoints();
                if (enableScaned) {
                    enableScaned = false;
                    ivCrop.setCropPoints(SmartCropper.withoutScan(ivCrop.getBitmap()));
                } else {
                    enableScaned = true;
                    ivCrop.setCropPoints(SmartCropper.scan(ivCrop.getBitmap()));
                }


            }
        });


        rightPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scrollPos + 1 < arrayCount) {

                    mSetData(1);
                }
            }
        });

        leftPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (scrollPos > 0) {
                    mSetData(0);
                }

            }
        });


        deleteCrop = findViewById(R.id.deleteCrop);
        deleteCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (arrayCount > 1) {

                        int temPos;
                        if (scrollPos + 1 == arrayCount) {
                            //Left
                            temPos = scrollPos;
                            scrollPos = scrollPos - 1;
                            if (arrayCount > 1) {
                                originalBitmap = croppedArrayBitmap.get(scrollPos);
                                croppedArrayBitmap.remove(temPos);
                                if (originalBitmap != null) {
                                    new CropImagee(originalBitmap).execute();
                                }

                                arrayListfile.remove(temPos);
                                arrayCount = arrayListfile.size();
                                int k = temPos;
                                tv_crop_count.setText("Page " + k + "/" + arrayCount);
                            }


                        } else {
                            //Right
                            temPos = scrollPos;
                            scrollPos = scrollPos + 1;
                            if (scrollPos < croppedArrayBitmap.size()) {
                                originalBitmap = croppedArrayBitmap.get(scrollPos);
                                croppedArrayBitmap.remove(temPos);
                                arrayListfile.remove(temPos);
                                new CropImagee(originalBitmap).execute();
                                Log.d("Right", "croppedArrayBitmap");
                            } else {
                                originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
                                croppedArrayBitmap.add(originalBitmap);

                                arrayListfile.remove(temPos);
                                croppedArrayBitmap.remove(temPos);

                                if (originalBitmap != null) {
                                    new CropImagee(originalBitmap).execute();
                                }
                                Log.d("Right", "arrayListfile");
                            }

                            arrayCount = arrayListfile.size();
                            int k = temPos + 1;
                            tv_crop_count.setText("Page " + k + "/" + arrayCount);

                            scrollPos = scrollPos - 1;
                        }

                    } else {
                        onBackPressed();
                    }

                } catch (Exception e) {

                }


            }
        });

        cropRetake = findViewById(R.id.cropRetake);
        cropRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMulti) {
                    try {

                        retake = scrollPos;
                        retakeFiles = arrayListfile;

                        Intent intent = new Intent(CropOldActivity.this, GernalCameraActivity.class);
                        intent.putExtra("retake", retake);
                        startActivity(intent);

                    } catch (Exception e) {

                    }

                } else {
                    startActivity(new Intent(CropOldActivity.this, GernalCameraActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!havePurchase()) {
            pdf_btn.setVisibility(View.VISIBLE);
        }
    }

    void goNext() {

        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        if (source != null) {
            intent.putExtra("source", source);
        }

        startActivity(intent);

    }


    public void setData(int innerCount) {


        if (arrayCount > 1) {
            originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath());
            new CropImagee(originalBitmap).execute();
            count = innerCount + 1;

            if (count == arrayCount) {
                btn_confirm.setText("Done");
            }
            int k = scrollPos + 1;
            tv_crop_count.setText("Page " + k + "/" + arrayCount);
        } else {
            count = 1;
            originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath());
            new CropImagee(originalBitmap).execute();
        }

        if (retake >= 0) {
            retake = -1;
        }else{
            croppedArrayBitmap.add(originalBitmap);
        }

    }


    public void mSetData(int pageDirection) {

        arrayCount = arrayListfile.size();
        if (pageDirection == 0) { //Left page
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
                new CropImagee(originalBitmap).execute();
            }

        } else if (pageDirection == 1) //right click
        {
            if (arrayCount > 1) {
                scrollPos = scrollPos + 1;
                if (scrollPos < croppedArrayBitmap.size()) {
                    originalBitmap = croppedArrayBitmap.get(scrollPos);
                    new CropImagee(originalBitmap).execute();
                } else {
                    originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
                    croppedArrayBitmap.add(originalBitmap);

                    if (originalBitmap != null) {
                        new CropImagee(originalBitmap).execute();
                        Log.d("Retake", "croppedArrayBitmap: "+croppedArrayBitmap.size());
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
                new CropImagee(originalBitmap).execute();
            }


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


    @Override
    public void Ivcallback(Object o) {
        String res = (String) o;
        if (res.equals("done"))
            dialog.dismiss();
    }


    private class CropImagee extends AsyncTask<Bitmap, Void, Bitmap> {


        public Bitmap RotateBitmap(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        public CropImagee(Bitmap mselectedBitmap) {
            selectedBitmap = mselectedBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        public Bitmap rotateImage(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    dialog.show();

                    ivCrop.setImageToCrop(selectedBitmap, CropOldActivity.this);
                }
            });
            return ivCrop.getBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dialog.dismiss();

            if (!isMulti && cnfrmBtnStatus) {
                goNext();
            }


        }
    }
}
