package com.kohler.importer;

import java.util.ArrayList;

public class ValueList {
	private long itemId;
	private String item;
	private String type;
	private ArrayList values;

	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList getValues() {
		return values;
	}
	public void setValues(ArrayList values) {
		this.values = values;
	}
}
