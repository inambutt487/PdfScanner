package me.sid.smartcropper.views.activities;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.FilterViewAdapter;
import me.sid.smartcropper.dialogs.AlertDialogHelper;
import me.sid.smartcropper.dialogs.SaveFileDialog;
import me.sid.smartcropper.interfaces.CreateCallback;
import me.sid.smartcropper.interfaces.FilterListener;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.StringUtils;
import me.sid.smartcropper.views.SignatureEditingFragments.SignatureDialogFragment;
import me.sid.smartcropper.views.photoEditingFragments.TextEditorDialogFragment;

public class EditActivity extends BaseActivity implements OnPDFCreatedInterface, OnPhotoEditorListener, FilterListener, View.OnClickListener {

    private Context mContext;

    ArrayList<String> imagesUri;
    ImageToPDFOptions mPdfOptions;
    ProgressDialog dialog;

    private ArrayList<Bitmap> mCroppedarrayList;
    DirectoryUtils mDirectoryUtils;

    int angle = 0;
    SaveSettings saveSettings = new SaveSettings.Builder()
            .setClearViewsEnabled(true)
            .setTransparencyEnabled(true)
            .build();

    LinearLayout menu_undo, menu_delete, menu_redo, menu_rotate, menu_addText, menu_write, menu_calendar, menu_ocr;
    Button btn_done;
    PhotoEditorView ivCrop;

    TextView tv_filter, tv_edited_count, btn_retake;

    private List<Pair<String, PhotoFilter>> mPairList = new ArrayList<>();
    RecyclerView thumbListView;
    FilterViewAdapter mFilterViewAdapter;

    ImageView forward, back;

    private PhotoEditor mPhotoEditor;

    private Boolean showSign = true;
    CardView rl_signature;

    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar = Calendar.getInstance();

    private int pos = 0;
    private int total;

    private Boolean swipe = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = EditActivity.this;
        makeFullScreen();
        setContentView(R.layout.activity_stich);


        setupFilters();


        mPdfOptions = new ImageToPDFOptions();
        imagesUri = new ArrayList<>();

        dialog = new ProgressDialog(EditActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating pdf file");
        dialog.setCancelable(false);

        mCroppedarrayList = croppedArrayBitmap;
        total = mCroppedarrayList.size();
        mDirectoryUtils = new DirectoryUtils(EditActivity.this);

        rl_signature = (CardView) findViewById(R.id.rl_signature);

        ivCrop = findViewById(R.id.iv_crop);
        ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
        mPhotoEditor = new PhotoEditor.Builder(EditActivity.this, ivCrop).setPinchTextScalable(true).build();

        mPhotoEditor.setOnPhotoEditorListener(this);
        mPhotoEditor.setBrushSize(7);

        forward = findViewById(R.id.forward);
        back = findViewById(R.id.back);

        if (mCroppedarrayList.size() == 1) {
            forward.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
        }


        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        menu_delete = findViewById(R.id.menu_delete);
        menu_delete.setOnClickListener(this);
        menu_redo = findViewById(R.id.menu_redo);
        menu_redo.setOnClickListener(this);
        menu_undo = findViewById(R.id.menu_undo);
        menu_undo.setOnClickListener(this);
        menu_rotate = findViewById(R.id.menu_rotate);
        menu_rotate.setOnClickListener(this);
        menu_addText = findViewById(R.id.menu_addText);
        menu_addText.setOnClickListener(this);
        menu_write = findViewById(R.id.menu_write);
        menu_write.setOnClickListener(this);
        menu_calendar = findViewById(R.id.menu_calendar);
        menu_calendar.setOnClickListener(this);
        menu_ocr = findViewById(R.id.menu_ocr);
        menu_ocr.setOnClickListener(this);
        btn_retake = findViewById(R.id.btn_retake);
        btn_retake.setOnClickListener(this);
        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);

        tv_filter = findViewById(R.id.tv_filter);
        tv_filter.setOnClickListener(this);

        tv_edited_count = findViewById(R.id.tv_edited_count);
        tv_edited_count.setOnClickListener(this);

        updateCount(pos);

        thumbListView = findViewById(R.id.rv_filters);
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);

        mFilterViewAdapter = new FilterViewAdapter(this, mPairList, mCroppedarrayList.get(0));
        thumbListView.setAdapter(mFilterViewAdapter);
        mFilterViewAdapter.notifyDataSetChanged();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(mPhotoEditor);
            }

        };


    }

    private void updateCount(int position) {
        total = mCroppedarrayList.size();
        position++;
        tv_edited_count.setText("Page " + position + "/" + total);
    }

    public void updateLabel(PhotoEditor photoEditor) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(ContextCompat.getColor(EditActivity.this, R.color.black));

        photoEditor.addText(sdf.format(myCalendar.getTime()), styleBuilder);
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show((AppCompatActivity) mContext, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {

                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);
                mPhotoEditor.editText(rootView, inputText, styleBuilder);


            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onTouchSourceImage(MotionEvent event) {

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.menu_undo) {

            mPhotoEditor.undo();

        }

        if (view.getId() == R.id.menu_addText) {

            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show((AppCompatActivity) mContext);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {

                    try {

                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);

                    } catch (Exception e) {

                    }

                }
            });

        }

        if (view.getId() == R.id.menu_write) {

            SignatureDialogFragment signatureDialogFragment = SignatureDialogFragment.show((AppCompatActivity) mContext);
            signatureDialogFragment.setOnSignatureListener(new SignatureDialogFragment.SignatureEditor() {
                @Override
                public void onDone(Bitmap bitmap) {
                    mPhotoEditor.addImage(bitmap);
                }
            });

        }

        if (view.getId() == R.id.menu_calendar) {
            try {
                new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            } catch (Exception e) {

            }

        }

        if (view.getId() == R.id.menu_ocr) {

            Intent intent = new Intent(mContext, OCRActivity.class);
            mContext.startActivity(intent);
        }

        if (view.getId() == R.id.menu_redo) {
            try {
                mPhotoEditor.redo();
            } catch (Exception e) {

            }

        }

        if (view.getId() == R.id.btn_retake) {
            croppedArrayBitmap.clear();
            mutliCreatedArrayBitmap.clear();
            Intent intent = new Intent(mContext, GernalCameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);

        }

        if (view.getId() == R.id.back) {
            try {
                if(mCroppedarrayList.size() > 1 && !swipe){
                    swipe = true;
                    if(pos > 0){
                        pos--;
                        mPhotoEditor.clearAllViews();
                        ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
                        updateCount(pos);
                        mFilterViewAdapter.updateFilter(mCroppedarrayList.get(pos));
                    }
                    swipe = false;
                }

            }catch (Exception e){
                Log.d("CrashApp", "error: "+ e.getMessage()+ " position"+ pos);
            }
        }

        if (view.getId() == R.id.forward) {
            try {
                if(mCroppedarrayList.size() > 1 && !swipe){
                    swipe = true;
                    if(pos + 1 < mCroppedarrayList.size()){
                        pos++;
                        mPhotoEditor.clearAllViews();
                        ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
                        updateCount(pos);
                        mFilterViewAdapter.updateFilter(mCroppedarrayList.get(pos));
                    }
                    swipe = false;
                }

            }catch (Exception e){
                Log.d("CrashApp", "error: "+ e.getMessage()+ " position"+ pos);
            }
        }

        if (view.getId() == R.id.menu_rotate) {
            angle = 90;
            Matrix matrix = new Matrix();
            matrix.postRotate(ivCrop.getRotation() + angle);
            Bitmap editedBitmap = mCroppedarrayList.get(pos);
            editedBitmap = Bitmap.createBitmap(editedBitmap, 0, 0, editedBitmap.getWidth(), editedBitmap.getHeight(), matrix, true);
            mCroppedarrayList.set(pos, editedBitmap);
            ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
        }

        if (view.getId() == R.id.menu_delete) {

            AlertDialogHelper.showAlert(mContext, new AlertDialogHelper.Callback() {
                @Override
                public void onSucess(int t) {
                    if (t == 0) {

                        try {

                            if(mCroppedarrayList.size()> 1 ){

                                if (pos + 1 == mCroppedarrayList.size()) {
                                    //left
                                    mCroppedarrayList.remove(pos);
                                    total = mCroppedarrayList.size();
                                    pos--;
                                    ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
                                    updateCount(pos);
                                    mFilterViewAdapter.updateFilter(mCroppedarrayList.get(pos));

                                } else {
                                    //Right
                                    mCroppedarrayList.remove(pos);
                                    total = mCroppedarrayList.size();
                                    ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
                                    updateCount(pos);
                                    mFilterViewAdapter.updateFilter(mCroppedarrayList.get(pos));
                                }

                            }else{
                                Intent intent = new Intent(mContext, GernalCameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mContext.startActivity(intent);
                            }

                        } catch (Exception e) {
                            Log.d("CrashApp", "error: "+ e.getMessage()+ " position"+ pos);
                        }

                    }
                }
            }, "Delete", "Do you  want to delete this file?");
        }

        if (view.getId() == R.id.btn_done) {

            try {
                if(mCroppedarrayList.size() > 1 ){

                    saveSetting();
                    mPhotoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
                        @Override
                        public void onBitmapReady(Bitmap saveBitmap) {
                            mCroppedarrayList.set(pos, saveBitmap);
                            pos++;
                            if(pos < mCroppedarrayList.size()){
                                mPhotoEditor.clearAllViews();
                                ivCrop.getSource().setImageBitmap(mCroppedarrayList.get(pos));
                                updateCount(pos);
                                mFilterViewAdapter.updateFilter(mCroppedarrayList.get(pos));
                            }else{
                                for (int i = 0; i < total; i++) {
                                    mutliCreatedArrayBitmap.add(mCroppedarrayList.get(i));
                                }

                                croppedArrayBitmap.clear();
                                Intent intent = new Intent(mContext, MultiScanActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mContext.startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(mContext, "Error While saving", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    saveSingleFile(mPhotoEditor);
                }

            }catch (Exception e){
                Log.d("CrashApp", "error: "+ e.getMessage()+ " position"+ pos);
            }
        }
    }

    public void saveSingleFile(PhotoEditor photoEditor) {

        photoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                createFile(saveBitmap);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Error While saving", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createFile(Bitmap img) {


        new SaveFileDialog(this, img, new CreateCallback() {
            @Override
            public void onSaveAs(int isFrom, String fileName) {


                if (isFrom == 1 && !fileName.isEmpty()) {
                    createImgFile(fileName, img);
                    Log.d("createFile", "createImgFile");
                } else if (isFrom == 2 && !fileName.isEmpty()) {
                    imagesUri.add(creatTempImg(img, 0));
                    createImgToPDF(fileName);
                    Log.d("createFile", "createImgToPDF");
                } else {
                    Log.d("createFile", "File Name Can't be Empty");
                    Toast.makeText(EditActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void createImgFile(String fileName, Bitmap img) {
        mDirectoryUtils.saveImageFile(img, fileName);
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

    private void createImgToPDF(String fileName) {
        mPdfOptions.setImagesUri(imagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setPageNumStyle(PG_NUM_STYLE_PAGE_X_OF_N);
        mPdfOptions.setPageColor(DEFAULT_PAGE_COLOR);
        mPdfOptions.setMargins(20, 20, 20, 20);
        mPdfOptions.setOutFileName(fileName);

        //Single PDF
        new CreatePdfAsync(mPdfOptions, new File(Environment.getExternalStorageDirectory().toString(), folderDirectory).getPath(), EditActivity.this).execute();
    }

    @Override
    public void onPDFCreationStarted() {

        dialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, final String path) {

        // Main Method of File Created Result
        dialog.dismiss();
        if (success) {
            imagesUri.clear();
            StringUtils.getInstance().showSnackbar(EditActivity.this, getString(R.string.created_success));

            Intent intent = new Intent(EditActivity.this, DocumentsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else {
            StringUtils.getInstance().showSnackbar(EditActivity.this, getString(R.string.convert_error));
        }
    }

    void saveFile(PhotoEditor photoEditor, int pos) {

        photoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                mutliCreatedArrayBitmap.add(saveBitmap);
                if (pos == mCroppedarrayList.size() - 1) {
                    croppedArrayBitmap.clear();
                    Intent intent = new Intent(mContext, MultiScanActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Error While saving", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void saveSetting() {
        saveSettings = new SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build();
    }

    public void setupFilters() {

            mPairList.add(new Pair<>("filters/original.jpg", PhotoFilter.NONE));
            mPairList.add(new Pair<>("filters/auto_fix.png", PhotoFilter.AUTO_FIX));
            mPairList.add(new Pair<>("filters/brightness.png", PhotoFilter.BRIGHTNESS));
            mPairList.add(new Pair<>("filters/contrast.png", PhotoFilter.CONTRAST));
            mPairList.add(new Pair<>("filters/documentary.png", PhotoFilter.DOCUMENTARY));
            mPairList.add(new Pair<>("filters/dual_tone.png", PhotoFilter.DUE_TONE));
            mPairList.add(new Pair<>("filters/fill_light.png", PhotoFilter.FILL_LIGHT));
            mPairList.add(new Pair<>("filters/fish_eye.png", PhotoFilter.FISH_EYE));
            mPairList.add(new Pair<>("filters/grain.png", PhotoFilter.GRAIN));
            mPairList.add(new Pair<>("filters/gray_scale.png", PhotoFilter.GRAY_SCALE));
            mPairList.add(new Pair<>("filters/lomish.png", PhotoFilter.LOMISH));
            mPairList.add(new Pair<>("filters/negative.png", PhotoFilter.NEGATIVE));
            mPairList.add(new Pair<>("filters/posterize.png", PhotoFilter.POSTERIZE));
            mPairList.add(new Pair<>("filters/saturate.png", PhotoFilter.SATURATE));
            mPairList.add(new Pair<>("filters/sepia.png", PhotoFilter.SEPIA));
            mPairList.add(new Pair<>("filters/sharpen.png", PhotoFilter.SHARPEN));
            mPairList.add(new Pair<>("filters/temprature.png", PhotoFilter.TEMPERATURE));
            mPairList.add(new Pair<>("filters/tint.png", PhotoFilter.TINT));
            mPairList.add(new Pair<>("filters/vignette.png", PhotoFilter.VIGNETTE));
            mPairList.add(new Pair<>("filters/cross_process.png", PhotoFilter.CROSS_PROCESS));
            mPairList.add(new Pair<>("filters/b_n_w.png", PhotoFilter.BLACK_WHITE));
            mPairList.add(new Pair<>("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL));
            mPairList.add(new Pair<>("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL));
            mPairList.add(new Pair<>("filters/rotate.png", PhotoFilter.ROTATE));

    }

}