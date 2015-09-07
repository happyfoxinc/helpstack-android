//  HSBaseExpandableListAdapter
//
//Copyright (c) 2014 HelpStack (http://helpstack.io)
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package com.tenmiles.helpstack.helper;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

/**
 * 
 * This is simple adapter that helps in storing Parent and Child value, 
 * this simplifying the amount of code written by developer.
 * 
 * Just call addParent and addChild to add custom properties of your choice.
 * 
 * @author Nalin Chhajer
 *
 */
public abstract class HSBaseExpandableListAdapter extends BaseExpandableListAdapter {
	
	ArrayList<Object> children = new ArrayList<Object>();
	SparseArray<Parent> groups = new SparseArray<Parent>();
	
	protected final LayoutInflater mLayoutInflater;
	protected final Context context;
	public HSBaseExpandableListAdapter(Context context) {
		super();
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	class Parent {
		int id;
		Object parent;
		ArrayList<Integer> childs = new ArrayList<Integer>();
	}
	
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	public void clearAll() {
		children.clear();
		groups.clear();
	}

	public void clearParent(int parentId) {
		if (groups.get(parentId) != null) {
			groups.get(parentId).childs.clear();	
		}
	}
	
	// Please note the value of the previous parent will be overridden
	public void addParent(int parentId, Object parent) {
		assert parent!=null;
		Parent parentHolder = new Parent();
		parentHolder.id = parentId;
		parentHolder.parent = parent;
		groups.put(parentId, parentHolder);
	}
	
	public void addChild(int parentId, Object child) {
		children.add(child);
		int pos = children.indexOf(child);
		groups.get(parentId).childs.add(pos);
	}
	
	public void addChildAll(int parentId, ArrayList child) {
		for (Object object : child) {
			addChild(parentId, object);
		}
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		int childPos = groups.valueAt(groupPosition).childs.get(childPosition);
		return children.get(childPos);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		int childPos = groups.valueAt(groupPosition).childs.get(childPosition);
		return childPos;
	}

	@Override
	public abstract View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent);
	
	@Override
	public int getChildrenCount(int groupPosition) {
		Parent mParent = groups.valueAt(groupPosition);
		if(mParent == null) {
			return 0;
		}
		return mParent.childs.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.valueAt(groupPosition).parent;
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	public long getParentId(int groupPosition) {
		return groups.valueAt(groupPosition).id;
	}

	@Override
	public abstract View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent);

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

    //Listeners
	protected OnChildItemClickListener childListener;
	protected OnParentItemClickListener parentListener;
	
	protected void sendChildClickEvent(int groupPosition, int childPosition,String type, Object map) {
		if(childListener!=null) {
			childListener.onChildListItemClick(groupPosition, childPosition, type, map);
		}
	}
	
	protected void sendParentItemClickEvent(int groupPosition,String type, Object obj) {
		if(parentListener!=null) {
			parentListener.onParentItemClicked(groupPosition, type, obj);
		}
	}
	
	protected boolean sendChildLongClickEvent(int groupPosition, int childPosition,String type, Object map) {
		if(childListener!=null) {
			return childListener.onChildListItemLongClick(groupPosition,childPosition,type, map);
		}
		return false;
	}
	
	protected void sendChildCheckedEvent(int groupPosition, int childPosition,String type,Object map,boolean checked) {
		if(childListener!=null) {
			childListener.onChildCheckedListener(groupPosition, childPosition, type, map, checked);
		}
	}
		
	public void setOnChildItemClickListener(OnChildItemClickListener listener) {
		this.childListener = listener;
	}
	
	public void setOnParentItemClickListener(OnParentItemClickListener listener) {
		this.parentListener = listener;
	}
	
	public interface OnChildItemClickListener {
		void onChildListItemClick(int groupPosition, int childPosition,String type,Object map);
		void onChildCheckedListener(int groupPosition, int childPosition, String type, Object map, boolean checked);
		boolean onChildListItemLongClick(int groupPosition, int childPosition, String type, Object map);
	}
	
	public interface OnParentItemClickListener {
		void onParentItemClicked(int groupPosition, String type, Object obj);
	}
}
