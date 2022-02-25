package me.sid.smartcropper.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import me.sid.smartcropper.App;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.PermissionUtils;

public class SaveMultiFileDialog extends Dialog {

    ImageView close_btn;
    Button btn_SaveFile;
    EditText fileNameEd;
    GenericCallback callback;

    View adLayout;
    Context context;

   /* TinyDB tinyDB;*/

    public SaveMultiFileDialog(@NonNull Context context, final GenericCallback fileCreated) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.save_multi_file_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.callback = fileCreated;


        btn_SaveFile = findViewById(R.id.btn_SaveFile);
        close_btn = findViewById(R.id.close_btn);

        fileNameEd = findViewById(R.id.fileNameEd);
        //Create Name of File


        adLayout = findViewById(R.id.adlayout);
        //
        btn_SaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                save();


            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



    void save() {
        if (PermissionUtils.hasPermissionGranted(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

            if (!fileNameEd.getText().toString().equals("") && fileNameEd.getText() != null) {

               /* String file_name = tinyDB.getString("Doc") + fileNameEd.getText().toString();*/
                String file_name = fileNameEd.getText().toString();

                callback.callback(file_name);
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
