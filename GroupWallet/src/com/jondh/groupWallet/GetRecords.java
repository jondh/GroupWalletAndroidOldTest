/**
 * Author: Jonathan Harrison
 * Date: 8/25/13
 * Description: This class is used to get all the records that a user either owes or is owed
 * 				for a specific wallet.
 * 				It uses AsyncTask so it will need to executed with the following list of parameters:
 * 				String userID, String walletID, String owe/owed
 */

package com.jondh.groupWallet;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class GetRecords extends AsyncTask<String, Void, ArrayList<Record>>{
	private ArrayList<User> userList;
	private getRecordsListener recordListener;
	private ProgressDialog mPDialog;
	private Activity activity;
	private DBhttpRequest httpRequest;
	
	GetRecords(ArrayList<User> _userList, Activity _activity, DBhttpRequest _httpRequest){
		userList = _userList;
		activity = _activity;
		httpRequest = _httpRequest;
	}
	
	public void setRecordListener(getRecordsListener _recordListener) {
        this.recordListener = _recordListener;
    }
	
	@Override
	protected void onPreExecute(){ 
	    mPDialog = new ProgressDialog(activity);
	    mPDialog.setMessage("Loading...");
	    mPDialog.show();    
	}
	
	@Override
	protected ArrayList<Record> doInBackground(String... arg0) {
		String url = "http://jondh.com/GroupWallet/android/getRecords.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userID",arg0[0]));
		nameValuePairs.add(new BasicNameValuePair("walletID",arg0[1]));
		nameValuePairs.add(new BasicNameValuePair("o",arg0[2]));
		String result = httpRequest.sendRequest(nameValuePairs, url);
		//String result = DBhttpRequest(nameValuePairs, url);
		ArrayList<Record> records = new ArrayList<Record>();
		try {
			JSONArray jArr = new JSONArray(result);
			for(int i = 0; i < jArr.length(); i++){
				JSONObject jObj = jArr.getJSONObject(i);
				Integer otherUID = jObj.getInt("otherUID");
				String otherName = "";
				for(int j = 0; j < userList.size(); j++){
					if(userList.get(j).getUserID() == otherUID){
						otherName = userList.get(j).getName();
					}
				}
				System.out.println(jObj.getString("comments"));
				Record curRecord = new Record(otherName,
						(int) jObj.getDouble("amount"),
						jObj.getString("comments"));
				records.add(curRecord);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return records;
	}
	
	@Override
	protected void onPostExecute(final ArrayList<Record> result) {
		mPDialog.dismiss();
		recordListener.gotRecords(result);
	}

	@Override
	protected void onCancelled() {
		
	}
	
	public interface getRecordsListener{
		void gotRecords(ArrayList<Record> _records);
	}
}
