package com.tenmiles.helpstack.theme.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 
 * Sets up Theme properties if any.
 * 
 * @author Nalin Chhajer
 *
 */
public class HSExpandableListView extends ExpandableListView {

	public HSExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public HSExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public HSExpandableListView(Context context) {
		super(context);
		initView(context);
	}
	
	public void initView(Context context) {
		
	}
	

}
