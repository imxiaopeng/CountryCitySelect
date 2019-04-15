package com.cxp.common_library.record.utils;

import android.os.Environment;

import com.cxp.common_library.record.interfaces.RecordListener;

import java.io.File;

/**
 * map3格式转换器
 */
public class RecorderManager extends MyMP3Record {
    public File mRecorderFile;
    public static String mPath = Environment.getExternalStorageDirectory() + "/AAA_record";
    private static String key;//向后台传递的key
    private static RecorderManager mInstance;
    private String mTime = "1＂";//录音时长
    private long startTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    private long endTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public RecorderManager(File recordFile, RecordListener listener) {
        super(recordFile, listener);
        mRecorderFile = recordFile;

    }

    public RecorderManager setPath(String path) {
        mPath = path;
        return this;
    }

    public String getAbsolutePath() {
        return mRecorderFile.getAbsolutePath();
    }

    public static RecorderManager getInstance(String name, RecordListener listener) {
        synchronized (RecorderManager.class) {
            key = name + ".mp3";
            File dirs = new File(mPath);
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            File file = new File(mPath, name + ".mp3");
            mInstance = new RecorderManager(file, listener);
        }
        return mInstance;
    }

    @Override
    public void stop() {
        super.stop();
        setEndTime(System.currentTimeMillis());
    }


    public boolean isRecordFileExist() {
        return mRecorderFile.exists();
    }

    public void deleteRecordFile() {
        if (isRecordFileExist()) {
            mRecorderFile.delete();
        }
    }

    public String getRecordFilePath() {
        if (mRecorderFile != null) {
            return mRecorderFile.getAbsolutePath();
        }
        return null;
    }

    public int getVoiceLevel() {
        try {
            if (isRecording()) {
                //1-32767
                int voi = getRealVolume();
                //  Log.i("main","===getRealVolume====   "+voi+"  "+getVolume());

                if (voi <= 500) {
                    return 1;
                } else if (voi < 1500) {
                    return 2;
                } else if (voi < 2600) {
                    return 3;
                } else if (voi < 3700) {
                    return 4;
                } else if (voi < 4800) {
                    return 5;
                } else if (voi < 6000) {
                    return 6;
                } else {
                    return 7;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    public void setTime(String time) {
        mTime = time.equals("0＂") ? "1＂" : time;
    }

    public String getTime() {
        return mTime;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
