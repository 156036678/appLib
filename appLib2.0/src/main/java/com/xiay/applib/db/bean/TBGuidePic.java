package com.xiay.applib.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 Created by Xiay on 2017/3/21.
 * 引导页数据
 */

@DatabaseTable(tableName="tb_guidePic")
public class TBGuidePic {
    @DatabaseField(generatedId=true)
    private int id;
    @DatabaseField(columnName="pics")
    public String  pics;
    public TBGuidePic(String pics) {
        this.pics = pics;
    }
    public TBGuidePic() {
    }
}
