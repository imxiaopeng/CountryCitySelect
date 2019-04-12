package com.example.countrycityselect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.OnViewDragListener;
import com.github.chrisbanes.photoview.PhotoView;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;

public class PreviewFragment extends Fragment {

    private View root;
    private PhotoView photoView;
    private MyJzvdStd videoPlayer;
    private PreviewActivity activity;
    private String path;
    private boolean isPicture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_preview, container, false);
        }
        initView();
        initData();
        return root;
    }

    private void initData() {
        activity = (PreviewActivity) getActivity();
        Bundle arguments = getArguments();
        if (arguments == null) {
            Toast.makeText(activity, "参数错误", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }
        PreviewBean data = arguments.getParcelable("data");
        if (data == null) {
            Toast.makeText(activity, "参数错误", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }
        isPicture = data.isPicture();
        path = data.getPath();
        if (isPicture) {
            photoView.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.GONE);
        } else {
            photoView.setVisibility(View.GONE);
            videoPlayer.setVisibility(View.VISIBLE);
            JZDataSource jzDataSource = new JZDataSource(path);
            jzDataSource.looping = true;
            videoPlayer.setUp(jzDataSource, Jzvd.SCREEN_WINDOW_NORMAL);
        }
        Glide.with(this).load(path)
                .apply(new RequestOptions().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_round))
                .into(photoView);
        photoView.setOnViewDragListener(new OnViewDragListener() {
            @Override
            public void onDrag(float dx, float dy) {
                photoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (activity.isAnimEnd()) {
                            if (activity.getLlTop().getVisibility() == View.VISIBLE) {
                                activity.hide();
                            }
                        }
                    }
                }, 150);
            }
        });
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (activity.isAnimEnd()) {
                            if (activity.getLlTop().getVisibility() == View.VISIBLE) {
                                activity.hide();
                            } else {
                                activity.show();
                            }
                        }
                    }
                }, 150);
            }
        });
    }

    private void initView() {
        photoView = root.findViewById(R.id.photo_view);
        videoPlayer = root.findViewById(R.id.videoPlayer);
        videoPlayer.setSingleTapListener(new MyJzvdStd.OnSingleTapListener() {
            @Override
            public void onSingleTab() {
                videoPlayer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (activity.isAnimEnd()) {
                            if (activity.getLlTop().getVisibility() == View.VISIBLE) {
                                activity.hide();
                            } else {
                                activity.show();
                            }
                        }
                    }
                }, 150);
            }
        });
    }

    public void reset() {
        if (photoView != null) {
            photoView.setScale(1f);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!isPicture && !TextUtils.isEmpty(path)) {
                Jzvd.releaseAllVideos();
                JZDataSource jzDataSource = new JZDataSource(path);
                jzDataSource.looping = true;
                videoPlayer.setUp(jzDataSource, Jzvd.SCREEN_WINDOW_NORMAL);
                videoPlayer.startVideo();
            }
        }
    }
}
