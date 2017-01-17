package com.xiay.applib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Xaiy
 */
public class AppUpdateInfo implements Parcelable{
    private  String downName,downUrl;
    private int iconRersource;

    public String getDownName() {
        return downName;
    }

    public void setDownName(String downName) {
        this.downName = downName;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getIconRersource() {
        return iconRersource;
    }

    public void setIconRersource(int iconRersource) {
        this.iconRersource = iconRersource;
    }

    public AppUpdateInfo(String downName, int iconRersource, String downUrl) {
        this.downName = downName;
        this.downUrl = downUrl;
        this.iconRersource = iconRersource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.downName);
        dest.writeString(this.downUrl);
        dest.writeInt(this.iconRersource);
    }

    protected AppUpdateInfo(Parcel in) {
        this.downName = in.readString();
        this.downUrl = in.readString();
        this.iconRersource = in.readInt();
    }

    public static final Creator<AppUpdateInfo> CREATOR = new Creator<AppUpdateInfo>() {
        @Override
        public AppUpdateInfo createFromParcel(Parcel source) {
            return new AppUpdateInfo(source);
        }

        @Override
        public AppUpdateInfo[] newArray(int size) {
            return new AppUpdateInfo[size];
        }
    };
}
