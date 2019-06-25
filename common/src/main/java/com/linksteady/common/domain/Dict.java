package com.linksteady.common.domain;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "t_dict")
public class Dict implements Serializable{

	private static final long serialVersionUID = 7780820231535870010L;

	@Id
	@GeneratedValue(generator = "JDBC")
	@Column(name = "DICT_ID")
	private Long dictId;

	@Column(name = "KEY")
	private String key;

	@Column(name = "VALUE")
	private String value;

	@Column(name = "TABLE_NAME")
	private String tableName;

	@Column(name = "FIELD_NAME")
	private String fieldName;

	/**
	 * @return DICT_ID
	 */
	public Long getDictId() {
		return dictId;
	}

	/**
	 * @param dictId
	 */
	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValuee() {
		return value;
	}

	public void setValuee(String valuee) {
		this.value = value;
	}

	/**
	 * @return FIELD_NAME
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName == null ? null : fieldName.trim();
	}

	/**
	 * @return TABLE_NAME
	 */
	public String getTableName() {
		return tableName;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("dictId", dictId)
				.add("keyy", key)
				.add("value", value)
				.add("tableName", tableName)
				.add("fieldName", fieldName)
				.toString();
	}

	/**
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName == null ? null : tableName.trim();
	}
}