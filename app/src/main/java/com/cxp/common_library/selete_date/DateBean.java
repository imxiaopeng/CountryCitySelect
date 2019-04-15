package com.cxp.common_library.selete_date;

import android.os.Parcel;
import android.os.Parcelable;

public class DateBean implements Parcelable {
    private int year, month, day;
    private boolean isSeleted;
    private boolean isStart, isEnd;

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public boolean equal(DateBean obj) {
        if (obj == null) {
            return false;
        }
        return (year == obj.year && month == obj.month && day == obj.day);
    }

    public boolean isBigWith(DateBean obj) {
        return year > obj.year || month > obj.month || day > obj.day;
    }

    public void setSeleted(boolean seleted) {
        isSeleted = seleted;
    }

    @Override
    public String toString() {
        return year +
                "年" + month +
                "月" + day + "日";
    }

    public String formatHHMMDD() {
        return year + "-" + month + "-" + day;
    }

    public DateBean() {
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public DateBean(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeByte(this.isSeleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isStart ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEnd ? (byte) 1 : (byte) 0);
    }

    protected DateBean(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.isSeleted = in.readByte() != 0;
        this.isStart = in.readByte() != 0;
        this.isEnd = in.readByte() != 0;
    }

    public static final Creator<DateBean> CREATOR = new Creator<DateBean>() {
        @Override
        public DateBean createFromParcel(Parcel source) {
            return new DateBean(source);
        }

        @Override
        public DateBean[] newArray(int size) {
            return new DateBean[size];
        }
    };
}
