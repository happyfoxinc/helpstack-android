package com.tenmiles.helpstack.logic;

import java.util.ArrayList;

import com.tenmiles.helpstack.model.HSKBItem;

public class HSEmailGear extends HSGear {

	private ArrayList<HSKBItem> kbItemArray = new ArrayList<HSKBItem>();
	
	public HSEmailGear() {
		kbItemArray.add(new HSKBItem("Test", "Test"));
	}
	
	public int getKBArticleCount() {
		return kbItemArray.size();
	}
	
	public HSKBItem getKBItemAtPosition(int position) {
		return kbItemArray.get(position);
	}
	
	
}
