package com.cxp.common_library.record.interfaces;

public interface RecordListener {
    void hasRecordRight(long startTimeMillis);

    void noRecordRight();

    void onRecording();
}
