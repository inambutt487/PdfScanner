package me.sid.smartcropper.views.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.Objects;

import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.Constants;

public class ViewImageActivity extends BaseActivity {

    public ImageView view_image;
    Toolbar toolbar;
    File file;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.dark_grey));
        toolbar = findViewById(R.id.toolbar);
        view_image = findViewById(R.id.view_image);
//        moreOption=findViewById(R.id.more_option);

        toolbar.setTitle("Scanned Image");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        if (getIntent().getExtras() != null) {
            String path = getIntent().getStringExtra("path");
            if (path != null) {
                file = new File(Objects.requireNonNull(path));
                toolbar.setTitle(file.getName().substring(0, file.getName().lastIndexOf(".")));
                Glide.with(ViewImageActivity.this).load(Uri.fromFile(file)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(view_image);
            } else {
                uri = Uri.parse(getIntent().getStringExtra("uri"));
                Glide.with(ViewImageActivity.this).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(view_image);
            }

        }
   /* moreOption.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.popupMenuStyle);
            final PopupMenu menu = new PopupMenu(wrapper, moreOption, Gravity.END);
            //menu.inflate(R.menu.folder_menu);
            menu.getMenu().add(1, R.id.rename, 1, "Rename");
            menu.getMenu().add(1, R.id.share, 1, "Share");
            menu.getMenu().add(1, R.id.delete, 1, "Delete");
            menu.getMenu().add(1, R.id.moveToFolder, 1, "Move to Folder");

            menu.getMenu().add(1, R.id.imgToPdf, 1, "Convert image to pdf");

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.rename) {


                        menu.dismiss();

                        return true;
                    }  else if (menuItem.getItemId() == R.id.delete) {



                        menu.dismiss();
                        return true;
                    } else if (menuItem.getItemId() == R.id.moveToFolder) {

                        menu.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            menu.show();
        }
    });
    */
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.share) {
            Constants.shareFile(ViewImageActivity.this, file);
            return true;
        }
        /*else if (item.getItemId() == R.id.pdf_to_img) {
            //Toast.makeText(this, String.valueOf(file), Toast.LENGTH_SHORT).show();
         *//*   ExtractImages extractImages = new ExtractImages(this);
            extractImages.extract(String.valueOf(file));*//*


            String filePath = file.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            croppedArrayBitmap.add(bitmap);


            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("source", "PDFViewerActivity");
            startActivity(intent);

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pdf_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}