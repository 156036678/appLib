package com.xiay.applib.db.helper;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.xiay.applib.db.bean.FieldToColumnRelationEntity;
import com.xiay.applib.db.bean.TBGuide;
import com.xiay.applib.db.bean.TBGuidePic;

import java.util.ArrayList;
import java.util.List;

public class AppDatabaseHelper<T> extends DatabaseHelper{

	/**默认在应用安装路径下面*/
	public AppDatabaseHelper() {
		super("appbase.db");
	}
	/**默认在应用安装路径下面*/
	public AppDatabaseHelper(String DATABASE_NAME) {
		super(DATABASE_NAME);
	}
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		List<Class> classs = new ArrayList<>();
		classs.add(TBGuide.class);
		classs.add(TBGuidePic.class);
		classs.add(FieldToColumnRelationEntity.class);
		super.createTable(arg1, classs);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource arg1, int arg2, int arg3) {
        List<Class<T>> tableClass=new ArrayList<>();
        tableClass.add((Class<T>) TBGuide.class);
        tableClass.add((Class<T>) TBGuidePic.class);
		super.updateTable(database,arg1, tableClass);
	}

}
