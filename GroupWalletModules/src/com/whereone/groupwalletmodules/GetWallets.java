/** 
 *  Author: Jonathan Harrison
 *  Date: 9/12/2013
 *  Name: AddWallet
 *  Description: Gets the list of wallets for the associated user given in the constructor
 *  			 The onPost and onCancelled functions can be implemented through the listener implementation.
 *  Input: An instance of DBhttpRequest
 *		   A User from the class User or Profile
 *
 *  Output: The onPostExecute output for this class is an ArrayList<Wallet> containing the user's list of wallets.
 *  Implementation:
 *  
	   	GetWallets getWallets = new GetWallets(DBhttpRequest, User);
	   	getWallets.setGetWalletsListener(new GetWalletsListener(){
	   		@Override
	   		public void getWalletsComplete(ArrayList<Wallet> result){
	   		
	   		}
	   		@Override
	   		public void getWalletsCancelled(){
	   		
	   		}
	   	});
	   	getWallets.execute();
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

public class GetWallets extends AsyncTask<Void, Void, ArrayList<Wallet>>{
	private DBhttpRequest httpRequest;
	private GetWalletsListener listener;
	private User user;
	
	//Constructor
	GetWallets(DBhttpRequest _httpRequest, User _user){
		httpRequest = _httpRequest;
		user = _user;
	}
	
	//Listener for completion
	public void setGetWalletsListener(GetWalletsListener _listener){
		listener = _listener;
	}

	//Task to run in background. Connects to GroupWallet Server to add wallet and returns data from it.
	@Override
	protected ArrayList<Wallet> doInBackground(Void... arg0) {
		String url = "http://jondh.com/GroupWallet/android/getWallets.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userID", user.getUserID().toString()));
		String result = httpRequest.sendRequest(nameValuePairs, url);
		
		ArrayList<Wallet> walletList = new ArrayList<Wallet>();
		try {
			JSONArray jArr = new JSONArray(result);
			for(int i = 0; i < jArr.length(); i++){
				JSONObject jObj = jArr.getJSONObject(i);
				Wallet curWallet = new Wallet(jObj.getInt("walletID"), jObj.getString("walletName"));
				walletList.add(curWallet);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return walletList;
	}
	
	//What happens when the httpRequest finishes successfully.
	@Override
	protected void onPostExecute(final ArrayList<Wallet> result) {
		listener.getWalletsComplete(result);
	}

	//What happens when the httpRequest is cancelled.
	@Override
	protected void onCancelled() {
		listener.getWalletsCancelled();
	}
	
	//Listener Interface to implement onPostExecute and onCancelled.
	public interface GetWalletsListener{
		public void getWalletsComplete(ArrayList<Wallet> result);
		public void getWalletsCancelled();
	}

}
