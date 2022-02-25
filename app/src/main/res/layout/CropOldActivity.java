package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.ImageUtils;
import me.sid.smartcropper.utils.OCRUtils;
import me.sid.smartcropperlib.IvGenericCallback;
import me.sid.smartcropperlib.view.CropImageView;

public class CropActivity extends BaseActivity implements View.OnClickListener, IvGenericCallback {
    String source;
    DirectoryUtils mDirectory;
    CropImageView ivCrop;
    ImageView leftPage,rightPage;
    ImageButton back_btn, settin_btn, pdf_btn;
    TextView tv_crop_count;
    Button  btn_crop, btn_confirm,btn_undo,btn_redo;
    Bitmap selectedBitmap = null;
    private ArrayList<File> arrayListfile = null;
    ProgressDialog dialog;
    int arrayCount = 0;
    int count = 0;
    int scrollPos=0;
    boolean againCrop = false;
    boolean cropBtnStatus=false;
    boolean cnfrmBtnStatus=false;
    Bitmap originalBitmap;
    Bitmap croppedBitmp;
    ArrayList<File> originalArrayFile;
    ArrayList<Bitmap> currentBitmapArray;

    boolean isMulti=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading data");
        mDirectory = new DirectoryUtils(this);
        arrayListfile = new ArrayList<>();
        originalArrayFile = new ArrayList<>();
        if (getIntent() != null) {
            againCrop = getIntent().getBooleanExtra("againCrop", false);//check if comes again for crop
            arrayListfile = (ArrayList<File>) getIntent().getSerializableExtra("FILES_TO_SEND");
            originalArrayFile=(ArrayList<File>) getIntent().getSerializableExtra("FILES_TO_SEND");

            if (arrayListfile != null)
                arrayCount = arrayListfile.size();
        }

        init();

    }

    private void init() {
        source=getIntent().getStringExtra("source");

        ivCrop = findViewById(R.id.iv_crop);
        ivCrop.setOnClickListener(this);
        btn_undo = findViewById(R.id.btn_crop_cancel);
        btn_redo=findViewById(R.id.btn_redo);
        btn_crop = findViewById(R.id.btn_crop);
        btn_confirm = findViewById(R.id.btn_confirm);
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        settin_btn = findViewById(R.id.settin_btn);
        settin_btn.setOnClickListener(this);
        pdf_btn = findViewById(R.id.pdf_btn);
        pdf_btn.setOnClickListener(this);
        currentBitmapArray=new ArrayList<Bitmap>();


        tv_crop_count = findViewById(R.id.tv_crop_count);

        leftPage=findViewById(R.id.left_page);
        rightPage=findViewById(R.id.right_page);
        originalBitmap=selectedBitmap;
        croppedBitmp=selectedBitmap;

//        if (croppedArrayBitmap.size() > 0 && againCrop) //means come again for crop
//        {
//            selectedBitmap = croppedArrayBitmap.get(againCropIndex);
//            new CropImagee(selectedBitmap).execute();
//
//
//
//        }
         if (arrayCount > 0) {
            if (arrayCount > 1) {
                isMulti=true;
               // btn_confirm.setText("Next");
                btn_crop.setVisibility(View.VISIBLE);
                tv_crop_count.setVisibility(View.VISIBLE);
                leftPage.setVisibility(View.VISIBLE);
                rightPage.setVisibility(View.VISIBLE);
            }else{
                btn_crop.setVisibility(View.GONE);
                isMulti=false;
            }
            setData(0);
          //  croppedArrayBitmap.add(croppedBitmp);
         //   currentBitmapArray.add(croppedBitmp);
        }else{
             btn_crop.setVisibility(View.GONE);
             isMulti=false;
         }


        btn_undo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              // arrayListfile.set(scrollPos,originalBitmap);
                Bitmap bit=ImageUtils.loadCapturedBitmap(originalArrayFile.get(scrollPos).getAbsolutePath());
                new CropImagee(bit).execute();
              //  currentBitmapArray.set(scrollPos,bit);
                //croppedArrayBitmap.set(scrollPos,bit);
               // ivCrop.setFullImgCrop();
            }
        });
        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(croppedArrayBitmap.get(scrollPos)!=null) {
                    new CropImagee(croppedArrayBitmap.get(scrollPos)).execute();
                }
                if(arrayListfile.size()>1) {
                    currentBitmapArray.set(scrollPos, croppedArrayBitmap.get(scrollPos));
                }
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMulti) {
                    goNext();
                }else{
                    cnfrmBtnStatus=true;
                    cropBtnStatus=true;
                    // originalBitmap=ivCrop.getBitmap();

                    croppedBitmp=ivCrop.crop();
                    croppedArrayBitmap.set(scrollPos,croppedBitmp);

                    //ivCrop.setImageToCrop(bit,CropActivity.this);
                    new CropImagee(croppedBitmp).execute();
                }
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropBtnStatus=true;
               // originalBitmap=ivCrop.getBitmap();

                croppedBitmp=ivCrop.crop();
                croppedArrayBitmap.set(scrollPos,croppedBitmp);

                //ivCrop.setImageToCrop(bit,CropActivity.this);
                new CropImagee(croppedBitmp).execute();
            }
        });



     rightPage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if (scrollPos+1 < arrayCount) {
               Bitmap b=  ivCrop.getBitmap();
               if(scrollPos<currentBitmapArray.size()){
                   currentBitmapArray.set(scrollPos,b); //current array
               }
               else{
                   currentBitmapArray.add(scrollPos,b); //current array

               }
               if(b!=null){

               }
                mSetData(1);
//                 else if (croppedArrayBitmap.size() > 0) {
//                     startActivity(EditActivity.class, null);
//                 }

             }
         }
     });

        leftPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(scrollPos>0){
                    mSetData(0);
                }
//                 else if (croppedArrayBitmap.size() > 0) {
//                     startActivity(EditActivity.class, null);
//                 }


            }
        });




    }

    void goNext(){


//                        if(croppedArrayBitmap.size()<arrayListfile.size()){
//                            for(int i=croppedArrayBitmap.size();i<arrayListfile.size();i++)
//                            {
//                                String filePath = arrayListfile.get(i).getPath();
//                                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                                croppedArrayBitmap.add(bitmap);
//                            }
//                        }
        croppedArrayBitmap.clear();
        if (arrayListfile.size() == 1) {
            Bitmap b=ivCrop.getBitmap();
            croppedArrayBitmap.add(b);
        }
        else {
            for (Bitmap bit : currentBitmapArray) {

                croppedArrayBitmap.add(bit);
            }

            if (croppedArrayBitmap.size() < arrayListfile.size()) {
                for (int i = croppedArrayBitmap.size(); i < arrayListfile.size(); i++) {
                    String filePath = arrayListfile.get(i).getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    croppedArrayBitmap.add(bitmap);
                }
            }

        }
        if (croppedArrayBitmap.size() > 0) {
            //croppedArrayBitmap.add(croppedArrayBitmap.get(croppedArrayBitmap.size()-1));
            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
            if (source != null) {
                intent.putExtra("source", source);
            }

            startActivity(intent);
        }
    }

    public void refreshArraylistData(int innerCount) {

      new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();

    }

   public void setData(int innerCount) {


            if (arrayCount > 1) {
                originalBitmap=ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath());
                new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();
                count = innerCount + 1;

                if (count == arrayCount) {
                    btn_confirm.setText("Done");
                }
                int k=scrollPos+1;
                tv_crop_count.setText("Page " + k + "/" + arrayCount);
            } else {
                count = 1;
                originalBitmap=ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath());
                new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(innerCount).getAbsolutePath())).execute();
            }
            croppedArrayBitmap.add(originalBitmap);
          //  currentBitmapArray.add(originalBitmap);
         //   mDirectory.deleteFile(new File(arrayListfile.get(innerCount).getAbsolutePath()));

    }

public  void mSetData(int pageDirection){
    if (pageDirection == 0) //left click
    {
        scrollPos=scrollPos-1;
        if (arrayCount > 1) {
            originalBitmap= croppedArrayBitmap.get(scrollPos);
           if(originalBitmap!=null) {
               new CropImagee(originalBitmap).execute();
           }
            if (count == arrayCount) {
                btn_confirm.setText("Done");
            }
            int k=scrollPos+1;
            tv_crop_count.setText("Page " + k + "/" + arrayCount);
        } else {
            count = 1;
            originalBitmap=ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
            new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath())).execute();
        }
     //   mDirectory.deleteFile(new File(arrayListfile.get(scrollPos).getAbsolutePath()));
    }
    else if (pageDirection == 1) //right click
    {
        if (arrayCount > 1) {
            scrollPos=scrollPos+1;
            if(scrollPos<croppedArrayBitmap.size()) { //
                originalBitmap = croppedArrayBitmap.get(scrollPos);
                new CropImagee(originalBitmap).execute();
            }
            else{ //have to laod new image
                originalBitmap = ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
                croppedArrayBitmap.add(originalBitmap);

               if(originalBitmap!=null){
                   new CropImagee(originalBitmap).execute();
               }
               else{
//                   showToast("bit is null",getApplicationContext());

               }

            }


            if (count == arrayCount) {
                btn_confirm.setText("Done");
            }
            int k=scrollPos+1;
            tv_crop_count.setText("Page " + k + "/" + arrayCount);
        } else {
            count = 1;
            originalBitmap=ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath());
            new CropImagee(ImageUtils.loadCapturedBitmap(arrayListfile.get(scrollPos).getAbsolutePath())).execute();
        }
      //  mDirectory.deleteFile(new File(arrayListfile.get(scrollPos).getAbsolutePath()));

    }
}

    public boolean addDataToArray() {
        if (ivCrop.canRightCrop()) {

            croppedArrayBitmap.add(ivCrop.crop());
            return true;
        }
        return false;

    }

    public boolean removeDataToArray() {
        if (ivCrop.canRightCrop()) {

            croppedArrayBitmap.remove(ivCrop.crop());
            return true;
        }
        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        if (requestCode == 999 && data != null) {
            ArrayList<File> file = (ArrayList<File>) data.getSerializableExtra("file");
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.back_btn) {
            onBackPressed();
        } else if (view.getId() == R.id.settin_btn) {
            startActivity(SettingActivity.class, null);
        }
        else if(view.getId()==R.id.pdf_btn){

            final String appPackageName = "com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    @Override
    public void onBackPressed() {
        showSaveDialog(this,"");
    }


    @Override
    public void Ivcallback(Object o) {
        String res = (String) o;
        if (res.equals("done"))
            dialog.dismiss();
    }


    private class CropImagee extends AsyncTask<Bitmap, Void, Bitmap> {


        public CropImagee(Bitmap mselectedBitmap) {
            selectedBitmap = mselectedBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();

        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    dialog.show();
                    ivCrop.setImageToCrop(selectedBitmap, CropActivity.this);
                }
            });
            return ivCrop.getBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dialog.dismiss();
            if (cropBtnStatus) {
           //     ivCrop.setImageToCrop(bitmap, CropActivity.this);
              //  croppedArrayBitmap.size();
            }

            if(!isMulti && cnfrmBtnStatus){
                goNext();
            }

        }
    }
}
