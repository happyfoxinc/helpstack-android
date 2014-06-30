package com.tenmiles.helpstack;

import com.tenmiles.helpstack.logic.HSGear;



/**
 * 
 * @author Nalin chhajer
 *
 */
public class HSEmailGear extends HSGear {

	public HSEmailGear(String supportEmailAddress, int localArticleResId) 
	{
		setNotImplementingKBFetching(localArticleResId);
		setNotImplementingTicketsFetching(supportEmailAddress);
	}
	
}
