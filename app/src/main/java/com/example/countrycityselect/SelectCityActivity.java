package com.example.countrycityselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity {
    private RecyclerView rv;
    private Ad ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        rv = (RecyclerView) findViewById(R.id.rv);
        List<CityBean> list =null;
        Type listType = new TypeToken<List<CityBean>>() {
        }.getType();
        list = new Gson().fromJson(getJson("world.json", this), listType);
        int index_country = getIntent().getIntExtra("index_country",0);
        ad = new Ad(this,list, Ad.FLAG_STATE);
        ad.setIndex_country(index_country);
        rv.setAdapter(ad);
        ad.setOnResultListener(new Ad.OnResultListener() {
            @Override
            public void onResult(Area area) {
                Intent data=new Intent();
                data.putExtra("data",area);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }
    public static void selectCity(Activity context, int requestCode){
        Intent intent=new Intent(context,SelectCityActivity.class);
        context.startActivityForResult(intent,requestCode);
    }
    public static void selectCity(Activity context,int indexCountry, int requestCode){
        Intent intent=new Intent(context,SelectCityActivity.class);
        intent.putExtra("index_country",indexCountry);
        context.startActivityForResult(intent,requestCode);
    }
    @Override
    public void onBackPressed() {
        switch (ad.getFlag()) {
            case Ad.FLAG_COUNTRY:
                super.onBackPressed();
                break;
            case Ad.FLAG_STATE:
                super.onBackPressed();
                break;
            case Ad.FLAG_CITY:
                ad.setFlag(Ad.FLAG_STATE);
                ad.notifyDataSetChanged();
                break;
        }
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
