package me.sid.smartcropper.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.File;

import me.sid.smartcropper.R;

public class PDFUtils {

    private final Activity mContext;
    private final FileUtils mFileUtils;

    public PDFUtils(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(mContext);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Creates a mDialog with details of given PDF file
     *
     * @param file - file name
     */
    public void showDetails(File file) {
        String name = file.getName();
        String path = file.getPath();
        String size = FileInfoUtils.getFormattedSize(file);
        String lastModDate = FileInfoUtils.getFormattedSize(file);

        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
        message.setText(String.format
                (mContext.getResources().getString(R.string.file_info), name, path, size, lastModDate));
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(mContext.getResources().getColor(R.color.black));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    /**
     * Check if a PDF at given path is encrypted
     *
     * @param path - path of PDF
     * @return true - if encrypted otherwise false
     */
   /* @WorkerThread
    public boolean isPDFEncrypted(String path) {
        boolean isEncrypted;
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(path);
            isEncrypted = pdfReader.isEncrypted();
        } catch (IOException e) {
            isEncrypted = true;
        } finally {
            if (pdfReader != null) pdfReader.close();
        }
        return isEncrypted;
    }*/




    /*private void initDoc(PdfReader reader, Document document, PdfWriter writer) {
        int numOfPages = reader.getNumberOfPages();
        PdfContentByte cb = writer.getDirectContent();
        PdfImportedPage importedPage;
        for (int page = 1; page <= numOfPages; page++) {
            importedPage = writer.getImportedPage(reader, page);
            document.newPage();
            cb.addTemplate(importedPage, 0, 0);
        }
    }
    private void appendImages(Document document, ArrayList<String> imagesUri) throws DocumentException, IOException {
        Rectangle documentRect = document.getPageSize();
        for (int i = 0; i < imagesUri.size(); i++) {
            document.newPage();
            Image image = Image.getInstance(imagesUri.get(i));
            image.setBorder(0);
            float pageWidth = document.getPageSize().getWidth(); // - (mMarginLeft + mMarginRight);
            float pageHeight = document.getPageSize().getHeight(); // - (mMarginBottom + mMarginTop);
            image.scaleToFit(pageWidth, pageHeight);
            image.setAbsolutePosition(
                    (documentRect.getWidth() - image.getScaledWidth()) / 2,
                    (documentRect.getHeight() - image.getScaledHeight()) / 2);
            document.add(image);
        }
    }*/


    /**
     * @param uri                     Uri of the pdf
     * @param path                    Absolute path of the pdf
     * @param onPdfReorderedInterface interface to update  pdf reorder progress
     */
//    public void reorderPdfPages(Uri uri, String path, @NonNull OnPdfReorderedInterface onPdfReorderedInterface) {
//        new ReorderPdfPagesAsync(uri, path, mContext, onPdfReorderedInterface).execute();
//    }

   /* private class ReorderPdfPagesAsync extends AsyncTask<String, String, ArrayList<Bitmap>> {

        private final Uri mUri;
        private final String mPath;
        private final OnPdfReorderedInterface mOnPdfReorderedInterface;
        private final Activity mActivity;

        *//**
         * @param uri                     Uri of the pdf
         * @param path                    Absolute path of the pdf
         * @param onPdfReorderedInterface interface to update  pdf reorder progress
         * @param activity                Its needed to get the current context
         *//*

        ReorderPdfPagesAsync(Uri uri,
                             String path,
                             Activity activity,
                             OnPdfReorderedInterface onPdfReorderedInterface) {
            this.mUri = uri;
            this.mPath = path;
            this.mOnPdfReorderedInterface = onPdfReorderedInterface;
            this.mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mOnPdfReorderedInterface.onPdfReorderStarted();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... strings) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            ParcelFileDescriptor fileDescriptor = null;
            try {
                if (mUri != null)
                    fileDescriptor = mActivity.getContentResolver().openFileDescriptor(mUri, "r");
                else if (mPath != null)
                    fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);
                if (fileDescriptor != null) {
                    PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                    bitmaps = getBitmaps(renderer);
                    // close the renderer
                    renderer.close();
                }
            } catch (IOException | SecurityException | IllegalArgumentException | OutOfMemoryError e) {
                e.printStackTrace();
            }
            return bitmaps;
        }

        *//**
         * Get list of Bitmaps from PdfRenderer
         *
         * @param renderer
         * @return
         *//*
        private ArrayList<Bitmap> getBitmaps(PdfRenderer renderer) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                        Bitmap.Config.ARGB_8888);
                // say we render for showing on the screen
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                // do stuff with the bitmap
                bitmaps.add(bitmap);
                // close the page
                page.close();
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            if (bitmaps != null && !bitmaps.isEmpty()) {
                mOnPdfReorderedInterface.onPdfReorderCompleted(bitmaps);
            } else {
                mOnPdfReorderedInterface.onPdfReorderFailed();
            }
        }
    }*/

}
