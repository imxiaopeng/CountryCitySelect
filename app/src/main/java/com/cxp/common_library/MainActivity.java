package com.cxp.common_library;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cxp.common_library.danmu.DanmuActivity;
import com.cxp.common_library.preview.PreviewActivity;
import com.cxp.common_library.preview.PreviewBean;
import com.cxp.common_library.record.RecordActivity;
import com.cxp.common_library.selete_area.Area;
import com.cxp.common_library.selete_area.CityBean;
import com.cxp.common_library.selete_area.CountryListActivity;
import com.cxp.common_library.selete_date.DateBean;
import com.cxp.common_library.selete_date.SeleteDateActivity;
import com.cxp.common_library.selete_tags.TagsActivity;
import com.example.permission.PermissionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.cxp.common_library.selete_area.SelectCityActivity.getJson;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    public static ArrayList<CityBean> list;
    public static String selectCountry = "中国";
    private ArrayList<PreviewBean> previewBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        tv = (TextView) findViewById(R.id.tv);
        Type listType = new TypeToken<List<CityBean>>() {
        }.getType();
        list = new Gson().fromJson(getJson("world.json", this), listType);

        previewBeans = new ArrayList<>();
        previewBeans.add(new PreviewBean(true, "http://pic31.nipic.com/20130804/7487939_090818211000_2.jpg"));
        previewBeans.add(new PreviewBean(true, "http://suixingyou.oss-cn-shanghai.aliyuncs.com/images/merchandise/0123120420181225161529997/detail1.jpg"));
        previewBeans.add(new PreviewBean(true, "http://suixingyou.oss-cn-shanghai.aliyuncs.com/images/merchandise/0123120420181225161529997/detail2.jpg"));
        previewBeans.add(new PreviewBean(true, new File(Environment.getExternalStorageDirectory(), "a.jpg").getAbsolutePath()));
        previewBeans.add(new PreviewBean(true, "http://suixingyou.oss-cn-shanghai.aliyuncs.com/images/merchandise/0123120420181225161529997/detail3.jpg"));
        previewBeans.add(new PreviewBean(false, "https://suixingyou.oss-cn-shanghai.aliyuncs.com/upload/video/rc-upload-1551422062194-13_2.mp4"));
        previewBeans.add(new PreviewBean(false, new File(Environment.getExternalStorageDirectory(), "a.mp4").getAbsolutePath()));
    }

    public void selectArea(View view) {
        Intent intent = new Intent(this, CountryListActivity.class);
        intent.putExtra("selectCountry", selectCountry);//这里传入上次选择的国家
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //选择地区
            Area area = data.getParcelableExtra("data");
            tv.setText(area.getCountry() + area.getState() + area.getCity());
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            //选择日期
            DateBean start = data.getParcelableExtra("start");
            DateBean end = data.getParcelableExtra("end");
            Toast.makeText(this, "已选择" + start.toString() + "-" + end.toString(), Toast.LENGTH_SHORT).show();
            SeleteDateActivity.startBean = null;
            SeleteDateActivity.endBean = null;
        }
    }

    @Override
    protected void onDestroy() {
        list = null;
        super.onDestroy();
    }

    public void selectDate(View view) {
        SeleteDateActivity.open(2, this);
    }

    public void selectTag(View view) {
        Intent intent = new Intent(this, TagsActivity.class);
        startActivity(intent);
    }

    public void openDanmu(View view) {
        Intent intent = new Intent(this, DanmuActivity.class);
        startActivity(intent);
    }

    public void openPreview(View view) {
        if (previewBeans.isEmpty()) {
            Toast.makeText(this, "当前没有可预览数据", Toast.LENGTH_SHORT).show();
            return;
        }
        PermissionUtils.newInstance(this).checkPermissons(new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
//              PreviewActivity.openPreview(list,this,4);
                PreviewActivity.openPreviewWithDelete(previewBeans, MainActivity.this, 3);
            }
        }, Permission.WRITE_EXTERNAL_STORAGE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoveEvent(PreviewBean bean) {
        if (bean != null) {
            PreviewBean willDelete = null;
            for (PreviewBean previewBean : previewBeans) {
                if (bean.getPath().equals(previewBean.getPath())) {
                    willDelete = previewBean;
                    break;
                }
            }
            previewBeans.remove(willDelete);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListClearEvent(String text) {
        if (TextUtils.equals(text, "clear")) {
            previewBeans.clear();
        }
    }

    public void openRecord(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
}
