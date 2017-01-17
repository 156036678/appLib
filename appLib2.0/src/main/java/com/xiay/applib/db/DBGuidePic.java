package com.xiay.applib.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Xiay on 2016/12/27.
 * 引导页数据
 */
@Entity
public class DBGuidePic {
    @Id
    private Long id;
    public String  pics;
    @Generated(hash = 1847652350)
    public DBGuidePic(Long id, String pics) {
        this.id = id;
        this.pics = pics;
    }
    @Generated(hash = 888147315)
    public DBGuidePic() {
    }
    public DBGuidePic(String pics) {
        this.pics = pics;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPics() {
        return this.pics;
    }
    public void setPics(String pics) {
        this.pics = pics;
    }
}
