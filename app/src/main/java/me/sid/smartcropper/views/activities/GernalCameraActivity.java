package me.sid.smartcropper.views.activities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.util.concurrent.ListenableFuture;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.FilePath;
import me.sid.smartcropper.utils.compressor.Compressor;
import me.sid.smartcropper.views.CameraResultDialog;

public class GernalCameraActivity extends BaseActivity implements View.OnClickListener {
    ArrayList<Image> images;
    //
    private PreviewView previewView = null;

    private static final String EXTRA_FROM_ALBUM = "extra_from_album";
    private static final int REQUEST_CODE_SELECT_ALBUM = 200;
    boolean mFromAlbum, multiSelected = false;
    Bitmap selectedBitmap = null;

    CameraResultDialog.CallbacksForCNIC callbacks;

    private ArrayList<File> arrayListfile = null;

    private Executor executor = Executors.newSingleThreadExecutor();
    @SuppressLint("RestrictedApi")
    private CameraSelector lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
    private ImageCapture imageCapture = null;

    ImageButton settin_btn, pdf_btn, close_btn, btnCapture;
    Button menu_Whiteboard, menu_Form, menu_Document, menu_BusinessCard, btn_single, btn_multi, btn_done_mutli;
    ImageView gallery_iv, sample_iv;
    TextView tv_images_count;
    ConstraintLayout mode_selection;
    int fromMulti = 0;
    int retake = -1;
    String resumeSource = "Activity";
    ConstraintLayout scanlayout;

    private long mLastClickTime = 0;

    private FrameLayout layoutSingle, layoutMulti;

    private ImageView scan_shape;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.fragment_gernal_camera);

        scanlayout = findViewById(R.id.scanning);

        scan_shape = findViewById(R.id.scan_shape);

        layoutSingle = findViewById(R.id.layoutSingle);
        layoutMulti = findViewById(R.id.layoutMulti);

        /*tinyDB.putString("Doc", "DocumentScanner_WB_");*/
        init();

        if (getIntent() != null) {
            fromMulti = getIntent().getIntExtra("fromMulti", 0);//check if comes again for crop

            if (fromMulti == 1) // need to stop selection of multi or single, if they comes from multi, screens. Only multi now allow.
            {
                mode_selection.setVisibility(View.GONE);
                mFromAlbum = true;
                selectPhoto();
            } else if (fromMulti == 2) // need to stop selection of multi or single, if they comes from multi, screens. Only multi now allow.
            {
                mode_selection.setVisibility(View.GONE);
                multiSelected = true;
            }

            retake = getIntent().getIntExtra("retake", -1);

            if(retake >= 0){
                try {
                    mode_selection.setVisibility(View.GONE);
                }catch (Exception e){

                }

            }

        }

        arrayListfile = new ArrayList<>();

        startCamera();

        callbacks = new CameraResultDialog.CallbacksForCNIC() {
            @Override
            public void okCallback(File imgFile) {
                try {
                    File compressor = new Compressor(GernalCameraActivity.this)
                            .setMaxHeight(600)
                            .setMaxWidth(600)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(imgFile);
                    setResult(999, new Intent().putExtra("file", compressor));
                    finish();
                } catch (Exception e) {
                    setResult(999, new Intent().putExtra("file", imgFile));
                    finish();
                }
            }

            @Override
            public void cancelCallback() {
                setResult(999);
                finish();
            }
        };
    }


    private void init() {

        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);
        pdf_btn = findViewById(R.id.pdf_btn);
        if(!havePurchase()){
            pdf_btn.setVisibility(View.VISIBLE);
        }
        pdf_btn.setOnClickListener(this);
        close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(this);
        btn_single = findViewById(R.id.btn_single);
        btn_single.setOnClickListener(this);
        btn_multi = findViewById(R.id.btn_multi);
        btn_multi.setOnClickListener(this);
        btn_done_mutli = findViewById(R.id.btn_done_mutli);
        btn_done_mutli.setOnClickListener(this);
        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);
        menu_Whiteboard = findViewById(R.id.menu_Whiteboard);
        menu_Whiteboard.setOnClickListener(this);
        menu_Form = findViewById(R.id.menu_Form);
        menu_Form.setOnClickListener(this);
        menu_Document = findViewById(R.id.menu_Document);
        menu_Document.setOnClickListener(this);
        menu_BusinessCard = findViewById(R.id.menu_BusinessCard);
        menu_BusinessCard.setOnClickListener(this);
        gallery_iv = findViewById(R.id.gallery_iv);
        gallery_iv.setOnClickListener(this);
        sample_iv = findViewById(R.id.sample_iv);
        sample_iv.setOnClickListener(this);
        previewView = findViewById(R.id.previewView);
        previewView.setOnClickListener(this);

        tv_images_count = findViewById(R.id.tv_images_count);
        mode_selection = findViewById(R.id.mode_selection);

        if (!multiSelected) {
            btn_done_mutli.setVisibility(View.GONE);
        }

    }

    private void goToCrop() {


        if (arrayListfile.size() > 0) {
            if (fromMulti == 0) // if not coming from multi screen, then clear the array.

                if(retake == -1){
                    croppedArrayBitmap.clear();
                }



            goNext();

        }

    }

    private void goNext() {

        Intent intent = new Intent(GernalCameraActivity.this, CropOldActivity.class);
                intent.putExtra("source","GernalCameraActivity");
                if(retake >=0){

                    try {
                        intent.putExtra("retake", retake);
                        intent.putExtra("retakeData", arrayListfile.get(0));

                    }catch (Exception e){

                    }

                }else{
                    intent.putExtra("FILES_TO_SEND", arrayListfile);
                }
                startActivity(intent);
    }



    private void selectPhoto() {
        if (multiSelected) {
            ImagePicker.with(this)
                    .setFolderMode(true)
                    .setFolderTitle("Album")
                    .setRootDirectoryName("DCIM")
                    .setDirectoryName("Image Picker")
                    .setMultipleMode(true)
                    .setShowNumberIndicator(true)
                    .setMaxSize(10)
                    .setLimitMessage("You can select up to 10 images")

                    .setRequestCode(100)
                    .start();
        } else {
            if (mFromAlbum) {
                Intent selectIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selectIntent.setType("image/*");
                if (selectIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(selectIntent, REQUEST_CODE_SELECT_ALBUM);
                }
            }
        }
    }


    protected void takePicture() {

        File file = new File(getExternalFilesDir(null), "image-temp1" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new
                ImageCapture.OutputFileOptions.Builder(file).build();

        if (!multiSelected) {
            scanlayout.setVisibility(View.VISIBLE);
            btn_done_mutli.setVisibility(View.GONE);
        } else {
            scanlayout.setVisibility(View.GONE);
            btn_done_mutli.setVisibility(View.VISIBLE);
        }

        imageCapture.takePicture(
                outputFileOptions,
                executor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        runOnUiThread(() ->
                                Glide.with(GernalCameraActivity.this).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(sample_iv)

                        );

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (!multiSelected) {
                                    scanlayout.setVisibility(View.VISIBLE);
                                } else {
                                    scanlayout.setVisibility(View.GONE);
                                }

                                addDataToArray(file);
                                if (!multiSelected) {
                                    goToCrop();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getApplicationContext(), "Captured Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void addDataToArray(File file) {
        if (multiSelected) {
            arrayListfile.add(file);
            if (arrayListfile.size() > 0) {
                tv_images_count.setVisibility(View.VISIBLE);
                btn_done_mutli.setVisibility(View.VISIBLE);
                tv_images_count.setText(String.valueOf(arrayListfile.size()));
            }
        } else {
            arrayListfile.add(0, file);
        }
    }

    @SuppressLint("RestrictedApi")
    private void bindCamera(ProcessCameraProvider cameraProviderFuture) {
        CameraX.unbindAll();

        // Preview config for the camera
        Preview previewConfig = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Handles the output data of the camera
        previewConfig.setSurfaceProvider(previewView.createSurfaceProvider());


        // Image capture config which controls the Flash and Lens
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .setCameraSelector(lensFacing)
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .build();

        ProcessCameraProvider cameraProvider = cameraProviderFuture;
        // Bind the camera to the lifecycle
        cameraProvider.bindToLifecycle(
                ((LifecycleOwner) this),
                cameraSelector,
                imageCapture,
                previewConfig
        );
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                ProcessCameraProvider cameraProvider = null;
                try {
                    cameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bindCamera(cameraProvider);

            }
        }, ContextCompat.getMainExecutor(this));

    }


    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_SELECT_ALBUM && data != null && data.getData() != null) {
            ContentResolver cr = getContentResolver();
            Uri bmpUri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                selectedBitmap = BitmapFactory.decodeStream(cr.openInputStream(bmpUri), new Rect(), options);

                String selectedFilePath = FilePath.getPath(this, bmpUri);
                final File file = new File(selectedFilePath);
                arrayListfile.add(file);
                sample_iv.setImageBitmap(selectedBitmap);
                goToCrop();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //
        else if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 100)) {
            Log.d("request", requestCode + "d");
            resumeSource = "ImagePicker";
            images = ImagePicker.getImages(data);
            // Do stuff with image's path or id. For example:
            for (Image image : images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //Glide.with(getApplicationContext()).load(image.getUri()).into(imageView);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image.getUri());

                        sample_iv.setImageBitmap(bitmap);
                        sample_iv.setClickable(true);
                        //populate arrayListfile
                        arrayListfile.add(new File(image.getPath()));
                    } catch (Exception e) {
                        //handle exception
                    }

                } else {
                    //Glide.with(context).load(image.path).into(imageView);
                }
            }
            goToCrop();


        }

        /*if (requestCode == UCrop.REQUEST_CROP) {
            if(getIntent() != null){
                handleCropResult(data);
            }

        }*/

    }

    /*private void handleCropResult(@NonNull Intent result) {


        final ArrayList<File> resultUri = (ArrayList<File>) UCrop.getOutput(result);
        if (resultUri != null) {

            if (resultUri.size() > 0) {

                for (int i = 0; i < resultUri.size(); i++) {

                    Bitmap originalBitmap = ImageUtils.loadCapturedBitmap(resultUri.get(i).getAbsolutePath());
                    croppedArrayBitmap.add(originalBitmap);
                }

            } else {

                Bitmap originalBitmap = ImageUtils.loadCapturedBitmap(resultUri.get(0).getAbsolutePath());
                croppedArrayBitmap.add(originalBitmap);
            }


            String source = "GernalCameraActivity";
            startActivity(new Intent(GernalCameraActivity.this, EditActivity.class));
        }
    }*/

    /*private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {


            Bitmap originalBitmap = ImageUtils.loadCapturedBitmap(resultUri.getPath());
                croppedArrayBitmap.add(originalBitmap);
                String source = "GernalCameraActivity";
                startActivity(new Intent(GernalCameraActivity.this, EditActivity.class));
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.settin_btn) {

        } else if (view.getId() == R.id.pdf_btn) {
            final String appPackageName = "com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf"; // getPackageName() from Context or Activity object

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//            } catch (android.content.ActivityNotFoundException anfe) {
//            }

        } else if (view.getId() == R.id.btnCapture) {
            if (!multiSelected) {
                // mis-clicking prevention, using threshold of 1000 ms
                Log.d("timee", "" + String.valueOf(SystemClock.elapsedRealtime() - mLastClickTime));
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
            takePicture();

        } else if (view.getId() == R.id.close_btn) {
            if (fromMulti != 0) {
                showSaveDialog(this, "");
            } else {
                startActivity(MainActivity.class, null);
                finish();
            }
        } else if (view.getId() == R.id.gallery_iv) {

            mFromAlbum = getIntent().getBooleanExtra(EXTRA_FROM_ALBUM, true);
            selectPhoto();

            /*            clearMyFiles();*/
        } else if (view.getId() == R.id.sample_iv) {
      /*      ImagePicker.with(this)
                    .setFolderMode(true)
                    .setFolderTitle("Album")
                    .setRootDirectoryName("DCIM")
                    .setDirectoryName("Image Picker")
                    .setMultipleMode(true)
                    .setShowNumberIndicator(true)
                    .setMaxSize(10)
                    .setLimitMessage("You can select up to 10 images")
                    .setSelectedImages(images)
                    .setRequestCode(100)
                    .start();*/
        } else if (view.getId() == R.id.btn_done_mutli) {

            goToCrop();


        }

        //single click
        else if (view.getId() == R.id.btn_single) {
            changeColor(R.id.btn_single);
            multiSelected = false;
//            btn_done_mutli.setVisibility(View.GONE);
        }
        //multi click
        else if (view.getId() == R.id.btn_multi) {
            changeColor(R.id.btn_multi);
            multiSelected = true;
//            btn_done_mutli.setVisibility(View.VISIBLE);
        }

        //menu
        else if (view.getId() == R.id.menu_Whiteboard) {
            changeColor(R.id.menu_Whiteboard);
        } else if (view.getId() == R.id.menu_Form) {
            changeColor(R.id.menu_Form);
        } else if (view.getId() == R.id.menu_Document) {
            changeColor(R.id.menu_Document);
        } else if (view.getId() == R.id.menu_BusinessCard) {
            changeColor(R.id.menu_BusinessCard);
        }
    }

    private void changeColor(int id) {
        if (id == R.id.menu_Whiteboard) {
            /*tinyDB.putString("Doc", "DocumentScanner_WB_");*/
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Whiteboard.setBackgroundResource(R.drawable.button_seagreen);
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setBackgroundResource(0);
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setBackgroundResource(0);
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setBackgroundResource(0);
            scan_shape.setImageResource(R.drawable.scan_whiteboard);
            scan_shape.setBackgroundResource(0);
        } else if (id == R.id.menu_Form) {
            /*tinyDB.putString("Doc", "DocumentScanner_Form_");*/
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setBackgroundResource(R.drawable.button_seagreen);
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Whiteboard.setBackgroundResource(0);
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setBackgroundResource(0);
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setBackgroundResource(0);
            scan_shape.setImageResource(R.drawable.form_scaning);
            scan_shape.setBackgroundResource(0);
        } else if (id == R.id.menu_Document) {
            tinyDB.putString("Doc", "DocumentScanner_Doc_");
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setBackgroundResource(R.drawable.button_seagreen);
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setBackgroundResource(0);
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Whiteboard.setBackgroundResource(0);
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setBackgroundResource(0);
            scan_shape.setImageResource(R.drawable.scan_doc);
            scan_shape.setBackgroundResource(0);
        } else if (id == R.id.menu_BusinessCard) {
            /*tinyDB.putString("Doc", "DocumentScanner_BC_");*/
            menu_BusinessCard.setTextColor(this.getResources().getColor(R.color.white));
            menu_BusinessCard.setBackgroundResource(R.drawable.button_seagreen);
            menu_Form.setTextColor(this.getResources().getColor(R.color.white));
            menu_Form.setBackgroundResource(0);
            menu_Document.setTextColor(this.getResources().getColor(R.color.white));
            menu_Document.setBackgroundResource(0);
            menu_Whiteboard.setTextColor(this.getResources().getColor(R.color.white));
            menu_Whiteboard.setBackgroundResource(0);
            scan_shape.setImageResource(R.drawable.scan_business);
            scan_shape.setBackgroundResource(0);


        } else if (id == R.id.btn_single) {
            arrayListfile.clear();
            sample_iv.setImageDrawable(null);
            layoutSingle.setBackgroundResource(R.color.colorRed);
            layoutMulti.setBackgroundResource(R.color.white);
            btn_single.setTextColor(this.getResources().getColor(R.color.white));
            btn_multi.setTextColor(this.getResources().getColor(R.color.colorRed));
            btn_done_mutli.setVisibility(View.GONE);
            tv_images_count.setVisibility(View.GONE);
            tv_images_count.setText("");
        } else if (id == R.id.btn_multi) {
            arrayListfile.clear();
            sample_iv.setImageDrawable(null);
            layoutSingle.setBackgroundResource(R.color.white);
            layoutMulti.setBackgroundResource(R.color.colorRed);
            btn_single.setTextColor(this.getResources().getColor(R.color.colorRed));
            btn_multi.setTextColor(this.getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onBackPressed() {
        if (fromMulti != 0) {
            showSaveDialog(this, "");
        } else {
            startActivity(MainActivity.class, null);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        scanlayout.setVisibility(View.GONE);
    }
}