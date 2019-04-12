package com.cxp.record.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cxp.record.R;
import com.cxp.record.utils.network.NetUtil;

import java.io.File;
import java.io.IOException;

/**
 * 播音控制器
 */
public class MediaManager implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    public MediaPlayer mPlayer;
    public boolean isPause;
    private Context mContext;
    private String url;
    private boolean hasTrackSeek;
    /**
     * 此handler是为了播放时倒数计时，UI如果不需要可以删掉
     */
    private Handler mHandler;
    private boolean isFinished;
    private boolean isPrepared;
    public int audioDuration;
    private SeekBar seekBar;
    private TextView progress;


    public MediaManager(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.mHandler = handler;
    }

    public interface OnPlayListener {
        void onCompletion(MediaPlayer mp);

        void onPlay(MediaPlayer mp);

        void onError();
    }

    /**
     * 加载本地资源
     *
     * @param path
     * @param playListener
     */
    public void playSound(String path, final OnPlayListener playListener) {
        if (!new File(path).exists()) {
            if (playListener != null) {
                playListener.onError();
            }
            return;
        }
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mPlayer.reset();
                    playListener.onError();
                    return false;
                }
            });
        } else {
            mPlayer.reset();
        }
        final Runnable progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                while (mPlayer.isPlaying() || hasTrackSeek) {
                    progress.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mPlayer.getCurrentPosition());
                            progress.setText("00:" + ((mPlayer.getCurrentPosition() / 1000) < 10 ? "0" + mPlayer.getCurrentPosition() / 1000 : mPlayer.getCurrentPosition() / 1000) + "/00:" +
                                    ((mPlayer.getDuration() / 1000) < 10 ? "0" + mPlayer.getDuration() / 1000 : mPlayer.getDuration() / 1000));
                        }
                    });
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                progress.post(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(mPlayer.getCurrentPosition());
                        progress.setText("00:" + ((mPlayer.getCurrentPosition() / 1000) < 10 ? "0" + mPlayer.getCurrentPosition() / 1000 : mPlayer.getCurrentPosition() / 1000) + "/00:" +
                                ((mPlayer.getDuration() / 1000) < 10 ? "0" + mPlayer.getDuration() / 1000 : mPlayer.getDuration() / 1000));
                    }
                });
            }
        };
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    hasTrackSeek = false;
                    playListener.onCompletion(mediaPlayer);
                    progress.setText("00:" + ((mPlayer.getDuration() / 1000) < 10 ? "0" + mPlayer.getDuration() / 1000 : mPlayer.getDuration() / 1000) + "/00:" +
                            ((mPlayer.getDuration() / 1000) < 10 ? "0" + mPlayer.getDuration() / 1000 : mPlayer.getDuration() / 1000));
                }
            });
            mPlayer.setDataSource(path);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    if (playListener != null) {
                        playListener.onPlay(mediaPlayer);
                    }
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    final View view = View.inflate(mContext, R.layout.layout_controler, null);
                    seekBar = view.findViewById(R.id.seek);
                    progress = view.findViewById(R.id.tv_progress);
                    seekBar.setMax(mPlayer.getDuration());
                    progress.setText("00:" + ((mediaPlayer.getCurrentPosition() / 1000) < 10 ? "0" + mediaPlayer.getCurrentPosition() / 1000 : mediaPlayer.getCurrentPosition() / 1000) + "/00:" +
                            ((mPlayer.getDuration() / 1000) < 10 ? "0" + mPlayer.getDuration() / 1000 : mPlayer.getDuration() / 1000));
                    seekBar.setProgress(mPlayer.getCurrentPosition());
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if (b) {
                                hasTrackSeek = b;
                                mPlayer.seekTo(i);
                                if (!mPlayer.isPlaying()) {
                                    mediaPlayer.start();
                                    new Thread(progressUpdateRunnable).start();
                                }
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                    adb.setView(view);
                    adb.show();
                    mPlayer.start();
                    new Thread(progressUpdateRunnable).start();
                }
            });
            mPlayer.prepare();
//            mPlayer.start();
            if (null != mHandler) {
                mHandler.sendMessage(Message.obtain(mHandler, 2, 1));
            }
            Log.e("tag", "time=" + mPlayer.getDuration());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载网络资源
     */
    public void play(String url) {
        try {
            this.url = url;
            mPlayer = new MediaPlayer();
            mPlayer.setLooping(false);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setDataSource(mContext, Uri.parse(url));
            mPlayer.prepareAsync();
        } catch (IOException e) {
            Log.v("AudioHttpPlayer", e.getMessage());
        }
    }


    public boolean isPlaying() {
        if (mPlayer != null && isPrepared) {
            return mPlayer.isPlaying();
        }
        return false;
    }


    //停止函数
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    //继续
    public void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
            if (null != mHandler) {
                mHandler.postDelayed(r, 1000);
            }
        }
    }


    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (r != null && null != mHandler) {
            mHandler.removeCallbacks(r);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("main", "  MediaManager====  onCompletion");
        mediaStop();
    }

    public void mediaStop() {
        isFinished = true;
        mPlayer.seekTo(0);
        mPlayer.reset();
        if (null != mHandler) {
            mHandler.removeCallbacks(r);
            mHandler.sendMessage(Message.obtain(mHandler, 2, 0));
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mPlayer.stop();
        mPlayer.release();
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        mediaStart(mp);
    }

    public void mediaStart(MediaPlayer mp) {
        Log.i("main", "mediaStart== " + mp.getDuration());
        audioDuration = mp.getDuration();
        mp.seekTo(0);
        mp.start();
        if (null != mHandler) {
            mHandler.removeCallbacks(r);
            mHandler.sendMessage(Message.obtain(mHandler, 2, 1));
            mHandler.postDelayed(r, 1000);
        }
    }

    private Runnable r = new Runnable() {

        @Override
        public void run() {
            if (isPlaying()) {
                Log.i("main", mPlayer.getCurrentPosition() + "  ====   " + mPlayer.getDuration());
                if (null != mHandler) {
                    mHandler.sendMessage(Message.obtain(mHandler, 1, mPlayer.getCurrentPosition()));
                    mHandler.postDelayed(r, 1000);
                }
            }
        }
    };


    public void play() {
        //首次播放缓存至本地，录音后替换
        if (!isPrepared) {     //如果已经播放过了，不在检查联网
            if (NetUtil.netIsAble(mContext) == -1) {
                return;
            }
        }
        if (mPlayer != null) {
            if (!mPlayer.isPlaying()) {
                if (!isFinished) {// 如果onCompletion()后直接开始/ stop后 准备在开始
                    mPlayer.prepareAsync();
                } else {
                    mediaStart(mPlayer);
                }
            } else {
                isFinished = false;
                mPlayer.stop();
                if (null != mHandler) {
                    mHandler.removeCallbacks(r);
                    mHandler.sendMessage(Message.obtain(mHandler, 2, 0));
                }
            }
        }
    }


    public void stop() {
        isPrepared = false;
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            //mPlayer.release();
            if (null != mHandler) {
                mHandler.removeCallbacks(r);
                mHandler.sendMessage(Message.obtain(mHandler, 2, 0));
            }
        }
    }

    public void destory() {
        if (mPlayer != null) {
            if (isPrepared && mPlayer.isPlaying()) {
                mPlayer.stop();
                if (null != mHandler) {
                    mHandler.removeCallbacks(r);
                    mHandler.sendMessage(Message.obtain(mHandler, 2, 0));
                }
            }
            mPlayer.release();
        }
    }
}
