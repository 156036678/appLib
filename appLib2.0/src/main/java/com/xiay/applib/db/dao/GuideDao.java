package com.xiay.applib.db.dao;

import com.xiay.applib.db.bean.TBGuide;
import com.xiay.applib.db.helper.DaoHelper;

/**
 * 引导页Dao
 * Created by Xiay on 2017/3/21.
 */

public class GuideDao extends DaoHelper<TBGuide> {
    /**
     * 返回实体类对应的dao对象
     */
    public GuideDao() {
        super(TBGuide.class);
    }
}
