package me.sid.smartcropper.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import me.sid.smartcropper.R;

public class AlertDeleteDialogHelper extends Dialog {
    Button btnOk,btnNo;

    public AlertDeleteDialogHelper(@NonNull Context context,final Callback callback) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.create_delete_dialog_layout);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        btnOk = findViewById(R.id.btnOk);
        btnNo = findViewById(R.id.btnNo);
         btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.onSucess(0);
                dismiss();

             }
        });
         btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onSucess(-1);
                dismiss();
            }
        });
    }



    public interface Callback {

        public void onSucess(int t);

    }

}