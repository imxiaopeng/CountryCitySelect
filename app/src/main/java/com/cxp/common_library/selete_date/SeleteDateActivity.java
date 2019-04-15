package com.cxp.common_library.selete_date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cxp.common_library.R;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SeleteDateActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<DateFragment> fragments;
    private ArrayList<String> titles;
    public static int month;
    public static int year;
    public static DateBean startBean, endBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selete_date);
        initView();
        getNetTime();
    }

    private void initFrgment() {
        fragments = new ArrayList<>();
        DateFragment month1 = new DateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("month", 0);
        month1.setArguments(bundle);

        DateFragment month2 = new DateFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("month", 1);
        month2.setArguments(bundle2);

        DateFragment month3 = new DateFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("month", 2);
        month3.setArguments(bundle3);
        fragments.add(month1);
        fragments.add(month2);
        fragments.add(month3);
        FrgAd ad = new FrgAd(getSupportFragmentManager());
        viewPager.setAdapter(ad);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    public static void open(int requestCode, Activity activity) {
        Intent intent = new Intent(activity, SeleteDateActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public void select(View view) {
        if (startBean == null) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endBean == null) {
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("start", startBean);
        intent.putExtra("end", endBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void reSelect(View view) {
        startBean = null;
        endBean = null;
        refreshOthers();
    }

    class FrgAd extends FragmentPagerAdapter {
        public FrgAd(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void getNetTime() {
        final ProgressDialog dialog = ProgressDialog.show(this, "加载网络时间...", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;//取得资源对象
                try {
                    url = new URL("http://www.baidu.com");
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect(); //发出连接
                    long ld = uc.getDate(); //取得网站日期时间
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(ld);
                    month = calendar.get(Calendar.MONTH);
                    year = calendar.get(Calendar.YEAR);
                    final String format = formatter.format(calendar.getTime());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titles = new ArrayList<>();
                            titles.add((month + 1) + "");
                            titles.add(((month + 2) % 12) == 0 ? "12" : ((month + 2) % 12) + "");
                            titles.add(((month + 3) % 12) == 0 ? "12" : ((month + 3) % 12) + "");
                            dialog.dismiss();
                            initFrgment();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void refreshOthers() {
        for (DateFragment fragment : fragments) {
            fragment.refresh();
        }
    }

    @Override
    public void onBackPressed() {
        startBean = null;
        endBean = null;
        super.onBackPressed();
    }
}
