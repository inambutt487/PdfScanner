package me.sid.smartcropper.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.Country.Country;
import me.sid.smartcropper.utils.Country.CountryListAdapter;
import me.sid.smartcropper.utils.FlagDrawableProvider;
import me.sid.smartcropper.utils.Flags;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyFilesHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Country> items;
    private ArrayList<Country> itemsCopy;
    private CountryListAdapter.OnItemClick mCallback;


    public CountryAdapter(Context context, ArrayList<Country> items, CountryListAdapter.OnItemClick listener) {
        mContext = context;
        this.items = items;
        this.itemsCopy = items;
        this.mCallback = listener;
    }


    @NonNull
    @Override
    public MyFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new MyFilesHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MyFilesHolder holder, int position) {

        Country country = items.get(position);

        holder.name.setText(country.getLanguage_country());
        holder.language.setText(country.getLanguage());


        try {

            FlagDrawableProvider flagProvider = new Flags.Builder(mContext)
                    .build();
            BitmapDrawable usFlag = flagProvider.forCountry(country.getCountry());

            holder.languageFlage.setImageDrawable(usFlag);

        }catch (Exception e){

        }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyFilesHolder extends RecyclerView.ViewHolder {

        private ImageView languageFlage;
        private TextView name;
        private TextView language;

        public MyFilesHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Country country = items.get(getAdapterPosition());
                    mCallback.onClick(country);
                }
            });

            name = ((TextView) itemView.findViewById(R.id.tvName));
            language = ((TextView) itemView.findViewById(R.id.tvLanguage));
            languageFlage = ((ImageView) itemView.findViewById(R.id.languageFlage));

        }
    }

    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();

                if(charSequence == null | charSequence.length() == 0){
                    filterResults.count = items.size();
                    filterResults.values = items;

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<Country> resultData = new ArrayList<>();

                    for(Country userModel: items){
                        if(userModel.getLanguage_country().toLowerCase().contains(searchChr)){
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

                items = (ArrayList<Country>) filterResults.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }

    public void updateData(ArrayList<Country> mList) {

        items = mList;
        notifyDataSetChanged();
    }

    public interface OnItemClick {
        void onClick (Country value);
    }
}
