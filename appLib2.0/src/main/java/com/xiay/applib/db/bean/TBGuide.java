package com.xiay.applib.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Xiay on 2017/3/21.
 * 引导页数据
 */
@DatabaseTable(tableName = "tb_guide")
public class TBGuide {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "oldData")
    public String oldData;
    @DatabaseField(columnName = "newData")
    public String newData;
    /**
     * 是否第一次启动
     */
    @DatabaseField(columnName = "isFirstLunch")
    public boolean isFirstLunch;
    /**
     * 是否显示从服务器下载下来的最新引导页
     */
    @DatabaseField(columnName = "isShowNewGuide")
    public boolean isShowNewGuide;

}
