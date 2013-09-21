/** 
 *  Author: Jonathan Harrison
 *  Date: 9/3/2013
 *  Name: AddWallet
 *  Description: Adds a wallet for GroupWallet from the User and name given in the constructor.
 *  			 The onPost and onCancelled functions can be implemented through the listener implementation.
 *  Input: An instance of DBhttpRequest
 *		   A User from the class User or Profile
 *  	   A String for the wallet name
 *  Output: String result -> "success" for success or some other string indicating what kind of failure
 *  Implementation:
 *  
	   	AddWallet addWallet = new AddWallet(DBhttpRequest, User, walletName);
	   	addWallet.setAddWalletListener(new AddWalletListener(){
	   		@Override
	   		public void addWalletComplete(String result){
	   		
	   		}
	   		@Override
	   		public void addWalletCancelled(){
	   		
	   		}
	   	});
	   	addWallet.execute();
 * 
 */

package com.whereone.groupwalletmodules;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.jondh.groupWallet.DBhttpRequest;
import com.jondh.groupWallet.User;

public class AddWallet extends AsyncTask<Void, Void, String>{
	private DBhttpRequest httpRequest;
	private AddWalletListener listener;
	private User user;
	private String walletName;
	
	//Constructor
	AddWallet(DBhttpRequest _httpRequest, User _user, String _walletName){
		httpRequest = _httpRequest;
		user = _user;
		walletName = _walletName;
	}
	
	//Listener for completion
	public void setAddWalletListener(AddWalletListener _listener){
		listener = _listener;
	}

	//Task to run in background. Connects to GroupWallet Server to add wallet and returns data from it.
	@Override
	protected String doInBackground(Void... arg0){
		String url = "http://jondh.com/GroupWallet/android/addWallet.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userID", user.getUserID().toString()));
		nameValuePairs.add(new BasicNameValuePair("otherUID",walletName));
		String result = httpRequest.sendRequest(nameValuePairs, url);
		
		return result;
	}
	
	//What happens when the httpRequest finishes successfully.
	@Override
	protected void onPostExecute(final String result) {
		listener.addWalletComplete(result);
	}

	//What happens when the httpRequest is cancelled.
	@Override
	protected void onCancelled() {
		listener.addWalletCancelled();
	}
	
	//Listener Interface to implement onPostExecute and onCancelled.
	public interface AddWalletListener{
		public void addWalletComplete(String result);
		public void addWalletCancelled();
	}
	
	public class AddWalletException extends Exception{

		private static final long serialVersionUID = 6886806143449463848L;
		private String message;

		public AddWalletException(){
			super();
			message = "unknown";
		}

		public AddWalletException(String _message){
			super(_message);
			message = _message;
		}
		
		public String getError(){
			return message;
		}
	}

}
