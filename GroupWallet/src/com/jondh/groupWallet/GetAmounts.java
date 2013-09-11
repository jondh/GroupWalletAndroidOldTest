/**
 * Author: Jonathan Harrison
 * Date: 8/25/13
 * Description: This class is used to get the money owe / owed from one user to another.
 * 				It uses AsyncTask so it will need to executed with the following list of parameters:
 * 				String userID, String userID, String walletID
 */

package com.jondh.groupWallet;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetAmounts extends AsyncTask<String, Void, Double>{
	private DBhttpRequest httpRequest;
	private getAmountsListener amountListener;
	private User user;
	private String scope; // scope is either a wallet or total
	
	GetAmounts(DBhttpRequest _httpRequest, String _scope){
		httpRequest = _httpRequest;
		scope = _scope; 
	}
	
	public void setUser(User _user){
		user = _user;
	}
	
	public void setAmountListener(getAmountsListener _amountListener) {
        this.amountListener = _amountListener;
    }
	
	@Override
	protected Double doInBackground(String... arg0) {
		String url = "http://jondh.com/GroupWallet/android/getAmounts.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userID",arg0[0]));
		nameValuePairs.add(new BasicNameValuePair("otherUID",arg0[1]));
		if(scope == "wallet"){
			nameValuePairs.add(new BasicNameValuePair("walletID",arg0[2]));
			nameValuePairs.add(new BasicNameValuePair("scope","wallet")); 
		}
		else if(scope == "total"){
			nameValuePairs.add(new BasicNameValuePair("scope","total")); 
		}
		String result = httpRequest.sendRequest(nameValuePairs, url);
		Log.i("GetAmounts Result", "otherUID: " + arg0[1] + " " +  result);
		try {
			JSONObject jObj = new JSONObject(result);
			Double amountOwe = jObj.getDouble("amountOwe");
			Double amountOwed = jObj.getDouble("amountOwed");
			if(user != null){
				user.setWalletAmount(amountOwed - amountOwe);
			}
			return amountOwed - amountOwe;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0.0;
		}
	}
	
	@Override
	protected void onPostExecute(final Double result) {
		amountListener.gotAmounts(result);
	}

	@Override
	protected void onCancelled() {
		
	}
	
	public interface getAmountsListener{
		void gotAmounts(Double _amount);
	}
}
