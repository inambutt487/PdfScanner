package me.sid.smartcropper.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.AlertDeleteDialogHelper;
import me.sid.smartcropper.dialogs.CreateFolderDialog;
import me.sid.smartcropper.interfaces.FolderMenuCallback;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.FileInfoUtils;

public class AllFolderAdapter extends RecyclerView.Adapter {
    ArrayList<File> folderArray;
    GenericCallback callback;
    FolderMenuCallback folderMenuCallback;
    DirectoryUtils mDirectory;

    public AllFolderAdapter(ArrayList<File> folderArray, Context mContext,FolderMenuCallback folderMenuCallback) {
        this.folderArray = folderArray;
        this.mContext = mContext;
        this.folderMenuCallback=folderMenuCallback;
        mDirectory = new DirectoryUtils(mContext);
    }
    public AllFolderAdapter(ArrayList<File> folderArray, Context mContext) {
        this.folderArray = folderArray;
        this.mContext = mContext;
       
    }
    public void filterList(ArrayList<File> filterdNames) {
        this.folderArray = filterdNames;
        notifyDataSetChanged();
    }

    Context mContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_delete_layout, parent, false);
        return new ViewHolderSwipe(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ViewHolderSwipe holder1 = (ViewHolderSwipe) holder;

        if (folderArray != null && 0 <= position && position < folderArray.size()) {
            final File data = folderArray.get(position);

            holder1.bind(data);
        }
        holder1.optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
                final PopupMenu menu = new PopupMenu(wrapper, holder1.optionMenu, Gravity.END);
                menu.inflate(R.menu.folder_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.rename) {
                           showRenameDialog(folderArray.get(position),holder1.getAdapterPosition());

                            menu.dismiss();

                            return true;
                        }  else if (menuItem.getItemId() == R.id.delete) {
                            showDeleteFileDialog(position);


                            menu.dismiss();
                            return true;
                        }/* else if (menuItem.getItemId() == R.id.moveToFolder) {

                            menu.dismiss();
                            return true;
                        }*/
                        return false;
                    }
                });
                menu.show();
            }
        });

    }
    private void showRenameDialog(final File file, final int pos) {
        CreateFolderDialog dialog = new CreateFolderDialog(mContext, new GenericCallback() {
            @Override
            public void callback(Object o) {
                if (mDirectory.renameFolder(file, (String) o)) {
                    Toast.makeText(mContext, "File is renamed", Toast.LENGTH_SHORT).show();
                    folderMenuCallback.renameCallback((String) o);

                    //notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "File renamed failed", Toast.LENGTH_SHORT).show();
                }
            }
        }, null);
        dialog.setSaveBtn("Rename File");
        dialog.setTitle("Enter New File Name");
        dialog.show();
    }
    private void showDeleteFileDialog( int pos) {


        AlertDeleteDialogHelper dialog = new AlertDeleteDialogHelper(mContext, new AlertDeleteDialogHelper.Callback() {
            @Override
            public void onSucess(int t) {

                if (t == 0) {

                    folderMenuCallback.deleteCallback(pos);
                }
            }
        });
        dialog.show();
    }
    public void setFolderArray(ArrayList<File> array) {
        folderArray = array;
        notifyDataSetChanged();
    }

    public void setCallback(GenericCallback callback) {
        this.callback = callback;
        this.folderMenuCallback=folderMenuCallback;
    }

    @Override
    public int getItemCount() {
        return folderArray.size();
    }

    public class MyFolderHolder extends RecyclerView.ViewHolder {
        TextView fileNameTv, fileSizeTv, dateTv;
        ConstraintLayout rootLayout;

        public MyFolderHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            fileSizeTv = itemView.findViewById(R.id.fileSizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

    private class ViewHolderSwipe extends RecyclerView.ViewHolder {
        RelativeLayout swipeLayout;
        View frontLayout;
        View deleteLayout;
        TextView textView;
        TextView fileNameTv, fileSizeTv, dateTv;
        ConstraintLayout rootLayout;
        ImageView optionMenu;

        public ViewHolderSwipe(View itemView) {
            super(itemView);
            swipeLayout = (RelativeLayout) itemView.findViewById(R.id.swipe_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            textView = (TextView) itemView.findViewById(R.id.text);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            fileSizeTv = itemView.findViewById(R.id.fileSizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            optionMenu=itemView.findViewById(R.id.option_menu);

        }

        public void bind(final File data) {
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFile(data);
                    folderArray.remove(getAdapterPosition());
                    callback.callback(String.valueOf(folderArray.size()));
                    notifyItemRemoved(getAdapterPosition());

                }
            });
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File temFile = new File(folderArray.get(getAdapterPosition()).getAbsolutePath());
                    if (callback != null) {
                        callback.callback(temFile);
                    }
                }
            });

            fileNameTv.setText(data.getName());
            fileSizeTv.setText("Files: " + data.listFiles().length);
            dateTv.setText(FileInfoUtils.getFormattedDate(data));
        }
    }

    public void deleteFile(File file) {
//        File deleteFile = new File(file.getAbsolutePath());
        if (file.listFiles().length == 0) {
            if (file.exists())
                file.delete();
        } else {
            for (File tempFile : file.listFiles()) {
                if (tempFile.exists())
                    tempFile.delete();
            }
            file.delete();
        }
    }
}
