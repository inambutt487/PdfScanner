package me.sid.smartcropper.adapters;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.ImageFilterView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import me.sid.smartcropper.R;
import me.sid.smartcropper.interfaces.FilterListener;

public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.ViewHolder> {

    private FilterListener mFilterListener;
    private List<Pair<String, PhotoFilter>> mPairList = new ArrayList<>();
    private Bitmap bitmap;


    private boolean status= false;


    public FilterViewAdapter(FilterListener filterListener, List<Pair<String, PhotoFilter>> mPairList, Bitmap bitmap) {
        mFilterListener = filterListener;
        this.mPairList = mPairList;
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Pair<String, PhotoFilter> filterPair = mPairList.get(position);
        holder.mImageFilterView.setFilterEffect(mPairList.get(position).second);
        holder.mImageFilterView.setSourceBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mPairList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageFilterView mImageFilterView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageFilterView = new ImageFilterView(itemView.getContext());
            mImageFilterView = itemView.findViewById(R.id.imgFilterView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterListener.onFilterSelected(mPairList.get(getLayoutPosition()).second);
                }
            });
        }
    }

    public void updateFilter(Bitmap bitmap){
        this.bitmap = bitmap;
        notifyDataSetChanged();
    }


}
