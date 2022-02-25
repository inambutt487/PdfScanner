package me.sid.smartcropper.views.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PdfReader;
import com.shockwave.pdfium.PdfiumCore;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.PasswordDialog;
import me.sid.smartcropper.dialogs.PermissionDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.OCRUtils;
import me.sid.smartcropper.utils.PDFUtils;
import me.sid.smartcropper.utils.PermissionUtils;
import me.sid.smartcropper.utils.StringUtils;


public class PDFViewerAcitivity extends BaseActivity implements OnErrorListener, GenericCallback,
        OnLoadCompleteListener, View.OnClickListener, PermissionDialog.PermissionGranted {

    private ImageView backimg, pdf_btn, share;

    PDFView pdfView;
    TextView text;
    File file;
    DirectoryUtils mDirectory;
    Uri uri;
    Boolean firstTry = true;
    PDFUtils pdfUtils;
    ImageView backWordTv, forwordTv;
    ProgressDialog dialog;
    String source;
    int pages = 0;
    TextView pageNum;
    private Boolean translate = false;
    Button btn_translate;


    String pathRoot;

    FileInputStream is;
    BufferedReader reader;

    ConstraintLayout bottom;
    Boolean status = false;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        makeFullScreen();
        setContentView(R.layout.activity_p_d_f_viewer_acitivity);

        if (!hasPermissionGranted(PDFViewerAcitivity.this)) {
            requestRequiredPermissions();
            return;
        }


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        pageNum = findViewById(R.id.pageNum);
        btn_translate = findViewById(R.id.btn_translate);
        pdfUtils = new PDFUtils(PDFViewerAcitivity.this);

        bottom = findViewById(R.id.bottom);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("PDF Reader");


        backimg = findViewById(R.id.backimg);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        share = findViewById(R.id.icon_share);


        pdf_btn = findViewById(R.id.pdf_btn);
        pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf")));

            }
        });


        pdfView = findViewById(R.id.pdfView);
        text = findViewById(R.id.pdfText);
        dialog = new ProgressDialog(PDFViewerAcitivity.this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Opening pdf file");

        Intent receivedIntent = getIntent();

        try {

            if (receivedIntent != null) {
                String receivedType = receivedIntent.getType();
                if (receivedType != null && receivedType.startsWith("application/pdf")) {

                    status = true;
                    Uri receivedUri = receivedIntent.getData();
                    if (receivedUri != null) {


                        dialog.show();

                        try {

                            uri = receivedUri;
                            Uri u = Uri.parse(uri.toString());

                            File f = new File("" + u);
                            /*String fName = f.getName();*/
                            InputStream inputStream = getContentResolver().openInputStream(uri);

                            file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".pdf");
                            copyInputStreamToFile(inputStream, file);


                            if (file.getAbsolutePath().endsWith(".txt")) {

                                pathRoot = file.getAbsolutePath();
                                text.setText(readFromFile(PDFViewerAcitivity.this, file));


                                text.setMovementMethod(new ScrollingMovementMethod());
                                translate = false;
                                btn_translate.setVisibility(View.GONE);
                                pdfView.setVisibility(View.GONE);
                                text.setVisibility(View.VISIBLE);
                                bottom.setVisibility(View.GONE);
                            } else {
                                new PdfReader(file.getAbsolutePath());
                                loadPDFFile(file == null ? uri : file, "");
                            }
                        } catch (BadPasswordException e) {
                            passWordLoad();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(this, receivedUri.toString(), Toast.LENGTH_SHORT).show();

                        source = "outside";
                    }
                } else {

                    status = false;

                    String path = getIntent().getStringExtra("path");
                    translate = getIntent().getBooleanExtra("transable", false);

                    if (translate) {
                        btn_translate.setVisibility(View.VISIBLE);
                    }

                    dialog.show();

                    if (path != null) {
                        file = new File(Objects.requireNonNull(path));
                        toolbarTitle.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
                    } else {
                        uri = Uri.parse(getIntent().getStringExtra("uri"));
                    }

                    try {
                        if (file.getAbsolutePath().endsWith(".txt")) {

                            pathRoot = file.getAbsolutePath();
                            text.setText(readFromFile(PDFViewerAcitivity.this, file));


                            text.setMovementMethod(new ScrollingMovementMethod());
                            translate = false;
                            btn_translate.setVisibility(View.GONE);
                            pdfView.setVisibility(View.GONE);
                            text.setVisibility(View.VISIBLE);
                            bottom.setVisibility(View.GONE);
                        } else {
                            new PdfReader(file.getAbsolutePath());
                            loadPDFFile(file == null ? uri : file, "");
                        }
                    } catch (BadPasswordException e) {
                        passWordLoad();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    source = "DocumentActivity";
                }
            }

        } catch (Exception e) {

        }


        mDirectory = new DirectoryUtils(PDFViewerAcitivity.this);
        backWordTv = findViewById(R.id.backWordTv);
        backWordTv.setOnClickListener(this);
        forwordTv = findViewById(R.id.forwordTv);
        forwordTv.setOnClickListener(this);

        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = generateImageFromPdf(pdfView.getCurrentPage());
                String text = null;
                try {
                    text = OCRUtils.getTextFromBitmap(PDFViewerAcitivity.this, bitmap);
                } catch (InterruptedException e) {
                    StringUtils.getInstance().showSnackbar(PDFViewerAcitivity.this, "No text found");
                }


                if (!text.isEmpty() && text != null) {
                    startActivity(new Intent(PDFViewerAcitivity.this, TranslationActivity.class).putExtra("text", text));
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.shareFile = file;
                startActivity(new Intent(PDFViewerAcitivity.this, ShareActivity.class));
            }
        });

    }

    public void requestRequiredPermissions() {

        PermissionDialog dialog = new PermissionDialog(this, this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

    }

    private Bitmap generateImageFromPdf(int pageNumber) {

        PdfiumCore pdfiumCore = new PdfiumCore(PDFViewerAcitivity.this);
        int pageNum = pageNumber;
        Bitmap cbitmap = null;

        try {
            com.shockwave.pdfium.PdfDocument pdf = pdfiumCore.newDocument(
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
            );

            pdfiumCore.openPage(pdf, pageNum);

            int widthh = pdfiumCore.getPageWidth(pdf, pageNum);
            int heightt = pdfiumCore.getPageHeight(pdf, pageNum);


            cbitmap = Bitmap.createBitmap(widthh, heightt, Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdf, cbitmap, pageNumber, 0, 0, widthh, heightt);

            pdfiumCore.closeDocument(pdf);

            new File(Environment.getExternalStorageDirectory() + "/PDF Reader").mkdirs();
            File outputFile = new File(Environment.getExternalStorageDirectory() + "/PDF Reader", "temp_img.jpg");
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            cbitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return cbitmap;
    }

    private void passWordLoad() {

        new PasswordDialog(PDFViewerAcitivity.this, new GenericCallback() {
            @Override
            public void callback(Object o) {
                String name = (String) o;
                if (!name.equals("")) {
                    loadPDFFile(file == null ? uri : file, (String) o);
                } else {
                    Toast.makeText(PDFViewerAcitivity.this, "Enter Password!", Toast.LENGTH_SHORT).show();
                }
            }

        }).show();

    }

    public String getFilepath(Uri _uri) {
        String filePath = null;

        Log.d("", "URI = " + _uri);
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = this.getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            // Toast.makeText(this, filePath.toString(), Toast.LENGTH_SHORT).show();
            cursor.close();

        } else {
            filePath = _uri.getPath();
        }
        Log.d("", "Chosen path = " + filePath);
        return filePath;
    }

    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }


        }

    }

    private void loadPDFFile(Comparable<? extends Comparable<?>> comparable, String password) {

        if (comparable instanceof File) {
            pdfView.fromFile(file).defaultPage(0)
                    .enableDoubletap(true)
                    .enableSwipe(true)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .onError(this)
                    .onLoad(this)
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {
                            hideKeyboard(PDFViewerAcitivity.this);
                            Toast.makeText(PDFViewerAcitivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            passWordLoad();
                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                            if (page == 0) {
                                count = 1;
                            } else {
                                count = page + 1;
                            }

                            pageNum.setText("Page " + String.valueOf(count) + "/" + String.valueOf(pages));

                        }
                    })
                    .password(password)
                    .load();
        } else if (comparable instanceof Uri) {

            pdfView.fromUri(uri).defaultPage(0)
                    .enableDoubletap(true)
                    .enableSwipe(true)
                    .enableAntialiasing(true)
                    .spacing(0)
                    .onLoad(this)
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {
                            hideKeyboard(PDFViewerAcitivity.this);
                            Toast.makeText(PDFViewerAcitivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            passWordLoad();
                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            if (page == 0) {
                                count = 1;
                            } else {
                                count = page + 1;
                            }
                            pageNum.setText("Page " + String.valueOf(count) + "/" + String.valueOf(pages));

                        }
                    })
                    .onError(this)
                    .password(password)
                    .load();
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            exit();
            return true;
        } else if (item.getItemId() == R.id.share) {
            Constants.shareFile = file;
            startActivity(new Intent(this, ShareActivity.class));
//            Constants.shareFile(PDFViewerAcitivity.this, file);
            return true;
        } else if (item.getItemId() == R.id.pdf) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pdfreader.pdfviewer.pdfeditor.pdfcreator.securepdf")));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        if (status) {
            startActivity(new Intent(PDFViewerAcitivity.this, DocumentsActivity.class));
            finish();
        } else {
            finish();
        }

    }


    void exit() {
        startActivity(new Intent(PDFViewerAcitivity.this, DocumentsActivity.class));
        finish();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_menu, menu);

        MenuItem pdf = menu.findItem(R.id.pdf);
        MenuItem share = menu.findItem(R.id.share);

        if (!havePurchase()) {
            pdf.setVisible(true);
        } else {
            pdf.setVisible(false);
        }

        if (translate) {
            pdf.setVisible(false);
            share.setVisible(false);
        }

        return true;
    }*/


    @Override
    public void onError(Throwable t) {
        try {

            dialog.dismiss();

            if (t.getMessage().contains("Password required or incorrect password")) {
                new PasswordDialog(PDFViewerAcitivity.this, new GenericCallback() {
                    @Override
                    public void callback(Object o) {
                        String name = (String) o;
                        if (!name.equals("")) {
                            loadPDFFile(file == null ? uri : file, (String) o);
                        } else {
                            Toast.makeText(PDFViewerAcitivity.this, "Enter Password!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).show();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void callback(Object o) {
        loadPDFFile(file == null ? uri : file, (String) o);

    }

    @Override
    public void loadComplete(int nbPages) {
        dialog.dismiss();
        addPageNumbers();
        if (pdfView.getPageCount() > 0 && pdfView.getPageCount() < 7) {


        } else {
        }

    }


    int count = 1;

    private void addPageNumbers() {
        pages = pdfView.getPageCount();
        pageNum.setText("Page 1/" + String.valueOf(pdfView.getPageCount()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backWordTv) {
            if (count > 1) {
                --count;
                pageNum.setText("Page " + String.valueOf(count) + "/" + String.valueOf(pages));
                pdfView.jumpTo(pdfView.getCurrentPage() - 1, true);
            } else {
                StringUtils.getInstance().showSnackbar(PDFViewerAcitivity.this, "No more page left");
            }
        } else if (view.getId() == R.id.forwordTv) {
            if (count < pages) {
                ++count;
                pageNum.setText("Page " + String.valueOf(count) + "/" + String.valueOf(pages));
                pdfView.jumpTo(pdfView.getCurrentPage() + 1, true);
            } else {
                StringUtils.getInstance().showSnackbar(PDFViewerAcitivity.this, "No more page left");
            }

        }
    }

    @Override
    public void granted() {
        PermissionUtils.checkAndRequestPermissions(
                this,
                new String[0],
                101
        );
    }

    String readFromFile(Context contect, File file) throws IOException {
        String readString = null;
        dialog.dismiss();

        {
            String myData = "";
            File myExternalFile = new File(file.getAbsolutePath());
            try {
                FileInputStream fis = new FileInputStream(myExternalFile);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String strLine;
                while ((strLine = br.readLine()) != null) {
                    myData = myData + strLine + "\n";
                }
                br.close();
                in.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return myData;

        }

    }

}