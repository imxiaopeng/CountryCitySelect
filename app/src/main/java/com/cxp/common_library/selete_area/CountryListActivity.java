package com.cxp.common_library.selete_area;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cxp.common_library.Ad;
import com.cxp.common_library.MainActivity;
import com.cxp.common_library.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cxp.common_library.selete_area.SelectCityActivity.getJson;

public class CountryListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private Ad ad;
    private String selectCountry = "中国";
    private ArrayList<CityBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countrylist);
        rv = (RecyclerView) findViewById(R.id.rv);
        list = MainActivity.list;
        selectCountry = getIntent().getStringExtra("selectCountry");
        Type listType = new TypeToken<List<CityBean>>() {
        }.getType();
        final List<CityBean> copyList = new Gson().fromJson(getJson("world.json", this), listType);
        ad = new Ad(this, list, Ad.FLAG_COUNTRY);
        ad.setEnableLoadMore(true);
        rv.setAdapter(ad);
        rv.postDelayed(new Runnable() {
            @Override
            public void run() {
                ad.setEnableLoadMore(false);
                int i = 0;
                for (CityBean cityBean : list) {
                    if (selectCountry.equals(cityBean.getCountryRegion().getName())) {
                        i = list.indexOf(cityBean);
                        break;
                    }
                }
                if (i != 0) {
                    Collections.swap(list, 0, i);
                }
                ad.notifyDataSetChanged();
            }
        }, 1000);
        ad.setOnItemClickListener(new Ad.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                int index = 0;
                for (CityBean cityBean : copyList) {
                    if (cityBean.getCountryRegion().getName().equals(list.get(i).getCountryRegion().getName())) {
                        index = copyList.indexOf(cityBean);
                    }
                }
                SelectCityActivity.selectCity(CountryListActivity.this, index, 1);
            }
        });
        ad.setOnResultListener(new Ad.OnResultListener() {
            @Override
            public void onResult(Area area) {
                selectCountry = area.getCountry();
                MainActivity.selectCountry = selectCountry;
                Log.e("--", "已选择地区" + area.getCountry() + "-" + area.getState() + "-" + area.getCity());
                Intent data = new Intent();
                data.putExtra("data", area);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Area area = data.getParcelableExtra("data");
            selectCountry = area.getCountry();
            MainActivity.selectCountry = selectCountry;
            setResult(RESULT_OK, data);
            finish();
            Log.e("--", "已选择地区" + area.getCountry() + "-" + area.getState() + "-" + area.getCity());
        }
    }
}
