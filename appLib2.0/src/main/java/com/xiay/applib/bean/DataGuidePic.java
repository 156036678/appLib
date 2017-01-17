package com.xiay.applib.bean;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DataGuidePic implements Parcelable {
    public ArrayList<String> pics=new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.pics);
    }

    public DataGuidePic() {
    }

    protected DataGuidePic(Parcel in) {
        this.pics = in.createStringArrayList();
    }

    public static final Parcelable.Creator<DataGuidePic> CREATOR = new Parcelable.Creator<DataGuidePic>() {
        @Override
        public DataGuidePic createFromParcel(Parcel source) {
            return new DataGuidePic(source);
        }

        @Override
        public DataGuidePic[] newArray(int size) {
            return new DataGuidePic[size];
        }
    };
}
