/**
 * Author: Jonathan Harrison
 * Date: 8/7/13
 * Description: This class is a popup dialog used to insert a record (transaction).
 */

package com.jondh.groupWallet;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;


public class RecordDialog {
	private FragmentManager fragmentManager;
	private static RecordDialogForm recordDialogForm;
	private static User otherUser;
	private static User user;
	private static Integer walletID;
	private static DBhttpRequest httpRequest;

	RecordDialog(FragmentManager _fragmentManager, User _user, Integer _walletID, DBhttpRequest _httpRequest){
		user = _user;
		walletID = _walletID;
		fragmentManager = _fragmentManager;
		httpRequest = _httpRequest;
		recordDialogForm = new RecordDialogForm();
	}

	public void show(User _user){
		otherUser = _user;
		recordDialogForm.show(fragmentManager, "RecordDialogForm");
	}
	
	public static class RecordDialogForm extends DialogFragment {
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	        LayoutInflater inflater = getActivity().getLayoutInflater();
	        builder.setTitle(otherUser.getName());
	        final View view = inflater.inflate(R.layout.insert_record_dialog, null);
	        builder.setView(view);
	        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	                  
        	public void onClick(DialogInterface dialog, int id) {
					String amount = ((EditText) view.findViewById(R.id.ir_amount)).getText().toString();
					String comments = ((EditText) view.findViewById(R.id.ir_comments)).getText().toString();
					String o;
					int o_selected = ((RadioGroup) view.findViewById(R.id.ir_radioGroup)).getCheckedRadioButtonId();
					if(o_selected == R.id.ir_owe){
						o = "owe";
					}
					else{
						o = "owed";
					}
					
					InsertRecordAsync iRecord = new InsertRecordAsync();
					iRecord.execute(user.getUserID().toString(), otherUser.getUserID().toString(),
							amount, walletID.toString(), comments, o);
					
        		}
            });
	        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	    
	    public class InsertRecordAsync extends AsyncTask<String, Void, Boolean>{
	    	private ProgressDialog mPDialog;
			
			@Override
			protected void onPreExecute(){ 
			    mPDialog = new ProgressDialog(getActivity());
			    mPDialog.setMessage("Loading...");
			    mPDialog.show();    
			}
	    	
	    	@Override
			protected Boolean doInBackground(String... arg0) {
	    		try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String url = "http://jondh.com/GroupWallet/android/insertRecord.php";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("userID",arg0[0]));
				nameValuePairs.add(new BasicNameValuePair("otherUID",arg0[1]));
				nameValuePairs.add(new BasicNameValuePair("amount",arg0[2]));
				nameValuePairs.add(new BasicNameValuePair("walletID",arg0[3]));
				nameValuePairs.add(new BasicNameValuePair("comments",arg0[4]));
				nameValuePairs.add(new BasicNameValuePair("o",arg0[5]));
				httpRequest.sendRequest(nameValuePairs, url);
				//DBhttpRequest(nameValuePairs, url);
				
				return true;
			}
			
			@Override
			protected void onPostExecute(final Boolean result) {
				mPDialog.dismiss();
			}

			@Override
			protected void onCancelled() {
				
			}
		}
	}
}
