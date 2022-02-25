package me.sid.smartcropper.views.activities;

import static me.sid.smartcropper.utils.Constants.DEFAULT_PAGE_COLOR;
import static me.sid.smartcropper.utils.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static me.sid.smartcropper.utils.Constants.PG_NUM_STYLE_PAGE_X_OF_N;
import static me.sid.smartcropper.utils.Constants.folderDirectory;
import static me.sid.smartcropper.utils.Constants.trashfolderDirectory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.dialogs.CreateFolderDialog;
import me.sid.smartcropper.interfaces.FolderMenuCallback;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.ImageToPdfCallback;
import me.sid.smartcropper.interfaces.OnPDFCreatedInterface;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.models.TabEntity;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.CreatePdfAsync;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageToPDFOptions;
import me.sid.smartcropper.utils.PDFUtils;
import me.sid.smartcropper.utils.PageSizeUtils;
import me.sid.smartcropper.utils.PermissionUtils;
import me.sid.smartcropper.utils.RemoteKeysPdf;
import me.sid.smartcropper.utils.StringUtils;
import me.sid.smartcropper.views.Tablayout.CommonTabLayout;
import me.sid.smartcropper.views.Tablayout.Listtener.CustomTabEntity;
import me.sid.smartcropper.views.Tablayout.Listtener.OnTabSelectListener;

public class DocumentsActivity extends BaseActivity implements View.OnClickListener
        , GenericCallback, ImageToPdfCallback, FolderMenuCallback, OnPDFCreatedInterface, View.OnTouchListener, AllFilesAdapter.AdapterInterface {


//    SingleSelectToggleGroup singleSelectToggleGroup;

    RecyclerView filesRecyclerView;
    TextView filterTv, emptyView, countTv;
    LottieAnimationView btn_goToCamera;
    //    ArrayList<FileInfoModel> fileInfoModelArrayList;
    ArrayList<FileInfoModel> pdfArrayList;
    ArrayList<FileInfoModel> scannedArrayList;
    ArrayList<File> foldersArray;
    AllFilesAdapter filesAdapter;
    RelativeLayout noFileLayout;
    DirectoryUtils mDirectoryUtils;
    ProgressDialog dialog;
    //    AllFolderAdapter adapter;
    private int mChecked;

    ConstraintLayout adlayout;
    View adlayout2;
    //
    ImageToPDFOptions mPdfOptions;

    CommonTabLayout tabLayout;

    View toolbar;
    ImageView search, menu, premium, sort;

    boolean homeThread = true;
    boolean pdfThread = false;

    boolean status = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        init();
    }


    private void init() {

        tabLayout = findViewById(R.id.tabLayout);
        toolbar = findViewById(R.id.toolbar);
        search = toolbar.findViewById(R.id.searchImg);
        menu = toolbar.findViewById(R.id.menuImg);
        premium = toolbar.findViewById(R.id.premiumImg);
        sort = toolbar.findViewById(R.id.sortImg);
        sort.setOnClickListener(this);
        premium.setVisibility(View.GONE);

        search.setOnClickListener(this);
        sort.setVisibility(View.VISIBLE);
        menu.setOnClickListener(this);
        premium.setOnClickListener(this);


        foldersArray = new ArrayList<>();
        scannedArrayList = new ArrayList<>();
        pdfArrayList = new ArrayList<>();
        mDirectoryUtils = new DirectoryUtils(this);

        dialog = new ProgressDialog(DocumentsActivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading pdf file");
        dialog.setCancelable(false);


        noFileLayout = findViewById(R.id.noFileLayout);
//        new_folder_btn = findViewById(R.id.new_folder_btn);


        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        countTv = findViewById(R.id.countTv);

        temp_scannedArrayList = scannedArrayList;
        filesAdapter = new AllFilesAdapter(DocumentsActivity.this, scannedArrayList, this, this, false);
        filesAdapter.setCallback(this, this);
//        countTv.setText(String.valueOf(fileInfoModelArrayList.size()));
        filesRecyclerView.setAdapter(filesAdapter);

        filterTv = findViewById(R.id.filterTv);
        filterTv.setOnClickListener(this);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setOnClickListener(this);

        btn_goToCamera = findViewById(R.id.btn_goToCamera);
        btn_goToCamera.loop(true);
        btn_goToCamera.playAnimation();
        croppedArrayBitmap.clear();
        mutliCreatedArrayBitmap.clear();

        btn_goToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RemoteKeysPdf.INSTANCE.setThreeCickCount(RemoteKeysPdf.INSTANCE.getThreeCickCount() + 1);

                startActivity(GernalCameraActivity.class, null);


            }
        });

        adlayout2 = findViewById(R.id.adlayout2);

        adlayout = findViewById(R.id.adlayout);
        if (!isConnected()) {
            adlayout.setVisibility(View.GONE);
        }

        adlayout.setVisibility(View.GONE);
        adlayout2.setVisibility(View.GONE);

        mPdfOptions = new ImageToPDFOptions();

        addTabs();
    }

    void addTabs() {
        ArrayList<CustomTabEntity> list = new ArrayList<>();
        list.add(new TabEntity("Scan Documents", R.drawable.scan_red, R.drawable.scan_gray));
        list.add(new TabEntity("PDF Documents", R.drawable.ic_pdf_file_selected, R.drawable.ic_pdf_file));
        tabLayout.setTabData(list);

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(20);
        linearLayout.setDividerDrawable(drawable);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                checkKeyboard();

                if (PermissionUtils.hasPermissionGranted(DocumentsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})) {
                    getAllFiles();

                } else {
                    PermissionUtils.checkAndRequestPermissions(DocumentsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_EXTERNAL_STORAGE);
                }

            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    void getAllFiles() {

        if (tabLayout.getCurrentTab() == 0) {


            homeThread = true;
            pdfThread = false;

            mDirectoryUtils.clearFilterArray();
            scannedArrayList = new ArrayList<>();
            scannedArrayList.clear();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            countTv.setText("0");
                        }
                    });
                    // Do the processing.

                    if(temp_scannedArrayList.size() >0){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mDirectoryUtils.clearFilterArray();

                                scannedArrayList = temp_scannedArrayList;
                                filesAdapter = new AllFilesAdapter(DocumentsActivity.this, scannedArrayList, DocumentsActivity.this, DocumentsActivity.this, false);
                                filesAdapter.setCallback(DocumentsActivity.this::callback, DocumentsActivity.this::imageToPdfCallback);
                                filesRecyclerView.setAdapter(filesAdapter);
                                countTv.setText(String.valueOf(scannedArrayList.size()));
                                filesAdapter.setCallback(DocumentsActivity.this, DocumentsActivity.this);
                                noFileLayout.setVisibility(View.GONE);

                                filesAdapter.setStatus(true);

                            }
                        });

                    }else{
                        getFiles();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }).start();
        } else {
            homeThread = false;
            pdfThread = true;

            mDirectoryUtils.clearSelectedArray();


            pdfArrayList = new ArrayList<>();
            pdfArrayList.clear();


            new Thread(new Runnable() {

                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                            countTv.setText("0");
                        }
                    });

                    // Do the processing.

                    if (temp_pdfArrayList.size()>0){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDirectoryUtils.clearFilterArray();

                                pdfArrayList = temp_pdfArrayList;
                                noFileLayout.setVisibility(View.GONE);
                                filesAdapter = new AllFilesAdapter(DocumentsActivity.this, pdfArrayList, DocumentsActivity.this, DocumentsActivity.this, false);
                                filesAdapter.setCallback(DocumentsActivity.this, DocumentsActivity.this);
                                filesRecyclerView.setAdapter(filesAdapter);
                                countTv.setText(String.valueOf(pdfArrayList.size()));

                                filesAdapter.setStatus(true);
                            }
                        });


                    }else{
                        getSelectedFiles(Environment.getExternalStorageDirectory(), Constants.pdfExtension);
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }).start();

        }

    }


    public void getSelectedFiles(File file, String type) {
        if (pdfThread) {


            scannedArrayList = new ArrayList<>();
            scannedArrayList.clear();

            File FileList[] = file.listFiles();
            String[] types = type.split(",");
            if (FileList != null) {
                for (int i = 0; i < FileList.length; i++) {

                    if (!FileList[i].getAbsolutePath().equals(Environment.getExternalStorageDirectory() + trashfolderDirectory)) {
                        if (FileList[i].isDirectory()) {
                            getSelectedFiles(FileList[i], type);
                        } else {
                            if (FileList[i].getName().endsWith(types[0]) || FileList[i].getName().endsWith(types[0])) {


                                //here you have that file.
                                File newFile = FileList[i];
                                String[] fileInfo = newFile.getName().split("\\.");
                                if (fileInfo.length == 2)
                                    pdfArrayList.add(new FileInfoModel(fileInfo[0], fileInfo[1], newFile));
                                else {

                                    pdfArrayList.add(new FileInfoModel(fileInfo[0],
                                            newFile.getAbsolutePath().substring(newFile.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                                            newFile));
                                }

                                sortArray(5);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!pdfArrayList.isEmpty()) {

                                            temp_pdfArrayList = pdfArrayList;
                                            noFileLayout.setVisibility(View.GONE);
                                            filesAdapter = new AllFilesAdapter(DocumentsActivity.this, pdfArrayList, DocumentsActivity.this, DocumentsActivity.this, false);
                                            filesAdapter.setCallback(DocumentsActivity.this, DocumentsActivity.this);
                                            filesRecyclerView.setAdapter(filesAdapter);
                                            countTv.setText(String.valueOf(pdfArrayList.size()));
                                            Log.d("newcount", String.valueOf(pdfArrayList.size()) + "   pdfff");
                                        } else {
                                            countTv.setText("0");
                                            Log.d("newcount", "0tab2");
                                        }
                                    }
                                });

                            }
                        }
                    }
                }

            }

            if (!pdfArrayList.isEmpty()) {
                status = true;
            }
        }
    }

    private void getFiles() {
        if (homeThread) {

            pdfArrayList = new ArrayList<>();
            pdfArrayList.clear();

            ArrayList<File> arrayList = mDirectoryUtils.searchDir(new File(Environment.getExternalStorageDirectory(), folderDirectory));
            mDirectoryUtils.clearSelectedArray();

            if (arrayList != null && arrayList.size() > 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.show();
                    }
                });
                for (File file : arrayList) {

                    String[] fileInfo = file.getName().split("\\.");
                    if (fileInfo.length == 2)
                        scannedArrayList.add(new FileInfoModel(fileInfo[0], fileInfo[1], file));
                    else {
                        scannedArrayList.add(new FileInfoModel(fileInfo[0],
                                file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                                file));
                    }

                    sortArray(5);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        temp_scannedArrayList = scannedArrayList;
                        filesAdapter = new AllFilesAdapter(DocumentsActivity.this, scannedArrayList, DocumentsActivity.this, DocumentsActivity.this, false);
                        filesAdapter.setCallback(DocumentsActivity.this::callback, DocumentsActivity.this::imageToPdfCallback);
                        filesRecyclerView.setAdapter(filesAdapter);
                        countTv.setText(String.valueOf(scannedArrayList.size()));
                        filesAdapter.setCallback(DocumentsActivity.this, DocumentsActivity.this);
                        noFileLayout.setVisibility(View.GONE);

                    }
                });


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        dialog.show();

                        if (filesAdapter != null) {
//                        filesRecyclerView.setAdapter(filesAdapter);
                            filesAdapter.updateList(new ArrayList<FileInfoModel>());
                        }
                        countTv.setText("0");
                        Log.d("newcount", "0tab1");
                        noFileLayout.setVisibility(View.VISIBLE);
                    }
                });

            }


        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        PDFUtils.hideKeyboard(this);
        if (tabLayout.getCurrentTab() == 0) {
            status = false;
            scannedArrayList = new ArrayList<>();
            scannedArrayList.clear();
            mDirectoryUtils.clearFilterArray();
            getFiles();
        }
    }

    public void hideAdLayout() {
        adlayout.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.menuImg:
                onBackPressed();
                break;
            case R.id.searchImg:
                Intent searchintent = new Intent(DocumentsActivity.this, SearchActivity.class);
                startActivity(searchintent);
                break;
            case R.id.premiumImg:

                if(!havePurchase()){
                    if(!haveNetworkConnection()){
                        StringUtils.getInstance().showSnackbar(DocumentsActivity.this, "No Internet Connection!");
                    }else{
                        startActivity(new Intent(DocumentsActivity.this, PremiumActivity.class));
                    }
                }else{
                    StringUtils.getInstance().showSnackbar(DocumentsActivity.this, "Already Purchase!");
                }

                break;
            case R.id.sortImg:
                if(filesAdapter.getStatus()){
                    showSortMenu();
                }


                break;
            case R.id.filterTv:
                if(filesAdapter.getStatus()){
                    showSortMenu();
                }
                break;
        }

    }



    @Override
    public void callback(Object o) {
        if (o instanceof String) {
            Log.d("newcount", "callback");
            countTv.setText((String) o);


        } else if (o instanceof Integer) {
            if ((Integer) o == 2) {

                if(pdfArrayList.size()>0){
                    tabLayout.setCurrentTab(1);
                    getAllFiles();
                }else{
                    tabLayout.setCurrentTab(0);
                    getAllFiles();
                }

            } else {
                if ((Integer) o == 0) {
                    noFileLayout.setVisibility(View.VISIBLE);
                } else {
                    noFileLayout.setVisibility(View.GONE);
                }
            }
        } else if (o instanceof File) {
            Intent intent = new Intent(this, AllFilesInFolderActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("path", ((File) o).getAbsolutePath());
            Objects.requireNonNull(this).startActivity(intent);
        } else if (o instanceof Integer) {
            getAllFiles();
        }

    }

    private void createImgToPDF(String fileName, ArrayList<String> imagesUri) {
        showRenameDialog(fileName, imagesUri);
    }

    private void showRenameDialog(String fileName, ArrayList<String> imagesUri) {
        CreateFolderDialog dialog = new CreateFolderDialog(DocumentsActivity.this, new GenericCallback() {
            @Override
            public void callback(Object o) {
                String newFileName = (String) o;

                mPdfOptions.setImagesUri(imagesUri);
                mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
                mPdfOptions.setImageScaleType(IMAGE_SCALE_TYPE_ASPECT_RATIO);
                mPdfOptions.setPageNumStyle(PG_NUM_STYLE_PAGE_X_OF_N);
                mPdfOptions.setPageColor(DEFAULT_PAGE_COLOR);
                mPdfOptions.setMargins(20, 20, 20, 20);
                mPdfOptions.setOutFileName(fileName);

                //Create PDF
                new CreatePdfAsync(mPdfOptions, new File(Environment.getExternalStorageDirectory().toString(), folderDirectory).getPath(), DocumentsActivity.this, newFileName).execute();
            }
        }, DocumentsActivity.this);
        dialog.setSaveBtn("Create File");
        dialog.setTitle("Enter New File Name");
        dialog.show();
    }

    public void checkKeyboard() {


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            hideAdLayout();
        } else {

        }
    }


    private void showSortMenu() {

        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        final PopupMenu menu = new PopupMenu(wrapper, sort, Gravity.END);
        menu.inflate(R.menu.sortby_menu);
        try {
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.nameTv) {// name
                        sortArray(6);
                        menu.dismiss();
                        return true;
                    } else if (menuItem.getItemId() == R.id.timeTv) {//
                        sortArray(5);
                        menu.dismiss();
                        return true;
                    } else if (menuItem.getItemId() == R.id.createDateTv) { //create date
                        sortArray(4);
                        menu.dismiss();
                        return true;
                    } else if (menuItem.getItemId() == R.id.zToATv) { // z to a
                        sortArray(3);
                        return true;
                    } else if (menuItem.getItemId() == R.id.sizeTv) { //by size
                        sortArray(2);
                        menu.dismiss();
                        return true;
                    } else if (menuItem.getItemId() == R.id.aTozTv) { //a to z
                        sortArray(1);
                        menu.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            menu.show();


        } catch (Exception e) {
            Log.d("getMessage", e.getMessage());
        }
    }

    public void sortArray(final int sortBy) {

        if (tabLayout.getCurrentTab() == 0) {

            Collections.sort(scannedArrayList, new Comparator<FileInfoModel>() {
                @Override
                public int compare(FileInfoModel fileInfoModel, FileInfoModel t1) {
                    if (sortBy == 1)
                        return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());//A to Z
                    else if (sortBy == 2)
                        return Long.compare(fileInfoModel.getFile().length(), t1.getFile().length());//File size
                    else if (sortBy == 3)
                        return t1.getFileName().compareToIgnoreCase(fileInfoModel.getFileName());//Z to A
                    else if (sortBy == 4)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//Create Date By
                    else if (sortBy == 5)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//Recent updated Date By
                    else if (sortBy == 6)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//By name

                    return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());

                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    temp_scannedArrayList = scannedArrayList;
                    filesAdapter = new AllFilesAdapter(DocumentsActivity.this, scannedArrayList, DocumentsActivity.this, DocumentsActivity.this, false);
                    filesAdapter.setCallback(DocumentsActivity.this, DocumentsActivity.this);
                    filesRecyclerView.setAdapter(filesAdapter);
                    filesAdapter.setStatus(true);
                }
            });


        } else {

            Collections.sort(pdfArrayList, new Comparator<FileInfoModel>() {
                @Override
                public int compare(FileInfoModel fileInfoModel, FileInfoModel t1) {
                    if (sortBy == 1)
                        return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());//A to Z
                    else if (sortBy == 2)
                        return Long.compare(fileInfoModel.getFile().length(), t1.getFile().length());//File size
                    else if (sortBy == 3)
                        return t1.getFileName().compareToIgnoreCase(fileInfoModel.getFileName());//Z to A
                    else if (sortBy == 4)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//Create Date By
                    else if (sortBy == 5)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//Recent updated Date By
                    else if (sortBy == 6)
                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());//By name

                    return fileInfoModel.getFileName().compareToIgnoreCase(t1.getFileName());

                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    temp_pdfArrayList = pdfArrayList;
                    filesAdapter = new AllFilesAdapter(DocumentsActivity.this, pdfArrayList, DocumentsActivity.this, DocumentsActivity.this, false);
                    filesRecyclerView.setAdapter(filesAdapter);
                    filesAdapter.setStatus(true);
                }
            });


        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DocumentsActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onPDFCreationStarted() {

    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        // File f=new File(path);
        // fileInfoModelArrayList.add(new FileInfoModel("newly created","pdf",f));
        //getFiles();
        mDirectoryUtils.clearFilterArray();
        getFiles();
        sortArray(5);
        tabLayout.setCurrentTab(0);


    }

    @Override
    public void imageToPdfCallback(Uri uri, String fileName) {
        ArrayList<String> imageUri = new ArrayList<String>();
        imageUri.add(uri.toString());
        createImgToPDF(fileName, imageUri);
    }

    @Override
    public void renameCallback(String newName) {

//        tabYourCreatedFolder();
    }

    @Override
    public void deleteCallback(int position) {
        File file = foldersArray.get(position);

        if (file.listFiles().length == 0) {
            if (file.exists())
                file.delete();
        } else {
            for (File tempFile : file.listFiles()) {
                if (tempFile.exists())
                    tempFile.delete();
            }
            file.delete();
        }

        //
        foldersArray.remove(position);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void renamed() {

        temp_pdfArrayList.clear();
        temp_scannedArrayList.clear();
        getAllFiles();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        temp_pdfArrayList.clear();
        temp_scannedArrayList.clear();
    }
}