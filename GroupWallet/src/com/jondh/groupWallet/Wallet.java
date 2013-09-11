/**
 * Author: Jonathan Harrison
 * Date: 8/25/13
 * Description: This class is used to represent a wallet.
 */

package com.jondh.groupWallet;

public class Wallet {
	private Integer id;
	private String name;
	
	Wallet(Integer _id, String _name){
		id = _id;
		name = _name;
	}
	
	public Integer getID(){
		return id;
	}
	public String getName(){
		return name;
	}
}
