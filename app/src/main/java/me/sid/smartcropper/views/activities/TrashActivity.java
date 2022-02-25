package me.sid.smartcropper.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.AllFilesAdapterTrash;
import me.sid.smartcropper.dialogs.AlertDeleteDialogHelper;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.StringUtils;

public class TrashActivity extends BaseActivity {


    public Button btn_delete, btn_restore;
    RecyclerView trash_rv;
    Toolbar toolbar;
    AllFilesAdapterTrash filesAdapter;
    DirectoryUtils mDirectoryUtils;
    ArrayList<FileInfoModel> fileInfoModelArrayList;
    RelativeLayout noFileLayout;
    ImageView settingImg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_trash);

        settingImg=findViewById(R.id.settingBtn);
        mDirectoryUtils = new DirectoryUtils(this);
        fileInfoModelArrayList = new ArrayList<>();
        btn_delete = findViewById(R.id.btn_delete);
        btn_restore = findViewById(R.id.btn_restore);
        trash_rv = findViewById(R.id.trash_rv);
        trash_rv.setLayoutManager(new LinearLayoutManager(this));
        trash_rv.setHasFixedSize(true);

        noFileLayout = findViewById(R.id.noFileLayout);

        getFiles();


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filesAdapter.getFilesArrayList().size() > 0) {
                    showDeleteFileDialog();
                }else{
                    showToast("Please Delete Select File",TrashActivity.this);
                }
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(filesAdapter.getFilesArrayList().size() > 0) {
                    restore();
                }else{
                    showToast("Please Restore Select File",TrashActivity.this);
                }


            }
        });

        if(!havePurchase()) {
            settingImg.setVisibility(View.VISIBLE);
            settingImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!havePurchase()) {
                        if (!haveNetworkConnection()) {
                            StringUtils.getInstance().showSnackbar(TrashActivity.this, "No Internet Connection!");
                        } else {
                            startActivity(new Intent(TrashActivity.this, PremiumActivity.class));
                        }
                    } else {
                        StringUtils.getInstance().showSnackbar(TrashActivity.this, "Already Purchase!");
                    }
                }
            });
        }

    }

    void restore(){
        if (filesAdapter.getFilesArrayList().size() != 0)
            restorFiles(filesAdapter.getFilesArrayList());
        finishAffinity();
        startActivity(new Intent(TrashActivity.this,DocumentsActivity.class));
        finish();
    }

    private void showDeleteFileDialog() {

        AlertDeleteDialogHelper dialog = new AlertDeleteDialogHelper(this, new AlertDeleteDialogHelper.Callback() {
            @Override
            public void onSucess(int t) {
                if (t == 0) {
                    if (filesAdapter.getFilesArrayList().size() != 0)
                        deleteFiles(filesAdapter.getFilesArrayList());
                }
            }
        });
        dialog.show();
    }

    private void restorFiles(ArrayList<FileInfoModel> moveFile) {

        File dest = mDirectoryUtils.restorTrashFolder();
        if (filesAdapter.getFilesArrayList().size() > 0) {
            for (int i = 0; i < moveFile.size(); i++) {

                mDirectoryUtils.moveFile(moveFile.get(i).getFile().getAbsolutePath(), moveFile.get(i).getFile().getName(), dest.getAbsolutePath() + "/");

            }
            if (filesAdapter.getFilesArrayList().size() != 0)
                deleteFiles(filesAdapter.getFilesArrayList());

            filesAdapter.clearCeckBoxArray();
            filesAdapter.setData(new ArrayList<>());



            getFiles();
        }
    }


    private void deleteFiles(ArrayList<FileInfoModel> moveFile) {

        if (filesAdapter.getFilesArrayList().size() > 0) {
            for (int i = 0; i < moveFile.size(); i++) {

                mDirectoryUtils.deleteFile(moveFile.get(i).getFile());

            }
            filesAdapter.clearCeckBoxArray();
            filesAdapter.setData(new ArrayList<>());
            getFiles();
        }
    }

    private void getFiles() {
        mDirectoryUtils.clearFilterArray();

        ArrayList<File> arrayList = mDirectoryUtils.searchDir(new File(Environment.getExternalStorageDirectory(), Constants.trashfolderDirectory));

        fileInfoModelArrayList.clear();

        if (arrayList != null && arrayList.size() > 0) {


            for (File file : arrayList) {

                String[] fileInfo = file.getName().split("\\.");
                if (fileInfo.length == 2)
                    fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0], fileInfo[1], file));
                else {
                    fileInfoModelArrayList.add(new FileInfoModel(fileInfo[0],
                            file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                            file));
                }

            }

            filesAdapter = new AllFilesAdapterTrash(TrashActivity.this, fileInfoModelArrayList,TrashActivity.this);
            trash_rv.setAdapter(filesAdapter);
            filesAdapter.notifyDataSetChanged();
            noFileLayout.setVisibility(View.GONE);

        } else {
            if (filesAdapter != null) {
                trash_rv.setAdapter(filesAdapter);
                filesAdapter.setData(new ArrayList<FileInfoModel>());
            }
            noFileLayout.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }





}