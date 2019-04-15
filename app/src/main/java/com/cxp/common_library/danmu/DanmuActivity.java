package com.cxp.common_library.danmu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cxp.common_library.R;

import java.util.ArrayList;


public class DanmuActivity extends AppCompatActivity {

    private BarrageView barrageView;
    private ArrayList<BarrageViewBean> barrageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmu);
        barrageView = findViewById(R.id.barrageview);
        init();
    }

    private void init() {
        barrageViews = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            barrageViews.add(new BarrageViewBean("我是标签" + (i + 1)));
        }
        barrageView.setData(barrageViews);
        barrageView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barrageView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barrageView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barrageView.onDestroy();
    }
}
