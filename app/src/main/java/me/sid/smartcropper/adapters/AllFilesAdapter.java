package me.sid.smartcropper.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.AlertDeleteDialogHelper;
import me.sid.smartcropper.dialogs.CreateFolderDialog;
import me.sid.smartcropper.dialogs.MessageDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.ImageToPdfCallback;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.FileInfoUtils;
import me.sid.smartcropper.views.activities.PDFViewerAcitivity;
import me.sid.smartcropper.views.activities.ShareActivity;

public class AllFilesAdapter extends RecyclerView.Adapter<AllFilesAdapter.MyFilesHolder> {

    Boolean status = false;
    Context context;
    ArrayList<FileInfoModel> filesArrayList, checkBoxArray;
    DirectoryUtils mDirectory;
    RecyclerView recyclerView;
    GenericCallback callback;
    ImageToPdfCallback imageToPdfCallback;
    boolean showCheckbox = false;
    Activity activity;
    AdapterInterface adapterInterface;

    private Boolean tranlsate;

    public void setCallback(GenericCallback callback, ImageToPdfCallback imageToPdfCallback) {
        this.callback = callback;
        this.imageToPdfCallback = imageToPdfCallback;
    }

    public AllFilesAdapter(Context context, ArrayList<FileInfoModel> filesArrayList, Activity activity, AdapterInterface adapterInterface, Boolean translate) {
        this.context = context;
        this.filesArrayList = filesArrayList;
        mDirectory = new DirectoryUtils(context);
        checkBoxArray = new ArrayList<>();
        this.activity = activity;
        this.adapterInterface = adapterInterface;
        this.tranlsate = translate;
    }

    public interface AdapterInterface {
        void renamed();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void updateList(ArrayList<FileInfoModel> filesArrayList) {

        this.filesArrayList.clear();
        this.filesArrayList.addAll(filesArrayList);
        this.notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_item_layout, parent, false);
        return new MyFilesHolder(view);
    }

    public void setData(ArrayList<FileInfoModel> filesArrayList) {
        this.filesArrayList = new ArrayList<>();
        this.filesArrayList = filesArrayList;
        notifyDataSetChanged();
    }

    public void setShowCheckbox(boolean showMoreBtn) {
        this.showCheckbox = showMoreBtn;
    }

    void openFile(MyFilesHolder holder) {

        Intent intent = new Intent(context, PDFViewerAcitivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
        intent.putExtra("transable", tranlsate);
        context.startActivity(intent);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyFilesHolder holder, int position) {

        if(position == getItemCount()-1){
            setStatus(true);
        }

        holder.fileNameTv.setText(filesArrayList.get(holder.getAdapterPosition()).getFileName());
        holder.sizeTv.setText(FileInfoUtils.getFormattedSize(filesArrayList.get(holder.getAdapterPosition()).getFile()));
        holder.dateTv.setText(FileInfoUtils.getFormattedDate(filesArrayList.get(holder.getAdapterPosition()).getFile()));


        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile(filesArrayList.get(holder.getAdapterPosition()).getFile());
            }
        });


        holder.rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteFileDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllFolderDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
            }
        });

        holder.viewTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openFile(holder);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openFile(holder);
            }
        });

       /* holder.moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                final PopupMenu menu = new PopupMenu(wrapper, holder.fileNameTv, Gravity.END);
                //menu.inflate(R.menu.navigation);
                menu.getMenu().add(1, R.id.rename, 1, "Rename");
                menu.getMenu().add(1, R.id.share, 1, "Share");
                menu.getMenu().add(1, R.id.delete, 1, "Delete");
                menu.getMenu().add(1, R.id.moveToFolder, 1, "Move to Folder");
                if (filesArrayList.get(holder.getAdapterPosition()).getFileType().equals("jpg")) {
                    menu.getMenu().add(1, R.id.imgToPdf, 1, "Convert image to pdf");
                    }

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.rename) {
                            menu.dismiss();
                            showRenameDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
                            return true;
                        } else if (menuItem.getItemId() == R.id.share) {
                            shareFile(filesArrayList.get(holder.getAdapterPosition()).getFile());
                            menu.dismiss();
                            return true;
                        } else if (menuItem.getItemId() == R.id.delete) {
*//*
                            ExtractImages extractImages= new ExtractImages(context);
                            extractImages.extract(String.valueOf(filesArrayList.get(holder.getAdapterPosition()).getFile()));
*//*
                            showDeleteFileDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
                            menu.dismiss();
                            return true;
                        } else if (menuItem.getItemId() == R.id.moveToFolder) {
                            showAllFolderDialog(filesArrayList.get(holder.getAdapterPosition()).getFile(), holder.getAdapterPosition());
                            menu.dismiss();
                            return true;
                        }
                        else if(menuItem.getItemId()==R.id.imgToPdf){
                            File f=filesArrayList.get(holder.getAdapterPosition()).getFile();
                            Uri u=Uri.fromFile(f);
                            menu.dismiss();
                            imageToPdfCallback.imageToPdfCallback(u,f.getName());

                            return true;
                        }
                        return false;
                    }
                });
                menu.show();
            }

        });*/

       /* holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (holder.fileTypeTv.getText().toString().equals("P")) {
                        Intent intent = new Intent(context, PDFViewerAcitivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
                        context.startActivity(intent);
                    } else if (holder.fileTypeTv.getText().toString().equals("I")) {
                        Intent intent = new Intent(context, ViewImageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
                        context.startActivity(intent);
                    } else if (holder.fileTypeTv.getText().toString().equals("T")) {
                        Intent intent = new Intent(context, ViewTextActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", filesArrayList.get(holder.getAdapterPosition()).getFile().getAbsolutePath());
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.d("exxx", "" + e.getMessage());
                }
            }
        });*/


        /*if (holder.checkBox.getVisibility() == View.VISIBLE) {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        checkBoxArray.add(filesArrayList.get(holder .getAdapterPosition()));
                    } else
                        checkBoxArray.remove(filesArrayList.get(holder.getAdapterPosition()));
                    callback.callback(checkBoxArray);
                }
            });
        }*/

        /*if (holder.checkBox.getVisibility() == View.VISIBLE) {
            if (filesArrayList.get(holder.getAdapterPosition()).getSelect()) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }

        }*/


    }


    public void refreshArray(File pdfFile) {
        String[] names = pdfFile.getName().split("\\.");
        FileInfoModel model = new FileInfoModel(names[0],
                pdfFile.getAbsolutePath().substring(pdfFile.getAbsolutePath().lastIndexOf(".")).replace(".", ""),
                pdfFile);
        ArrayList<FileInfoModel> arrayList = filesArrayList;
        arrayList.add(filesArrayList.size(), model);
        setData(arrayList);
        recyclerView.smoothScrollToPosition(filesArrayList.size());

    }

    private void shareFile(File file) {
        Constants.shareFile = file;
        context.startActivity(new Intent(context, ShareActivity.class));
//        Constants.shareFile(context, file);
    }

    private void showDeleteFileDialog(final File file, final int pos) {
      /*  AlertDialogHelper.showAlert(context, new AlertDialogHelper.Callback() {
            @Override
            public void onSucess(int t) {
                if (t == 0) {
                    if (mDirectory.deleteFile(file)) {
                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                        filesArrayList.remove(pos);
                        notifyItemRemoved(pos);
                    } else {
                        Toast.makeText(context, "File not deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, "Delete", "Are you really want to delete this file?");
*/
        AlertDeleteDialogHelper dialog = new AlertDeleteDialogHelper(context, new AlertDeleteDialogHelper.Callback() {


            @Override
            public void onSucess(int t) {

                File dest = mDirectory.createFolder("Trash");

                if (t == 0) {

                    if (mDirectory.moveFile(file.getAbsolutePath(), file.getName(), dest.getAbsolutePath() + "/")) {
                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                        filesArrayList.remove(pos);
                        if (callback != null) {
                            callback.callback(filesArrayList.size());
                        }
                        notifyItemRemoved(pos);
                    } else {
                        Toast.makeText(context, "File not deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    private void showRenameDialog(final File file, final int pos) {
        CreateFolderDialog dialog = new CreateFolderDialog(context, new GenericCallback() {
            @Override
            public void callback(Object o) {

                if (mDirectory.renameFile(file, (String) o)) {
                    Toast.makeText(context, "File is renamed", Toast.LENGTH_SHORT).show();
                    FileInfoModel model = getItem(pos);
                    model.setFileName((String) o);
                    adapterInterface.renamed();
                } else {
                    Toast.makeText(context, "File renamed failed", Toast.LENGTH_SHORT).show();
                }

            }
        }, activity);
        dialog.setSaveBtn("Rename File");
        dialog.setTitle("Enter New File Name");
        dialog.show();
    }

    public void filterList(ArrayList<FileInfoModel> filterdNames) {
        this.filesArrayList = filterdNames;
        notifyDataSetChanged();
    }

    public ArrayList<FileInfoModel> getFilesArrayList() {
        return checkBoxArray;
    }

    public void clearCeckBoxArray() {
        checkBoxArray = new ArrayList<>();
    }

    public ArrayList<FileInfoModel> getRealArray() {
        return filesArrayList;
    }

    public FileInfoModel getItem(int position) {
        return filesArrayList.get(position);
    }

    private void showAllFolderDialog(File moveFile, final int pos) {

        if (!moveFile.getParent().equals(DirectoryUtils.getDownloadFolderPath())) {
            mDirectory.moveFile(moveFile.getAbsolutePath(), moveFile.getName(), DirectoryUtils.getDownloadFolderPath() + "/");
            MessageDialog dialog = new MessageDialog(activity, "Success", "File saved in downloads.");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            /*Toast.makeText(context, "File saved in downloads", Toast.LENGTH_SHORT).show();*/
            notifyItemRemoved(pos);
            callback.callback(2);
        } else {
            MessageDialog dialog = new MessageDialog(activity, "Opps", "File already saved in downloads.");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            /*Toast.makeText(context, "File already saved in downloads", Toast.LENGTH_SHORT).show();*/
        }

    }

    @Override
    public int getItemCount() {
        return filesArrayList.size();
    }

    public class MyFilesHolder extends RecyclerView.ViewHolder {
        TextView fileNameTv, sizeTv, dateTv, viewTv;
        View share, rename, delete, save;
        ImageView fileTypeRl;


        public MyFilesHolder(@NonNull View itemView) {
            super(itemView);
           /* fileTypeRl = itemView.findViewById(R.id.fileTypeRl);
            fileTypeTv = itemView.findViewById(R.id.fileTypeTv);*/
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            viewTv = itemView.findViewById(R.id.viewTv);
            share = itemView.findViewById(R.id.shareView);
            rename = itemView.findViewById(R.id.renameView);
            delete = itemView.findViewById(R.id.deleteView);
            save = itemView.findViewById(R.id.saveView);
            fileTypeRl = itemView.findViewById(R.id.fileTypeRl);

        }

        public void selectedItem(FileInfoModel model) {
            for (FileInfoModel model1 : filesArrayList) {
                model1.setSelect(model.getFileName().equals(model.getFileName()));
            }
            notifyDataSetChanged();
        }


    }
}
