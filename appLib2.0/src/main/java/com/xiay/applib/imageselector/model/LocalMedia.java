package com.xiay.applib.imageselector.model;

import java.io.Serializable;


public class LocalMedia implements Serializable {
    private String path;
    private int addIconResId;

    public LocalMedia(String path) {
        this.path = path;
    }
    public LocalMedia(String path,int addIconResId) {
        this.path = path;
        this.addIconResId = addIconResId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public int getAddIconResId() {
        return addIconResId;
    }

    public void setAddIconResId(int addIconResId) {
        this.addIconResId = addIconResId;
    }

    @Override
    public String toString() {
        return "LocalMedia{" +
                "path='" + path + '\'' +
                '}';
    }
}
