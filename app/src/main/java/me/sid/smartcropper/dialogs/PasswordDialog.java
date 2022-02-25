package me.sid.smartcropper.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import me.sid.smartcropper.views.activities.DocumentsActivity;

public class PasswordDialog extends Dialog {

    ImageView close_btn;
    Button btn_SaveFile;
    EditText fileNameEd;
    GenericCallback callback;

    View adLayout;
    public PasswordDialog(@NonNull Context context, final GenericCallback fileCreated) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.password_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.callback = fileCreated;


        close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.startActivity(new Intent(context, DocumentsActivity.class));
            }
        });

        btn_SaveFile = findViewById(R.id.btn_SaveFile);
        fileNameEd = findViewById(R.id.fileNameEd);

        adLayout=findViewById(R.id.adlayout);
        //
        btn_SaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.hasPermissionGranted(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})) {

                    if (!fileNameEd.getText().toString().equals("") && fileNameEd.getText() != null) {
                      callback.callback(fileNameEd.getText().toString());
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
        });
    }
}
