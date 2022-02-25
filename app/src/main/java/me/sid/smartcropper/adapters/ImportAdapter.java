package me.sid.smartcropper.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.sid.smartcropper.R;
import me.sid.smartcropper.dialogs.AlertDeleteDialogHelper;
import me.sid.smartcropper.dialogs.CreateFolderDialog;
import me.sid.smartcropper.interfaces.GenericCallback;
import me.sid.smartcropper.interfaces.ImageToPdfCallback;
import me.sid.smartcropper.models.FileInfoModel;
import me.sid.smartcropper.utils.Constants;
import me.sid.smartcropper.utils.DirectoryUtils;
import me.sid.smartcropper.utils.FileInfoUtils;
import me.sid.smartcropper.views.activities.PDFViewerAcitivity;
import me.sid.smartcropper.views.activities.ShareActivity;

public class ImportAdapter extends RecyclerView.Adapter<ImportAdapter.MyFilesHolder> {

    Context context;
    ArrayList<FileInfoModel> filesArrayList, checkBoxArray;
    ArrayList<FileInfoModel> copyArrayList = new ArrayList<>();
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

    public ImportAdapter(Context context, ArrayList<FileInfoModel> filesArrayList, Activity activity, AdapterInterface adapterInterface, Boolean translate) {
        this.context = context;
        this.filesArrayList = filesArrayList;
        copyArrayList = filesArrayList;
        mDirectory = new DirectoryUtils(context);
        checkBoxArray = new ArrayList<>();
        this.activity = activity;
        this.adapterInterface = adapterInterface;
        this.tranlsate = translate;
    }

    public interface AdapterInterface {
        void renamed();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.import_item_layout, parent, false);
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

        holder.fileNameTv.setText(filesArrayList.get(holder.getAdapterPosition()).getFileName());
        holder.sizeTv.setText(FileInfoUtils.getFormattedSize(filesArrayList.get(holder.getAdapterPosition()).getFile()));
        holder.dateTv.setText(FileInfoUtils.getFormattedDate(filesArrayList.get(holder.getAdapterPosition()).getFile()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
//                    notifyDataSetChanged();
//                    callback.callback(1);
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
            Toast.makeText(context, "File saved in downloads", Toast.LENGTH_SHORT).show();
            notifyItemRemoved(pos);
            callback.callback(2);
        } else {
            Toast.makeText(context, "File already saved in downloads", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return filesArrayList.size();
    }

    public class MyFilesHolder extends RecyclerView.ViewHolder {
        TextView fileNameTv, sizeTv, dateTv;
        ImageView fileTypeRl;


        public MyFilesHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            fileTypeRl = itemView.findViewById(R.id.fileTypeRl);

        }

        public void selectedItem(FileInfoModel model) {
            for (FileInfoModel model1 : filesArrayList) {
                model1.setSelect(model.getFileName().equals(model.getFileName()));
            }
            notifyDataSetChanged();
        }


    }


    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if(charSequence == null | charSequence.length() == 0){
                    filterResults.count = filesArrayList.size();
                    filterResults.values = filesArrayList;

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<FileInfoModel> resultData = new ArrayList<>();

                    for(FileInfoModel userModel: filesArrayList){
                        if(userModel.getFileName().toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                filesArrayList = (ArrayList<FileInfoModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }
}
