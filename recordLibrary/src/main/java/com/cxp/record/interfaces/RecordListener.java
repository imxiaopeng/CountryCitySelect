package com.cxp.record.interfaces;

public interface RecordListener {
    void hasRecordRight(long startTimeMillis);

    void noRecordRight();

    void onRecording();
}
