package me.sid.smartcropper.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;

import me.sid.smartcropper.App;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.CreateCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.PermissionUtils;

public class SaveFileDialog extends Dialog {

    ImageView close_btn;
    Button btn_SaveFile;
    EditText fileNameEd;
    RadioGroup fileRG;
    RadioButton rb_PDF, rb_IMG;
    CreateCallback callback;
    DirectoryUtils mDirectoryUtils;
    Bitmap mFile;
    File mPdfFile;
    Context context;

  /*  TinyDB tinyDB;*/

    public SaveFileDialog(@NonNull Context context, Bitmap file, final CreateCallback fileCreated) {
        super(context);
        this.context = context;

     /*   tinyDB = new TinyDB(context);*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.save_file_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.callback = fileCreated;
        this.mFile = file;

        mDirectoryUtils = new DirectoryUtils(context);
        close_btn = findViewById(R.id.close_btn);
        btn_SaveFile = findViewById(R.id.btn_SaveFile);


        fileNameEd = findViewById(R.id.fileNameEd);
        //Create Name of File

        fileRG = findViewById(R.id.fileRG);
        rb_PDF = findViewById(R.id.rb_PDF);
        rb_IMG = findViewById(R.id.rb_IMG);


        btn_SaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                save();


            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowing())
                    dismiss();
            }
        });

    }

    void save() {
        if (PermissionUtils.hasPermissionGranted(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

            if (!fileNameEd.getText().toString().equals("") && fileNameEd.getText() != null) {

            /*    String file_name = tinyDB.getString("Doc") + fileNameEd.getText().toString();*/

                String file_name = fileNameEd.getText().toString();


                if (rb_IMG.isChecked()) {
                    callback.onSaveAs(2, file_name);
//                            callback.callback((mDirectoryUtils.saveImageFile(mFile, fileNameEd.getText().toString())));
//                            callback.callback("Done");
                } else if (rb_PDF.isChecked()) {
                    callback.onSaveAs(2, file_name);
//                            callback.callback(fileNameEd.getText().toString());
//                            callback.callback((mDirectoryUtils.savePDFFile(mFile, fileNameEd.getText().toString())));
                }
            } else {
                Toast.makeText(context, "File name Can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            PermissionUtils.checkAndRequestPermissions(App.getInstance().getmCurrentActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_EXTERNAL_STORAGE);
        }
        fileNameEd.setText("");
        dismiss();
    }


}
