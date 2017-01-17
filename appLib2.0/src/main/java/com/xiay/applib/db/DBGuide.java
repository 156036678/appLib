package com.xiay.applib.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Xiay on 2016/12/27.
 * 引导页数据
 */
@Entity
public class DBGuide {
    @Id
    private Long id;
    public String oldData;
    public String newData;
    /**是否第一次启动*/
    public boolean isFirstLunch;
    /**是否显示从服务器下载下来的最新引导页*/
    public boolean isShowNewGuide;
    @Generated(hash = 1943970874)
    public DBGuide(Long id, String oldData, String newData, boolean isFirstLunch,
            boolean isShowNewGuide) {
        this.id = id;
        this.oldData = oldData;
        this.newData = newData;
        this.isFirstLunch = isFirstLunch;
        this.isShowNewGuide = isShowNewGuide;
    }
    @Generated(hash = 1446965647)
    public DBGuide() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOldData() {
        return this.oldData;
    }
    public void setOldData(String oldData) {
        this.oldData = oldData;
    }
    public String getNewData() {
        return this.newData;
    }
    public void setNewData(String newData) {
        this.newData = newData;
    }
    public boolean getIsFirstLunch() {
        return this.isFirstLunch;
    }
    public void setIsFirstLunch(boolean isFirstLunch) {
        this.isFirstLunch = isFirstLunch;
    }
    public boolean getIsShowNewGuide() {
        return this.isShowNewGuide;
    }
    public void setIsShowNewGuide(boolean isShowNewGuide) {
        this.isShowNewGuide = isShowNewGuide;
    }

}
