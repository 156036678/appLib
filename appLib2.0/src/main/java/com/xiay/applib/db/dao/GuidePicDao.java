package com.xiay.applib.db.dao;

import com.xiay.applib.db.bean.TBGuidePic;
import com.xiay.applib.db.helper.DaoHelper;

/**
 * 引导页图片Dao
 * Created by Xiay on 2017/3/21.
 */

public class GuidePicDao extends DaoHelper<TBGuidePic> {
    /**
     * 返回实体类对应的dao对象
     */
    public GuidePicDao() {
        super(TBGuidePic.class);
    }
}
