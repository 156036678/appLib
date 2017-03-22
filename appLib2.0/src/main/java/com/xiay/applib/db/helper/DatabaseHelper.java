package com.xiay.applib.db.helper;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableInfo;
import com.j256.ormlite.table.TableUtils;
import com.xiay.applib.AppApplication;
import com.xiay.applib.db.bean.FieldToColumnRelationEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DatabaseHelper<T> extends OrmLiteSqliteOpenHelper {
    public String DATABASE_PATH;

    public DatabaseHelper(String DATABASE_NAME, CursorFactory factory, String DATABASE_PATH) {
        super(AppApplication.context, DATABASE_NAME, factory, 1);
        this.DATABASE_PATH = DATABASE_PATH;
    }
    public DatabaseHelper(String DATABASE_NAME) {
        super(AppApplication.context, DATABASE_NAME, null, 1);
    }
    @Override
    public abstract void onCreate(SQLiteDatabase arg0, ConnectionSource arg1);

    @Override
    public abstract void onUpgrade(SQLiteDatabase database, ConnectionSource arg1, int arg2, int arg3);

    /**
     *建表的方法,子类onCreate中调用
     * @param arg1       设定文件
     * @param tableClass 表对应的实体class
     * @throws SQLException
     */
    public void createTable(ConnectionSource arg1, List<Class> tableClass) {
        try {
            List<Class<T>> list = new ArrayList<>();
            // TableUtils类提供了一些静态方法用以辅助创建和删除表
            for (int i = 0; i < tableClass.size(); i++) {
                TableUtils.createTable(arg1, tableClass.get(i));
                list.add(tableClass.get(i));
            }
            saveRelations(connectionSource, list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     数据库版本更新方法:当前表结构变化较大时调用该方法删除旧表，建新表
     * @param arg1
     * @param tableClass
     * @throws SQLException
     */
    public void setNewTable(ConnectionSource arg1, List<Class> tableClass) throws SQLException {
        for (int i = 0; i < tableClass.size(); i++) {
            TableUtils.dropTable(arg1, tableClass.get(i), true);
        }
        createTable(arg1, tableClass);

    }

    /**
     * 数据库版本更新，添加字段，在实体类中添加属性加上注解后onUpdate中调用该方法即可
     *
     * @param database
     * @param connectionSource
     * @param tableClass
     * @throws SQLException
     */
    public <T, ID> void updateTable(SQLiteDatabase database, ConnectionSource connectionSource, List<Class<T>> tableClass){

        try {
            Dao<FieldToColumnRelationEntity, ID> dao = DaoManager.createDao(connectionSource, FieldToColumnRelationEntity.class);
            // 遍历传入的实体表对象集合
            for (Class<T> dataClass : tableClass) {

                TableInfo<T, ID> tableInfo = new TableInfo<>(connectionSource, null, dataClass);
                //获取表对象各种属性，返回值为属性数组
                FieldType[] fieldTypes = tableInfo.getFieldTypes();
                //遍历属性数组
                for (FieldType fieldType : fieldTypes) {
                    //获取属性对应字段名
                    String columnName = fieldType.getColumnName();
                    //获取属性名
                    String fieldName = fieldType.getFieldName();
                    //获取表名
                    String tableName = fieldType.getTableName();
                    //查询条件
                    Map<String, Object> selections = new HashMap<>();
                    selections.put("fieldName", fieldName);
                    selections.put("columnName", columnName);
                    selections.put("tableName", tableName);
                    // 查找字段名and属性名
                    List<FieldToColumnRelationEntity> columnandfieldlist = dao.queryForFieldValues(selections);

                    selections = new HashMap<>();
                    selections.put("columnName", columnName);
                    selections.put("tableName", tableName);
                    // 查找字段名
                    List<FieldToColumnRelationEntity> columnList = dao.queryForFieldValues(selections);

                    selections = new HashMap<>();
                    selections.put("fieldName", fieldName);
                    selections.put("tableName", tableName);
                    // 查找属性名
                    List<FieldToColumnRelationEntity> fieldList = dao.queryForFieldValues(selections);

                    DatabaseType databaseType = connectionSource.getDatabaseType();

                    /**
                     * 先判断当前实体所对应的数据表是否存在
                     * */
                    // 表存在
                    if (dao.queryForEq("tableName", tableName)
                            .size() != 0) {
                        /**
                         * 然后通过字段名和属性名两个条件值查找tb_fcr表
                         * */
                        // 字段名或属性名有一个被更改
                        if (columnandfieldlist.size() == 0) {
                            // 属性被更改
                            if (fieldList.size() == 0) {
                                // 字段名没被更改
                                if (columnList.size() != 0) {
                                    /**
                                     * 根据字段名查找原有字段名和属性名，然后在tb_fcr表中修改该字段对应的属性名为更改后属性名
                                     * */
                                    GenericRawResults<String[]> gr_results = dao.queryRaw("select columnName,fieldName, tableName ,id from tb_fcr where columnName='"
                                            + columnName + "' and tableName ='" + tableName + "'");
                                    List<String[]> results = gr_results.getResults();
                                    FieldToColumnRelationEntity entity = new FieldToColumnRelationEntity();
                                    entity.setColumnName(columnName);
                                    entity.setFieldName(fieldName);
                                    entity.setTableName(results.get(0)[2]);
                                    entity.setId(Integer.parseInt(results.get(0)[3]));
                                    // 当属性名被更改但字段名不变时，在属性字段关系表中更新该字段记录
                                    dao.update(entity);
                                } else {
                                    /**
                                     * 在tb_fcr表中没有该属性及字段对应的记录，该属性及字段为新添加的,则在tb_fcr表中新添加该对应关系记录
                                     * */
                                    if (fieldList.size() == 0) {
                                        String sql = alertTableSQL(databaseType,
                                                fieldType);
                                        database.execSQL(sql);
                                        FieldToColumnRelationEntity entity = new FieldToColumnRelationEntity();
                                        entity.setColumnName(columnName);
                                        entity.setFieldName(fieldName);
                                        entity.setTableName(tableName);
                                        dao.create(entity);
                                    }
                                }

                            } else {
                                // 属性没有被更改
                                if (columnList.size() == 0) {
                                    // 字段名被更改
                                    // 根据属性名查找原有字段名和属性名
                                    GenericRawResults<String[]> gr_results = dao
                                            .queryRaw("select columnName,fieldName, tableName ,id from tb_fcr where fieldName='"
                                                    + fieldName + "'and tableName ='" + tableName + "'");
                                    List<String[]> results = gr_results.getResults();
                                    /*
                                     * 如果属性名不变，但对应字段名更改了，则在该对象对应表中首先创建新字段，然后将原有字段数据拷贝到新字段下，原有字段置空
                                     * */
                                    String sql1 = alertTableSQL(databaseType,
                                            fieldType);
                                    database.execSQL(sql1);
                                    String sql2 = "update "
                                            + tableName + " set "
                                            + columnName + " = "
                                            + results.get(0)[0];
                                    database.execSQL(sql2);
                                    String sql3 = "update "
                                            + tableName + " set "
                                            + results.get(0)[0] + " = null";
                                    database.execSQL(sql3);
                                    /*
                                     * 在tb_fcr表中更新属性及字段对应关系
                                     * */
                                    FieldToColumnRelationEntity old_entity = new FieldToColumnRelationEntity();
                                    old_entity.setColumnName(columnName);
                                    old_entity.setFieldName(fieldName);
                                    old_entity.setTableName(results.get(0)[2]);
                                    old_entity.setId(Integer.parseInt(results.get(0)[3]));
                                    //更新属性与字段对应关系
                                    dao.update(old_entity);
                                }
                            }
                        }

                    } else {
                        // 表不存在则直接创建表先啊
                        List<Class> list = new ArrayList<Class>();
                        list.add(dataClass);
                        createTable(connectionSource, list);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存创建的表属性及属性对应数据表中字段名等信息
     * @param connectionSource
     * @param tableClass
     * @throws SQLException
     */
    public <ID> void saveRelations(ConnectionSource connectionSource,
                                   List<Class<T>> tableClass) throws SQLException {
        Dao<FieldToColumnRelationEntity, ID> dao = DaoManager.createDao(connectionSource, FieldToColumnRelationEntity.class);
        for (Class<T> dataClass : tableClass) {

            TableInfo<T, ID> tableInfo = new TableInfo<>(connectionSource,
                    null, dataClass);
            FieldType[] fieldTypes = tableInfo.getFieldTypes();
            List<FieldToColumnRelationEntity> fcrs = new ArrayList<>();
            for (FieldType fieldType : fieldTypes) {
                FieldToColumnRelationEntity fcr = new FieldToColumnRelationEntity();
                fcr.setColumnName(fieldType.getColumnName());
                fcr.setFieldName(fieldType.getFieldName());
                fcr.setTableName(fieldType.getTableName());
                fcrs.add(fcr);
            }
            dao.create(fcrs);
        }
    }

    /**
     * 由实体属性得到相应字段名及字段类型，拼接sql
     * @param databaseType
     * @param fieldType
     * @return String 返回类型
     * @throws SQLException 设定文件
     */
    private static String alertTableSQL(DatabaseType databaseType,
                                        FieldType fieldType) throws SQLException {
        List<String> queriesAfter = new ArrayList<>();
        StringBuilder sb = new StringBuilder(256);
        sb.append("ALTER TABLE ");
        databaseType.appendEscapedEntityName(sb, fieldType.getTableName());
        sb.append(" ADD ");
        List<String> additionalArgs = new ArrayList<>();
        List<String> statementsBefore = new ArrayList<>();
        List<String> statementsAfter = new ArrayList<>();
        // our statement will be set here later
        boolean first = true;
        // skip foreign collections
        if (fieldType.isForeignCollection()) {
            first = true;
        } else if (first) {
            first = false;
        } else {
            sb.append(", ");
        }
        String columnDefinition = fieldType.getColumnDefinition();
        if (columnDefinition == null) {
            // we have to call back to the database type for the specific create
            // syntax
            databaseType.appendColumnArg(fieldType.getTableName(), sb,
                    fieldType, additionalArgs, statementsBefore,
                    statementsAfter, queriesAfter);
        } else {
            // hand defined field
            databaseType.appendEscapedEntityName(sb, fieldType.getColumnName());
            sb.append(' ').append(columnDefinition).append(' ');
        }
        // }
        // add any sql that sets any primary key fields
        FieldType[] fieldTypes = {fieldType};
        databaseType.addPrimaryKeySql(fieldTypes, additionalArgs,
                statementsBefore, statementsAfter, queriesAfter);
        // add any sql that sets any unique fields
        databaseType.addUniqueComboSql(fieldTypes, additionalArgs,
                statementsBefore, statementsAfter, queriesAfter);
        for (String arg : additionalArgs) {
            // we will have spat out one argument already so we don't have to do
            // the first dance
            sb.append(", ").append(arg);
        }
        sb.append(" ");
        return sb.toString();

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (DATABASE_PATH!=null){
            return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (DATABASE_PATH!=null){
            return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return super.getReadableDatabase();

    }

    // 释放资源
    @Override
    public void close() {
        super.close();

    }

}
