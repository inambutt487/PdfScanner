package me.sid.smartcropper.views.activities;

import static me.sid.smartcropper.utils.Constants.pdfExtension;
import static me.sid.smartcropper.utils.Constants.trashfolderDirectory;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.ImportAdapter;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.ImageToPdfCallback;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.StringUtils;

public class TranslatePDFActivity extends BaseActivity implements View.OnClickListener, GenericCallback, ImageToPdfCallback, ImportAdapter.AdapterInterface {

    private ImageView backimg, premiumImg;

    RecyclerView filesRecyclerView;
    ImportAdapter filesAdapter;
    ArrayList<FileInfoModel> pdfArrayList;


    DirectoryUtils mDirectoryUtils;
    RelativeLayout noFileLayout;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_pdfactivity);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("PDF Translation");

        backimg = findViewById(R.id.backimg);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        if(!havePurchase()) {
            premiumImg = findViewById(R.id.premiumImg);
            premiumImg.setVisibility(View.VISIBLE);
            premiumImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!havePurchase()) {
                        if (!haveNetworkConnection()) {
                            StringUtils.getInstance().showSnackbar(TranslatePDFActivity.this, "No Internet Connection!");
                        } else {
                            startActivity(new Intent(TranslatePDFActivity.this, PremiumActivity.class));
                        }
                    } else {
                        StringUtils.getInstance().showSnackbar(TranslatePDFActivity.this, "Already Purchase!");
                    }
                }
            });
        }


        mDirectoryUtils = new DirectoryUtils(this);
        pdfArrayList = new ArrayList<>();

        noFileLayout = findViewById(R.id.noFileLayout);
        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        filesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        /*filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));*/
        filesAdapter = new ImportAdapter(TranslatePDFActivity.this, pdfArrayList, this, this, true);
        filesRecyclerView.setAdapter(filesAdapter);
        filesAdapter.setCallback(this, this);

        getAllFiles();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.isEmpty() || newText == null){
                    getAllFiles();
                }else{
                    filesAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });


        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getAllFiles();
                return false;
            }
        };

    }

    private void loadData() {

        pdfArrayList.clear();
        mDirectoryUtils = new DirectoryUtils(this);
    }

    void getAllFiles() {
        pdfArrayList = new ArrayList<>();
        pdfArrayList.clear();

        if(temp_importArrayList.size()> 0){
            pdfArrayList = temp_importArrayList;

            filesAdapter = new ImportAdapter(TranslatePDFActivity.this, pdfArrayList,TranslatePDFActivity.this,TranslatePDFActivity.this, true);
            filesAdapter.setCallback(TranslatePDFActivity.this,TranslatePDFActivity.this);
            filesRecyclerView.setAdapter(filesAdapter);

        }else{
            mDirectoryUtils.clearSelectedArray();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // Do the processing.
                    getSelectedFiles(Environment.getExternalStorageDirectory(), pdfExtension);
                }
            }).start();
        }


    }


    public void getSelectedFiles(File file, String type) {
        File FileList[] = file.listFiles();
        String[] types = type.split(",");
        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {
//                if (!FileList[i].getAbsolutePath().equals("/storage/emulated/0/Doc Scanner/Trash"))
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

                            if (!pdfArrayList.isEmpty()) {

                                Collections.sort(pdfArrayList, new Comparator<FileInfoModel>() {
                                    @Override
                                    public int compare(FileInfoModel fileInfoModel, FileInfoModel t1) {
                                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());
                                    }
                                });
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!pdfArrayList.isEmpty()) {

                                        temp_importArrayList = pdfArrayList;
//                                        noFileLayout.setVisibility(View.GONE);
                                        filesAdapter = new ImportAdapter(TranslatePDFActivity.this, pdfArrayList,TranslatePDFActivity.this,TranslatePDFActivity.this, true);
                                        filesAdapter.setCallback(TranslatePDFActivity.this,TranslatePDFActivity.this);
                                        filesRecyclerView.setAdapter(filesAdapter);
                                    }
                                }
                            });

//                            filesRecyclerView.setAdapter(filesAdapter);
//                            selectedFiles.add(FileList[i]);

                        }
                    }
                }
            }
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void callback(Object o) {

    }

    @Override
    public void imageToPdfCallback(Uri uri, String fileName) {

    }

    @Override
    public void renamed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        temp_importArrayList.clear();
    }
}