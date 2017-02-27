package com.xiay.applib.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import gen.greendao.DaoMaster;
import gen.greendao.DaoSession;


/**
 * Created by Xiay on 2016/9/6.
 */

public class AppGreenDao {
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private Database mDataBase;
    private DaoSession mDaoSession;
    private static AppGreenDao appGreenDao;

    public  void init(Context content){
        if (appGreenDao ==null){
            appGreenDao =this;
            setDatabase(content);
        }
    }
    public static DaoSession getDaoSession(){
        return appGreenDao.getSession();
    }

    /**
     * 设置greenDao
     */
    private void setDatabase(Context content) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(content, "app_data_db", null);
        db = mHelper.getWritableDatabase();
        mDataBase = mHelper.getWritableDb();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getSession() {
        return mDaoSession;
    }
    public static SQLiteDatabase getDb() {
        return appGreenDao.db;
    }
}
