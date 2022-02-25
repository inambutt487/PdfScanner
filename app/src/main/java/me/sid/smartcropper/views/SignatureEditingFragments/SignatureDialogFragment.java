package me.sid.smartcropper.views.SignatureEditingFragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kyanogen.signatureview.SignatureView;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.ColorPickerAdapter;


public class SignatureDialogFragment extends DialogFragment {

    public static final String TAG = SignatureDialogFragment.class.getSimpleName();

    SignatureView signature_view;
    TextView tv_clear_signature, iv_close_signature, iv_apply_signature;
    RecyclerView addTextColorPickerRecyclerView;
    ColorPickerAdapter colorPickerAdapter;
    private SignatureEditor signatureEditor;

    public interface SignatureEditor {
        void onDone(Bitmap bitmap);
    }



    public static SignatureDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                               @NonNull String inputText,
                                               @ColorInt int colorCode) {
        SignatureDialogFragment fragment = new SignatureDialogFragment();
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }


    public static SignatureDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_signature_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signature_view = (SignatureView) view.findViewById(R.id.signature_view);

        int signa_pen_color;
        signa_pen_color = getActivity().getResources().getColor(R.color.colorAccent);
        signature_view.setPenColor(signa_pen_color);

        tv_clear_signature = (TextView) view.findViewById(R.id.tv_clear_signature);
        tv_clear_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signature_view.clearCanvas();
            }
        });

        iv_close_signature = (TextView) view.findViewById(R.id.iv_close_signature);
        iv_close_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                signature_view.clearCanvas();
            }
        });

        iv_apply_signature = (TextView) view.findViewById(R.id.iv_apply_signature);
        iv_apply_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (!signature_view.isBitmapEmpty()) {
                        signatureEditor.onDone(signature_view.getSignatureBitmap());
                        signature_view.clearCanvas();
                    }

                } catch (Exception e) {

                }

                dismiss();

            }
        });

        //Setup the color picker for text color
        addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        colorPickerAdapter = new ColorPickerAdapter(getActivity());
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                signature_view.setPenColor(colorCode);
            }
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
    }

    public void setOnSignatureListener(SignatureEditor signatureEditor) {
        this.signatureEditor = signatureEditor;
    }

}
