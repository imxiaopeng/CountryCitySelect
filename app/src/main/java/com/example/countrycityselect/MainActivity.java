package com.example.countrycityselect;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.countrycityselect.SelectCityActivity.getJson;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    public static ArrayList<CityBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        Type listType = new TypeToken<List<CityBean>>() {
        }.getType();
        list = new Gson().fromJson(getJson("world.json", this), listType);
    }

    public void select(View view) {
        Intent intent = new Intent(this, CountryListActivity.class);
        intent.putExtra("selectCountry", "中国");//这里传入上次选择的国家
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Area area = data.getParcelableExtra("data");
            tv.setText(area.getCountry() + area.getState() + area.getCity());
        }
    }

    @Override
    protected void onDestroy() {
        list = null;
        super.onDestroy();
    }
}
