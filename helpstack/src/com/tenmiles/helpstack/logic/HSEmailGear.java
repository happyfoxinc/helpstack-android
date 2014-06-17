package com.tenmiles.helpstack.logic;



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
