package me.sid.smartcropper.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class GetFilesUtility extends AsyncTask<String, Void, ArrayList<File>> {
    DirectoryUtils mDirectoryUtils;
    getFilesCallback callback;
    Context context;
    ProgressDialog dialog;

    public GetFilesUtility(Context context, getFilesCallback getFiles) {
        this.context=context;
        this.callback = getFiles;
        mDirectoryUtils = new DirectoryUtils(context);
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading Files");
        dialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
      //  dialog.show();
     }

    @Override
    protected ArrayList<File> doInBackground(String... strings) {
        mDirectoryUtils.clearSelectedArray();
        return mDirectoryUtils.getSelectedFiles(Environment.getExternalStorageDirectory()
                , strings[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<File> arrayList) {
        super.onPostExecute(arrayList);
//        for (File file : arrayList) {
//
//            String[] fileInfo = file.getName().split("\\.");
//            if (fileInfo.length == 2)
//                FileListStatic.Companion.addFile(new FileInfoModel(fileInfo[0], fileInfo[1], file));
//            else {
//                FileListStatic.Companion.addFile(new FileInfoModel(fileInfo[0],
//                        file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
//                        file));
//            }
//
//
//        }
         callback.getFiles(arrayList);
         dialog.dismiss();
    }

    public interface getFilesCallback {
        public void getFiles(ArrayList<File> arrayList);
    }
}