package com.xiay.applib.db.helper;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 数据表操作类
 */
public class DaoHelper<T> {
    private Dao<T, Integer> dao;
    private DatabaseConnection connection;
    private Savepoint savePoint;
    T beanClass;

    /**
     * *返回实体类对应的dao对象
     *
     * @see "如数据库升级或更改路径为 /storage/emulated/0/包名/app.db“ <BR/><BR/>
     * AppDatabaseHelper helper= new AppDatabaseHelper();
     * <BR/>
     * DatabaseUtils  bru=new DatabaseUtils(context);
     * <BR/>
     * bru.setDatabasePath(helper, AppHelper.getInstance().getSDPath(context)+"/."+context.getPackageName(),"app.db",1);
     * <BR/>
     * dao = helper.getDao(beanClass);
     */
    public DaoHelper(DatabaseHelper helper, Class<T> beanClass) {
        if (dao == null) {
            try {
                this.beanClass = beanClass.newInstance();
                dao = helper.getDao(beanClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回实体类对应的dao对象
     * <BR/>
     * 默认路径为 /data/data/包名/appbase.db“
     */
    public DaoHelper(Class<T> beanClass) {
        if (dao == null) {
            try {
                this.beanClass = beanClass.newInstance();
                AppDatabaseHelper helper = new AppDatabaseHelper();
                //  DatabaseUtils  bru=new DatabaseUtils(context);
                //  bru.setDatabasePath(helper, AppHelper.getInstance().getSDPath(context)+"/."+context.getPackageName(),"app.db",1);
                dao = helper.getDao(beanClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * * 返回实体类对应的dao对象
     * * @param beanClass
     * @param DATABASE_NAME 数据库名字
     * <BR/>
     * 默认路径为 /data/data/包名/app.db“

     */


    public DaoHelper(Class<T> beanClass,String DATABASE_NAME) {
        if (dao == null) {
            try {
                this.beanClass = beanClass.newInstance();
                AppDatabaseHelper helper = new AppDatabaseHelper(DATABASE_NAME);
                //  DatabaseUtils  bru=new DatabaseUtils(context);
                //  bru.setDatabasePath(helper, AppHelper.getInstance().getSDPath(context)+"/."+context.getPackageName(),"app.db",1);
                dao = helper.getDao(beanClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public QueryBuilder<T, Integer> getQueryBuilder() {
        return dao.queryBuilder();
    }
    /*
     * *************新增数据方法*********************************
	 */

    /**
     * 新增单条数据
     */
    public void insert(T object) {
        try {
            dao.createIfNotExists(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 存在更新，没有增加
     */
    public void save(T object) {
        try {
            dao.createOrUpdate(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 批量新增
     */
    public void insert(Collection<T> collection) {
        try {
            dao.create(collection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

	/*
     * *************查询数据方法*********************************
	 */

    /**
     * 使用迭代器查询表中所用记录
     */
    public List<T> queryAllData() {
        List<T> datalist = new ArrayList<>();
        CloseableIterator<T> iterator = dao.closeableIterator();
        try {
            while (iterator.hasNext()) {
                T data = iterator.next();
                datalist.add(data);
            }
        } finally {
            // close it at the end to close underlying SQL statement
            try {
                iterator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }

    /**
     * 根据传入的字段与value值的map匹配查询
     */
    public List<T> queryDataEqByClause(Map<String, Object> clause) throws SQLException {
        // queryBuild构建多条件查询
        List<T> result = dao.queryForFieldValuesArgs(clause);
        return result;
    }

    /**
     * 返回查询结果的总数
     *
     * @see "对应SQL：SELECT COUNT(*) FROM 'table'"
     */
    public long queryCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 返回所有行的第一行。
     */
    public T queryForFirst() {
        try {
            return getQueryBuilder().queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据sql查询记录
     */
    public List<String[]> queryDataBySql(String sql) throws SQLException {
        GenericRawResults<String[]> rawResults = dao.queryRaw(sql);
        List<String[]> results = rawResults.getResults();
        return results;
    }

	/*
     * *************更新数据方法*********************************
	 */

    /**
     * 使用对象更新一条记录
     */
    public void updateData(T object) throws SQLException {
        dao.update(object);
    }


    /**
     * 根据条件做update时直接使用sql语句进行更新
     */
    public void updataDatabySQL(String sql) throws SQLException {
        dao.updateRaw(sql);
    }

	/*
     * *************删除数据方法*********************************
	 */

    /**
     * 使用对象删除一条记录
     */
    public void delectData(T object) throws SQLException {
        dao.delete(object);
    }

    /**
     * 批量删除
     */
    public void delectDatas(Collection<T> datas) throws SQLException {
        dao.delete(datas);
    }

    /**
     * 批量删除
     */
    public boolean deleteAll() {
        try {
            return dao.executeRaw("DELETE FROM " + dao.getTableName()) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 可进行批量操作，需要进行批量操作时直接将代码放到callable的call()中即可
     */
    public <A> void callBatchTasks(Callable<A> callable) {
        try {
            dao.callBatchTasks(callable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/*
	 * *************直接执行sql语句方法*********************************
	 */

    /**
     * 直接执行所有的sql语句，应用于特殊场景
     */
    public int executeSql(String sql) throws SQLException {
        int result = dao.executeRaw(sql);
        return result;
    }
	
	/*
	 * *************事务操作*********************************
	 */

    /**
     * 开启数据库事务操作
     */
    public void beginTransaction(String savepoint) throws SQLException {
        connection = dao.startThreadConnection();
        savePoint = connection.setSavePoint(savepoint);

    }

    /**
     * 提交事务
     */
    public void commit() throws SQLException {
        connection.commit(savePoint);
        dao.endThreadConnection(connection);
    }

    /**
     * 提交事务
     */
    public void rollBack(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
        dao.endThreadConnection(connection);
    }
}
