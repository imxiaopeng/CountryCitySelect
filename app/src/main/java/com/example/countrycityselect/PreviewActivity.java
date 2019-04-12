package com.example.countrycityselect;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.jzvd.JZMediaManager;
import cn.jzvd.Jzvd;

public class PreviewActivity extends AppCompatActivity {

    private LinearLayout llTop;
    private ArrayList<PreviewFragment> fragments;
    private ArrayList<PreviewBean> list;
    private PreviewAd ad;
    private int currentItem;

    public LinearLayout getLlTop() {
        return llTop;
    }

    public boolean isAnimEnd() {
        return animEnd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView ivDelete = findViewById(R.id.iv_delete);
        final TextView tvIndex = findViewById(R.id.tv_index);
        llTop = findViewById(R.id.ll_top);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (v != 0) {
                    if (animEnd) {
                        if (llTop.getVisibility() == View.VISIBLE) {
                            hide();
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int i) {
                currentItem = i;
                tvIndex.setText((i + 1) + "/" + list.size());
                fragments.get(i).reset();
                try {
                    JZMediaManager.pause();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        boolean enableDelete = getIntent().getBooleanExtra("enableDelete", false);
        ivDelete.setVisibility(enableDelete ? View.VISIBLE : View.INVISIBLE);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() == 1) {
                    //全部删除完成
                    sendNotifyDeleteAll();
                    finish();
                    return;
                }
                PreviewBean remove = list.remove(viewPager.getCurrentItem());
                sendNotify(remove);
                fragments.remove(viewPager.getCurrentItem());
                ad.notifyDataSetChanged();
                tvIndex.setText(viewPager.getCurrentItem() + 1 + "/" + fragments.size());
            }
        });
        list = getIntent().getParcelableArrayListExtra("list");
        int position = getIntent().getIntExtra("position", 0);
        fragments = new ArrayList<>();
        for (PreviewBean previewBean : list) {
            PreviewFragment fragment = new PreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", previewBean);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
//        bundle.putParcelable("data", new PreviewBean(false,"https://suixingyou.oss-cn-shanghai.aliyuncs.com/upload/video/rc-upload-1551422062194-13_2.mp4"));
        ad = new PreviewAd(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(ad);
        viewPager.setCurrentItem(position > fragments.size() - 1 ? fragments.size() - 1 : position);
        tvIndex.setText((position > fragments.size() - 1 ? fragments.size() : position + 1) + "/" + list.size());
    }

    /**
     * 通知调用者清空数据
     */
    private void sendNotifyDeleteAll() {
        EventBus.getDefault().post("clear");
    }

    /**
     * 通知调用者 删除该条数据
     *
     * @param remove
     */
    private void sendNotify(PreviewBean remove) {
        EventBus.getDefault().post(remove);
    }

    @Override
    protected void onStart() {
        super.onStart();
        llTop.postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, 1000);
    }

    boolean animEnd = false;//动画是否结束

    public void hide() {
        llTop.animate().translationY(-llTop.getMeasuredHeight()).setDuration(400).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animEnd = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.parseColor("#000000"));
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                llTop.setVisibility(View.GONE);
                animEnd = true;
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void show() {
        llTop.animate().translationY(0).setDuration(400).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    llTop.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWindow().setStatusBarColor(Color.parseColor("#333333"));
                        }
                    }, 50);
                }
                animEnd = false;
                llTop.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    class PreviewAd extends FragmentStatePagerAdapter {
        protected final ArrayList<PreviewFragment> fragments;

        PreviewAd(FragmentManager fm, ArrayList<PreviewFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            JZMediaManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JZMediaManager.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Jzvd.releaseAllVideos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openPreview(ArrayList<PreviewBean> list, Context context, int position) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putParcelableArrayListExtra("list", list);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void openPreviewWithDelete(ArrayList<PreviewBean> list, Context context, int position) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putParcelableArrayListExtra("list", list);
        intent.putExtra("enableDelete", true);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }
}
