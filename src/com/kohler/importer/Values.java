package com.kohler.importer;

public class Values {
	private String attribute;
	private String value;
	private String action;
	private long itemAttributeTypeId;

	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getAction() {
		return action;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getItemAttributeTypeId() {
		return itemAttributeTypeId;
	}
	public void setItemAttributeTypeId(long itemAttributeTypeId) {
		this.itemAttributeTypeId = itemAttributeTypeId;
	}
	
}
