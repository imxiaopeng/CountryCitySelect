package com.cxp.record;

import com.cxp.record.interfaces.RecordListener;
import com.cxp.record.utils.RecorderManager;

import java.io.File;
import java.io.IOException;

public class RecorderUtils {
    private static RecorderUtils utils;
    private final RecorderManager mp3Recorder;

    public RecorderManager getMp3Recorder() {
        return mp3Recorder;
    }

    private final OnRecordListener listener;

    public static RecorderUtils getInstance(OnRecordListener listener) {
        if (utils == null) {
            utils = new RecorderUtils(listener);
        }
        return utils;
    }

    public RecorderUtils(final OnRecordListener listener) {
        this.listener = listener;
        mp3Recorder = RecorderManager.getInstance(System.currentTimeMillis() + "", new RecordListener() {
            @Override
            public void hasRecordRight(long l) {
                mp3Recorder.setStartTime(l);
            }

            @Override
            public void noRecordRight() {
//                Toast.makeText(RecordActivity.this, "您无录音权限", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecording() {
                listener.onProgress(mp3Recorder.getRealVolume());
            }
        });

    }

    public void start() {
        try {
            mp3Recorder.start();
            listener.onStart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mp3Recorder.stop();
        listener.onStop(mp3Recorder.mRecorderFile);
    }

    public interface OnRecordListener {
        void onStart();

        /**
         * 获取当前分贝值
         *
         * @param db 分贝值 最大值7000
         */
        void onProgress(int db);

        void onStop(File file);
    }
}
