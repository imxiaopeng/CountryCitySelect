package com.example.countrycityselect;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cy.cyflowlayoutlibrary.FlowLayout;
import com.cy.cyflowlayoutlibrary.FlowLayoutAdapter;

import java.util.ArrayList;

public class TagsActivity extends AppCompatActivity {
    private ArrayList<String> list;
    private FlowLayoutAdapter<String> adapter;
    private FlowLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        fl = (FlowLayout) findViewById(R.id.fl);
        list = new ArrayList<>();
        list.add("标签1");
        list.add("标签22");
        list.add("标签33333");
        list.add("标签4");
        list.add("我是长标签标签标签标签");
        list.add("我是短标签标签");
        adapter = new FlowLayoutAdapter<String>(list) {
            @Override
            public void bindDataToView(FlowLayoutAdapter.ViewHolder holder, int position, String bean) {
                holder.setText(R.id.tv, bean);
            }

            @Override
            public void onItemClick(final int position, final String bean) {
                final EditText et = new EditText(TagsActivity.this);
                et.setText(bean);
                et.setSelection(et.getText().length());
                new AlertDialog.Builder(TagsActivity.this).setTitle("请输入消息")
                        .setIcon(R.mipmap.ic_launcher)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String tag = et.getText().toString().trim();
                                if (TextUtils.isEmpty(tag)) {
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    return;
                                }
                                list.set(position, et.getText().toString().trim());
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null).show();
            }

            @Override
            public int getItemLayoutID(int position, String bean) {
                return R.layout.item_tv_tag;
            }
        };
        fl.setAdapter(adapter);
    }

    public void addTag(View view) {
        list.add("我是新增的标签");
        adapter.notifyDataSetChanged();
    }
}
