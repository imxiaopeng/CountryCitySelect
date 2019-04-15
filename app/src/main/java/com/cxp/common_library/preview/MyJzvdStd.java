package com.cxp.common_library.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.cxp.common_library.R;
import com.cxp.common_library.ScreenTool;
import com.danikula.videocache.HttpProxyCacheServer;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZTextureView;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 这里可以监听到视频播放的生命周期和播放状态
 * 所有关于视频的逻辑都应该写在这里
 */
public class MyJzvdStd extends JzvdStd {
    private Listener listener;
    private HttpProxyCacheServer mProxyCacheServer;
    private Surface mSurface;
    private GestureDetector detector;
    private Context mContext;

    public MyJzvdStd(Context context) {
        super(context);
    }

    private OnSingleTapListener singleTapListener;

    public MyJzvdStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mContext = context;
        detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (singleTapListener != null) {
                    singleTapListener.onSingleTab();
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        mProxyCacheServer = new HttpProxyCacheServer(context);
    }

    @Override
    public void setUp(JZDataSource jzDataSource, int screen) {
        String url = (String) jzDataSource.getCurrentUrl();
        try {
            if (!url.contains("http") && !url.contains("https")) {
                jzDataSource.looping = true;
                super.setUp(jzDataSource, screen);
            } else {
                jzDataSource = new JZDataSource(mProxyCacheServer.getProxyUrl(url));
                jzDataSource.looping = true;
                super.setUp(jzDataSource, screen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return currentState == Jzvd.CURRENT_STATE_PLAYING;
    }

    public interface OnSingleTapListener {
        void onSingleTab();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    public void setSingleTapListener(OnSingleTapListener singleTapListener) {
        this.singleTapListener = singleTapListener;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == cn.jzvd.R.id.fullscreen) {
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //click quit fullscreen
            } else {
                //click goto fullscreen
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.jz_mlayout_standard;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

    @Override
    public void startVideo() {
        super.startVideo();
    }

    //onState 代表了播放器引擎的回调，播放视频各个过程的状态的回调
    @Override
    public void onStateNormal() {
        super.onStateNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        if (listener != null) {
            listener.onPrepared();
        }
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
    }

    @Override
    public void onStateError() {
        super.onStateError();
        if (listener != null) {
            listener.onError();
        }
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
    }

    //changeUiTo 真能能修改ui的方法
    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
    }

    @Override
    public void changeUiToPauseClear() {
        super.changeUiToPauseClear();
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

    @Override
    public void startWindowFullscreen() {
        super.startWindowFullscreen();
    }

    @Override
    public void startWindowTiny() {
        super.startWindowTiny();
    }

    @Override
    public void onVideoSizeChanged() {
        super.onVideoSizeChanged();
        if (listener != null) {
            listener.onVideoSizeChanged();
        }
        JZTextureView textureView = JZMediaManager.textureView;
        ViewGroup.LayoutParams params = textureView.getLayoutParams();
        int currentVideoWidth = JZMediaManager.instance().currentVideoWidth;
        int currentVideoHeight = JZMediaManager.instance().currentVideoHeight;
        int screenWidth = ScreenTool.getScreenWidth(mContext);
        int screenHeight = ScreenTool.getScreenHeight(mContext);
        float videoR = currentVideoWidth * 1.0f / currentVideoHeight;
        float screenR = screenWidth * 1.0f / screenHeight;
        float r = videoR / screenR;
        if (r >= 0.95 && r <= 1.6) {
            Jzvd.VIDEO_IMAGE_DISPLAY_TYPE = Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT;
            params.width = screenWidth;
            params.height = screenHeight;
        } else {
            Jzvd.VIDEO_IMAGE_DISPLAY_TYPE = Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER;
            params.width = screenWidth;
            params.height = (int) (screenWidth * 1.0f / currentVideoWidth * currentVideoHeight);
        }
        textureView.setLayoutParams(params);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onVideoSizeChanged();

        void onPrepared();

        void onError();
    }
}
