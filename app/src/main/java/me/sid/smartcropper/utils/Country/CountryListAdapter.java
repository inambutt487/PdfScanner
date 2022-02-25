package me.sid.smartcropper.utils.Country;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.sid.smartcropper.R;
import me.sid.smartcropper.utils.FlagDrawableProvider;
import me.sid.smartcropper.utils.Flags;

public class CountryListAdapter extends BaseAdapter {

    private final Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Country> items;
    private ArrayList<Country> itemsCopy;
    private OnItemClick mCallback;

    public CountryListAdapter(Context context, ArrayList<Country> items, OnItemClick listener) {
        mContext = context;
        this.items = items;
        this.itemsCopy = items;
        this.mCallback = listener;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        Item item;
        Country country = items.get(position);

        if (convertView == null) {
            item = new Item();
            itemView = inflater.inflate(R.layout.item_country, parent, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onClick(country);
                }
            });
            item.setName((TextView) itemView.findViewById(R.id.tvName));
            item.setLanguage((TextView) itemView.findViewById(R.id.tvLanguage));
            item.setLanguageFlage((ImageView) itemView.findViewById(R.id.languageFlage));
            itemView.setTag(item);
        } else {
            item = (Item) itemView.getTag();
        }

        item.getName().setText(country.getName());
        item.getLanguage().setText(country.getLanguage());


        try {

            FlagDrawableProvider flagProvider = new Flags.Builder(itemView.getContext())
                    .build();
            BitmapDrawable usFlag = flagProvider.forCountry(country.getCountry());

            item.getLanguageFlage().setImageDrawable(usFlag);

        }catch (Exception e){

        }


        return itemView;
    }

    public void updateData(ArrayList<Country> mList) {

        items = mList;
        notifyDataSetChanged();
    }

    public static class Item {

        private ImageView languageFlage;
        private TextView name;

        private TextView language;

        public TextView getLanguage() {
            return language;
        }

        public void setLanguage(TextView language) {
            this.language = language;
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public ImageView getLanguageFlage() {
            return languageFlage;
        }

        public void setLanguageFlage(ImageView languageFlage) {
            this.languageFlage = languageFlage;
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
                        if(userModel.getLanguage().toLowerCase().contains(searchChr)){
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

    public interface OnItemClick {
        void onClick (Country value);
    }
}
