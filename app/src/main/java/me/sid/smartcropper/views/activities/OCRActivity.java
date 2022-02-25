package me.sid.smartcropper.views.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.SaveOCRFileDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.OCRUtils;
import me.sid.smartcropper.utils.StringUtils;

import static me.sid.smartcropper.utils.Constants.folderDirectory;

public class OCRActivity extends BaseActivity {

    public TextView btn_done, copytxt, sharetxt, deletetxt, retake, btnOK, retakeEdit;
    public EditText ocrText;
    private ProgressBar extractingProgress;
    public static String FILE_PATH = "file_path";
    ArrayList<Bitmap> bitmaps;
    StringBuffer extractedText;
    Toolbar toolbar;
    ConstraintLayout adlayout;
    View adlayout2;
    ImageView backbtn, premiumImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);

        init();

        premiumImg = findViewById(R.id.premiumImg);
        if(!havePurchase()) {
            premiumImg.setVisibility(View.VISIBLE);
            premiumImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!havePurchase()) {
                        if (!haveNetworkConnection()) {
                            StringUtils.getInstance().showSnackbar(OCRActivity.this, "No Internet Connection!");
                        } else {
                            startActivity(new Intent(OCRActivity.this, PremiumActivity.class));
                        }
                    } else {
                        StringUtils.getInstance().showSnackbar(OCRActivity.this, "Already Purchase!");
                    }
                }
            });
        }

        backbtn = findViewById(R.id.menuImg);
        copytxt=findViewById(R.id.copyTxt);
        sharetxt=findViewById(R.id.shareTxt);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ocrText = findViewById(R.id.ocrText);
        ocrText.setMovementMethod(new ScrollingMovementMethod());
        btn_done = findViewById(R.id.btn_done);
        extractingProgress = findViewById(R.id.extractingProgress);
        extractingProgress.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        ocrText.setText("Extracting Text Please Wait");
        bitmaps = new ArrayList<>();
//        bitmaps.add(croppedBitmap);
        bitmaps.addAll(croppedArrayBitmap);

        new OCRExtractTask(this, getApplicationContext(), bitmaps).execute();


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doneCall();

            }
        });

        copytxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!extractedText.equals("")) {
                    setClipboard( String.valueOf(extractedText));
                }
            }
        });

        sharetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });


        deletetxt=findViewById(R.id.deleteTxt);
        deletetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocrText.setText("");
            }
        });

        retake=findViewById(R.id.retakeTxt);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OCRActivity.this, GernalCameraActivity.class));
            }
        });

        btnOK=findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneCall();
            }
        });

        retakeEdit=findViewById(R.id.retakeEdit);
        retakeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocrText.setFocusableInTouchMode(true);
                ocrText.setFocusable(true);
            }
        });

    }

    void share(){
        if (!extractedText.equals("")) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            /*This will be the actual content you wish you share.*/
            String shareBody =String.valueOf(extractedText);
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain");
            /*Applying information Subject and Body.*/
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ocr text");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            /*Fire!*/
            startActivity(Intent.createChooser(intent, "Share via"));
        }
    }

    void doneCall(){
        if (!extractedText.equals("")) {


                new SaveOCRFileDialog(OCRActivity.this, new GenericCallback() {
                    @Override
                    public void callback(Object o) {
                        String name = (String) o;
                        if (!name.equals("")) {
                            writeToFile(name, String.valueOf(extractedText));
                        } else {
                            Toast.makeText(OCRActivity.this, "File Name Can't be Empty", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).show();



        }
    }


    private void setClipboard(String text) {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this,"Text copied to clipboard",Toast.LENGTH_SHORT).show();

    }

    public void init() {
        adlayout2 = findViewById(R.id.adlayout2);
        adlayout = findViewById(R.id.adlayout);

        adlayout.setVisibility(View.GONE);
        adlayout2.setVisibility(View.GONE);
        if (!isConnected()) {

            adlayout.setVisibility(View.INVISIBLE);
        }
    }

    private void writeToFile(String name, String data) {
        try {

            File directory = new File(Environment.getExternalStorageDirectory().toString(), folderDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, name + ".txt");
            if (file.exists()) {
                file.delete();
            }


            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();

            Toast.makeText(this, "Text file Saved !", Toast.LENGTH_LONG).show();
            onBackPressed();

        } catch (java.io.IOException e) {

            //do something if an IOException occurs.
            Toast.makeText(this, "ERROR - Text could't be added", Toast.LENGTH_LONG).show();
        }
    }

    public void setText(String content) {
        this.ocrText.setText(content);
    }

    private class OCRExtractTask extends AsyncTask<String, Void, String> {

        private OCRActivity ocrActivity;
        private Context context;
        private ArrayList<Bitmap> arrayList;

        public OCRExtractTask(OCRActivity ocrActivity, Context context, ArrayList<Bitmap> bitmaps) {
            this.ocrActivity = ocrActivity;
            this.context = context;
            this.arrayList = bitmaps;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                extractedText = new StringBuffer();

                for (Bitmap eachPage : bitmaps) {
                    try {
                        extractedText.append(
                                OCRUtils.getTextFromBitmap(OCRActivity.this, eachPage)
                        );

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                        btn_done.setVisibility(View.GONE);
                        extractingProgress.setVisibility(View.GONE);
                        ocrText.setText("No Text Found");
                    }


                }
            } catch (Exception e) {
                return "";
            }
            return String.valueOf(extractedText);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")) {
                btn_done.setVisibility(View.GONE);
                extractingProgress.setVisibility(View.GONE);
                ocrText.setText("No Text Found");
            } else {
                btn_done.setVisibility(View.VISIBLE);
                extractingProgress.setVisibility(View.GONE);
                ocrText.setText(s);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}