package me.sid.smartcropper.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapter;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.ImageToPdfCallback;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.PDFUtils;

import static me.sid.smartcropper.utils.Constants.trashfolderDirectory;

public class SearchActivity extends AppCompatActivity implements GenericCallback, ImageToPdfCallback,AllFilesAdapter.AdapterInterface {


    RecyclerView filesRecyclerView;
    ArrayList<FileInfoModel> fileInfoModelArrayList;
    AllFilesAdapter filesAdapter;
    ImageView backbtn;
    EditText searchEt;
    RelativeLayout noFileLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        initViews();
        noFileLayout = findViewById(R.id.noFileLayout);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDFUtils.hideKeyboard(SearchActivity.this);
                View view = SearchActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void initViews(){
        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        searchEt=findViewById(R.id.searchEt);
        backbtn=findViewById(R.id.backBtn);
        fileInfoModelArrayList = new ArrayList<>();


        fileInfoModelArrayList.clear();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // Do the processing.
                getSelectedFiles(Environment.getExternalStorageDirectory(), Constants.pdfExtension);
            }
        }).start();


        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!searchEt.getText().toString().equals("")){
                    filter(searchEt.getText().toString());
                }else{
                    filesAdapter = new AllFilesAdapter(SearchActivity.this, fileInfoModelArrayList,SearchActivity.this,SearchActivity.this, false);
                    filesAdapter.setCallback(SearchActivity.this,SearchActivity.this);
                    filesRecyclerView.setAdapter(filesAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!searchEt.getText().toString().equals("")){
                        filter(searchEt.getText().toString());
                    }else{
                        filesAdapter = new AllFilesAdapter(SearchActivity.this, fileInfoModelArrayList,SearchActivity.this,SearchActivity.this, false);
                        filesAdapter.setCallback(SearchActivity.this,SearchActivity.this);
                        filesRecyclerView.setAdapter(filesAdapter);
                    }
                    return true;
                }
                return false;
            }
        });

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
                                fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0], fileInfo[1], newFile));
                            else {
                                fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0],
                                        newFile.getAbsolutePath().substring(newFile.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                                        newFile));
                            }

                            if (!fileInfoModelArrayList.isEmpty()) {

                                Collections.sort(fileInfoModelArrayList, new Comparator<FileInfoModel>() {
                                    @Override
                                    public int compare(FileInfoModel fileInfoModel, FileInfoModel t1) {
                                        return Long.compare(t1.getFile().lastModified(), fileInfoModel.getFile().lastModified());
                                    }
                                });
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!fileInfoModelArrayList.isEmpty()) {


//                                        noFileLayout.setVisibility(View.GONE);
                                        filesAdapter = new AllFilesAdapter(SearchActivity.this, fileInfoModelArrayList,SearchActivity.this,SearchActivity.this, false);
                                        filesAdapter.setCallback(SearchActivity.this,SearchActivity.this);
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

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<FileInfoModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (FileInfoModel s : fileInfoModelArrayList) {
            //if the existing elements contains the search input
            if (s.getFileName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //        if (filterdNames.size() > 0) {
        if (filesAdapter != null ) {
            filesAdapter.filterList(filterdNames);
            if(filterdNames.size()==0){
                noFileLayout.setVisibility(View.VISIBLE);
            }else {
                noFileLayout.setVisibility(View.GONE);
            }
        }
//        }
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
}