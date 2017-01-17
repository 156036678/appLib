package com.xiay.applib.bean;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.xiay.applib.util.AppUtil;

import java.io.File;

/**
 * @Xaiy
 */
public class DownFileInfo  implements Parcelable{
    /**
     * @param url 下载地址
     * @param fileFolder 保存的文件夹
     * @param fileName 文件名
     * @param isRange 是否断点续传下载
     * @param isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功
     * @param downloadListener
     */
    public String url;
    public String fileFolder;
    public String fileName;
    public boolean isRange=true;
    public boolean isDeleteOld;


    public DownFileInfo() {
    }
    public DownFileInfo(String url,String fileName,String fileFolder) {
        this.url=url;
        this.fileName=fileName;
        this.fileFolder=fileFolder;
    }

    /**
     * fileFolder dir: /data/data/package/fileName
     * @param context
     * @return
     */
    public DownFileInfo(Context context,String url, String fileName) {
        this.url=url;
        this.fileName=fileName;
        this.fileFolder=getDataDir(context);
    }
    public String getDataDir(Context context){
        return  new File(AppUtil.getDataDirectory(context), fileName).getAbsolutePath();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.fileFolder);
        dest.writeString(this.fileName);
        dest.writeByte(this.isRange ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDeleteOld ? (byte) 1 : (byte) 0);
    }

    protected DownFileInfo(Parcel in) {
        this.url = in.readString();
        this.fileFolder = in.readString();
        this.fileName = in.readString();
        this.isRange = in.readByte() != 0;
        this.isDeleteOld = in.readByte() != 0;
    }

    public static final Creator<DownFileInfo> CREATOR = new Creator<DownFileInfo>() {
        @Override
        public DownFileInfo createFromParcel(Parcel source) {
            return new DownFileInfo(source);
        }

        @Override
        public DownFileInfo[] newArray(int size) {
            return new DownFileInfo[size];
        }
    };
}
