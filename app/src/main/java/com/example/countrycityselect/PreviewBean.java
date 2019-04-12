package com.example.countrycityselect;

import android.os.Parcel;
import android.os.Parcelable;

public class PreviewBean implements Parcelable {
    private boolean isPicture;
    private String path;

    public boolean isPicture() {
        return isPicture;
    }

    public void setPicture(boolean picture) {
        isPicture = picture;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PreviewBean(boolean isPicture, String path) {
        this.isPicture = isPicture;
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isPicture ? (byte) 1 : (byte) 0);
        dest.writeString(this.path);
    }

    protected PreviewBean(Parcel in) {
        this.isPicture = in.readByte() != 0;
        this.path = in.readString();
    }

    public static final Parcelable.Creator<PreviewBean> CREATOR = new Parcelable.Creator<PreviewBean>() {
        @Override
        public PreviewBean createFromParcel(Parcel source) {
            return new PreviewBean(source);
        }

        @Override
        public PreviewBean[] newArray(int size) {
            return new PreviewBean[size];
        }
    };
}
