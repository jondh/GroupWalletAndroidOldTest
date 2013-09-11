/**
 * Author: Jonathan Harrison
 * Date: 8/25/13
 * Description: This class abstracts a "record". The record is a record of a
 * 				transaction between the logged in user and some other user.
 */

package com.jondh.groupWallet;

public class Record {
	private String user;
	private Integer amount;
	private String comment;
	
	Record(String _user, Integer _amount){
		user = _user;
		amount = _amount;
		comment = "";
	}
	
	Record(String _user, Integer _amount, String _comment){
		user = _user;
		amount = _amount;
		comment = _comment;
	}
	
	public String getUser(){
		return user;
	}
	public Integer getAmount(){
		return amount;
	}
	public String getComment(){
		return comment;
	}
}
