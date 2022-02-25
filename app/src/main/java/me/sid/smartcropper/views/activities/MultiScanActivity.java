package me.sid.smartcropper.views.activities;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.MultiFilesAdapter;
import me.sid.smartcropper.dialogs.SaveMultiFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.StringUtils;
import me.sid.smartcropper.views.FloatButton.OptionsFabLayout;

public class MultiScanActivity extends BaseActivity implements OnPDFCreatedInterface {

    private ImageView backimg, premiumImg;

    public Button btn_done;
    RecyclerView multi_imgs_rv;
    MultiFilesAdapter adapter;

    ProgressDialog dialog;
    Boolean captureAgain = false;
    ArrayList<String> imagesUri;

    private ImageToPDFOptions mPdfOptions;
    private OptionsFabLayout fabWithOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_multi_scan);

        backimg = findViewById(R.id.backimg);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        premiumImg = findViewById(R.id.premiumImg);
        if(!havePurchase()) {
            premiumImg.setVisibility(View.VISIBLE);
            premiumImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!havePurchase()) {
                        if (!haveNetworkConnection()) {
                            StringUtils.getInstance().showSnackbar(MultiScanActivity.this, "No Internet Connection!");
                        } else {
                            startActivity(new Intent(MultiScanActivity.this, PremiumActivity.class));
                        }
                    } else {
                        StringUtils.getInstance().showSnackbar(MultiScanActivity.this, "Already Purchase!");
                    }
                }
            });
        }

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("SAVE PDF");

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        fabWithOptions = (OptionsFabLayout) findViewById(R.id.fab_l);

        dialog = new ProgressDialog(MultiScanActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Creating pdf file");
        dialog.setCancelable(false);

        mPdfOptions = new ImageToPDFOptions();
        imagesUri = new ArrayList<>();


        btn_done = findViewById(R.id.btn_done);
        multi_imgs_rv = findViewById(R.id.multi_imgs_rv);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        multi_imgs_rv.setLayoutManager(mLayoutManager);


         adapter = new MultiFilesAdapter(mutliCreatedArrayBitmap, MultiScanActivity.this);
        multi_imgs_rv.setAdapter(adapter);


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFile_forMulti();
            }
        });

        fabWithOptions.setMiniFabsColors(
                R.color.colorPrimary,
                R.color.colorPrimary);
        fabWithOptions.setMainFabOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabWithOptions.closeOptionsMenu();
            }
        });

        //Set mini fabs clicklisteners.
        fabWithOptions.setMiniFabSelectedListener(new OptionsFabLayout.OnMiniFabSelectedListener() {
            @Override
            public void onMiniFabSelected(MenuItem fabItem) {
                switch (fabItem.getItemId()) {
                    case R.id.fab_gallery:
                        gotoCamera(1);
                        break;
                    case R.id.fab_camera:
                        gotoCamera(2);
                    default:
                        break;
                }
            }
        });

    }

    void gotoCamera(int flowId) {
        captureAgain = true;
        Intent intent = new Intent(MultiScanActivity.this, GernalCameraActivity.class);
        intent.putExtra("fromMulti", flowId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        showSaveDialog(this,"");
    }


    private void createFile_forMulti() {
        new SaveMultiFileDialog(MultiScanActivity.this, new GenericCallback() {
            @Override
            public void callback(Object o) {
                String name = (String) o;
                if (!name.equals("")) {
                    createImgToPDF(name);
                } else {
                    Toast.makeText(MultiScanActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                }
            }

        }).show();


    }

    private void createImgToPDF(String fileName) {

        for (int i = 0; i < mutliCreatedArrayBitmap.size(); i++) {
            imagesUri.add(creatTempImg(mutliCreatedArrayBitmap.get(i), i));
        }

        mPdfOptions.setImagesUri(imagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setPageNumStyle(PG_NUM_STYLE_PAGE_X_OF_N);
        mPdfOptions.setPageColor(DEFAULT_PAGE_COLOR);
        mPdfOptions.setMargins(20, 20, 20, 20);
        mPdfOptions.setOutFileName(fileName);

        //Multiple PDF
        new CreatePdfAsync(mPdfOptions, new File(Environment.getExternalStorageDirectory().toString(), folderDirectory).getPath(), MultiScanActivity.this).execute();

    }


    @Override
    public void onPDFCreationStarted() {
        dialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, final String path) {
        dialog.dismiss();
        if (success) {
            mutliCreatedArrayBitmap.clear();
            imagesUri.clear();
            StringUtils.getInstance().showSnackbar(MultiScanActivity.this, getString(R.string.created_success));
            startActivity(DocumentsActivity.class, null);
            finish();
        } else {
            StringUtils.getInstance().showSnackbar(MultiScanActivity.this, getString(R.string.convert_error));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!captureAgain) {
            mutliCreatedArrayBitmap.clear();
        }
     }
}