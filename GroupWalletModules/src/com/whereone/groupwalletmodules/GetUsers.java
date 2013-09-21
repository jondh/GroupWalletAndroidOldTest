/** 
 *  Author: Jonathan Harrison
 *  Date: 9/12/2013
 *  Name: AddWallet
 *  Description: Gets the list of users for the either a user's specific wallet or all of the user's wallets
 *  			 The onPost and onCancelled functions can be implemented through the listener implementation.
 *  Input: An instance of DBhttpRequest
 *		   A User from the class User or Profile 
 *		   A Wallet (optional) - get users from this wallet or all if null is inputed
 *
 *  Output: The onPostExecute output for this class is an ArrayList<User> containing the resulting list of users.
 *  Implementation:
 *  
	   	GetUsers getUsers = new GetUsers(DBhttpRequest, User, Wallet(can be null));
	   	getUsers.setGetUsersListener(new GetUsersListener(){
	   		@Override
	   		public void getUsersComplete(ArrayList<User> result){
	   		
	   		}
	   		@Override
	   		public void getUsersCancelled(){
	   		
	   		}
	   	});
	   	getUsers.execute();
 * 
 */

package com.whereone.groupwalletmodules;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.jondh.groupWallet.DBhttpRequest;
import com.jondh.groupWallet.User;
import com.jondh.groupWallet.Wallet;

public class GetUsers extends AsyncTask<Void, Void, ArrayList<User>>{
	private DBhttpRequest httpRequest;
	private GetUsersListener listener;
	private User user;
	private Wallet wallet;
	
	//Constructor
	GetUsers(DBhttpRequest _httpRequest, User _user, Wallet _wallet){
		httpRequest = _httpRequest;
		user = _user;
		wallet = _wallet;
	}
	
	//Listener for completion
	public void setGetUsersListener(GetUsersListener _listener){
		listener = _listener;
	}

	//Task to run in background. Connects to GroupWallet Server to retrieve desired users
	@Override
	protected ArrayList<User> doInBackground(Void... arg0) {
		String url = "http://jondh.com/GroupWallet/android/getUsers.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if(wallet == null){
			nameValuePairs.add(new BasicNameValuePair("userID", user.getUserID().toString()));
			nameValuePairs.add(new BasicNameValuePair("scope", "all"));
		}
		else{
			nameValuePairs.add(new BasicNameValuePair("userID", user.getUserID().toString()));
			nameValuePairs.add(new BasicNameValuePair("walletID", wallet.getID().toString()));
			nameValuePairs.add(new BasicNameValuePair("scope", "wallet"));
		}
		
		String result = httpRequest.sendRequest(nameValuePairs, url);
		
		ArrayList<User> userList = new ArrayList<User>();
		try {
			JSONArray jArr = new JSONArray(result);
			for(int i = 0; i < jArr.length(); i++){
				JSONObject jObj = jArr.getJSONObject(i);
				if(jObj.getInt("userID") != user.getUserID()){
					User curUser = new User(jObj.getInt("userID"),
							jObj.getInt("fbID"),
							jObj.getString("username"),
							jObj.getString("firstName"), 
							jObj.getString("lastName"),
							324.4,
							234.2);
					userList.add(curUser);
					curUser.findPicURL();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return userList;
	}
	
	//What happens when the httpRequest finishes successfully.
	@Override
	protected void onPostExecute(final ArrayList<User> result) {
		listener.getUsersComplete(result);
	}

	//What happens when the httpRequest is cancelled.
	@Override
	protected void onCancelled() {
		listener.getUsersCancelled();
	}
	
	//Listener Interface to implement onPostExecute and onCancelled.
	public interface GetUsersListener{
		public void getUsersComplete(ArrayList<User> result);
		public void getUsersCancelled();
	}

}
