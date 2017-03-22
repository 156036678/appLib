package com.xiay.applib.db.bean;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 记录实体类中属性与表中字段对应关系实体类
 *
 */ 
@DatabaseTable(tableName="tb_fcr")
public class FieldToColumnRelationEntity {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String fieldName;
	@DatabaseField
	private String columnName;
	@DatabaseField
	private String tableName;

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


}
