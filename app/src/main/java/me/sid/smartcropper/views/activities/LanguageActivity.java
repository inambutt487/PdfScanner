package me.sid.smartcropper.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.sid.smartcropper.R;
import me.sid.smartcropper.adapters.CountryAdapter;
import me.sid.smartcropper.utils.Country.Country;
import me.sid.smartcropper.utils.Country.CountryListAdapter;
import me.sid.smartcropper.utils.StringUtils;
import me.sid.smartcropper.utils.TinyDB;

public class LanguageActivity extends BaseActivity implements CountryListAdapter.OnItemClick {

    private ImageView backimg, premiumImg;

    private ArrayList<Country> mList;
    private RecyclerView lvCountry;

    private  String value = null;
    private  String text = null;
    private  String translated = null;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Language");

        tinyDB =new  TinyDB(LanguageActivity.this);

        hideKeyboard(LanguageActivity.this);

        Intent intent = getIntent();

        if(intent != null){
            value = intent.getStringExtra("source");
            text = intent.getStringExtra("text");
            translated = intent.getStringExtra("translated");
        }


        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        backimg = findViewById(R.id.backimg);
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LanguageActivity.this, TranslationActivity.class).putExtra("text", text));
                finish();
            }
        });

        if(!havePurchase()){
            premiumImg = findViewById(R.id.premiumImg);
            premiumImg.setVisibility(View.VISIBLE);
            premiumImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!havePurchase()){
                        if(!haveNetworkConnection()){
                            StringUtils.getInstance().showSnackbar(LanguageActivity.this, "No Internet Connection!");
                        }else{
                            startActivity(new Intent(LanguageActivity.this, PremiumActivity.class));
                        }
                    }else{
                        StringUtils.getInstance().showSnackbar(LanguageActivity.this, "Already Purchase!");
                    }
                }
            });
        }



        new Thread(new Runnable() {

            @Override
            public void run() {
                // Do the processing.

                loadData();


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        lvCountry = (RecyclerView) findViewById(R.id.filesRecyclerView);
                        lvCountry.setLayoutManager(new LinearLayoutManager(LanguageActivity.this));
                        CountryAdapter adapter = new CountryAdapter(LanguageActivity.this, mList, LanguageActivity.this);
                        lvCountry.setAdapter(adapter);


                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {

                                if(newText.isEmpty() || newText == null){
                                    loadData();
                                    adapter.updateData(mList);
                                }else{
                                    adapter.getFilter().filter(newText);
                                }
                                return false;
                            }
                        });


                        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
                            @Override
                            public boolean onClose() {
                                loadData();
                                adapter.updateData(mList);
                                return false;
                            }
                        };


                    }
                });

            }
        }).start();


    }

    private void loadData() {

        mList = new ArrayList<>();
        mList.clear();

        List<String> myArrayList = Arrays.asList(getResources().getStringArray(R.array.lang_array_bcp_47_code));
        List<String> myArraylanguage = Arrays.asList(getResources().getStringArray(R.array.lang_array_name_local));
        List<String> mylanguage = Arrays.asList(getResources().getStringArray(R.array.lang_array_name_local2));

        //sorting
        for(int i = 0; i < myArrayList.size() ; i++){
            Log.d("string is",myArrayList.get(i));

            String currentString = myArrayList.get(i);
            String[] separated = currentString.split("-");
            String language_code = separated[0]; // this will contain language code
            String country_code = separated[1];
            String country_name = separated[2];
            String language_name = myArraylanguage.get(i);
            String language_country =  mylanguage.get(i);

            Log.d("language_code",language_code);
            Log.d("country_code",country_code);
            Log.d("country_name",country_name);
            mList.add(new Country(language_code, country_name, country_code, language_name, language_country));

        }

        Collections.sort(mList, new Comparator<Country>() {

            @Override
            public int compare(Country a1, Country a2) {

                // String implements Comparable
                return (a1.getName().toString()).compareTo(a2.getName().toString());
            }
        });

    }

    @Override
    public void onBackPressed() {
       finish();
    }

    @Override
    public void onClick(Country country) {

        //Pending
        tinyDB.putString(value, country.getCode());

        ArrayList<String> selectedLanguage = new ArrayList<>();
        selectedLanguage.add(country.getLanguage());
        selectedLanguage.add(country.getCode());
        tinyDB.putListString(value, selectedLanguage);

        startActivity(new Intent(LanguageActivity.this, TranslationActivity.class).putExtra("text", text).putExtra("translated", translated));
        finish();



    }
}