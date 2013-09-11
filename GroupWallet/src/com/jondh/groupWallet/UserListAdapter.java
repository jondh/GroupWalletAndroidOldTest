/**
 * Author: Jonathan Harrison
 * Date: 8/20/13
 * Description: This class is a list view adapter for a list of users
 */

package com.jondh.groupWallet;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jondh.groupWallet.GetAmounts.getAmountsListener;

public class UserListAdapter extends ArrayAdapter<User> {
	LoadImage mLoadImage;
	DBhttpRequest httpRequest;
	Wallet wallet;
	Integer userID;
	
	public UserListAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	    // TODO Auto-generated constructor stub
	}

	private List<User> users;

	public UserListAdapter(Context context, int resource, List<User> _users, Wallet _wallet, Integer _userID) {

	    super(context, resource, _users);

	    this.users = _users;
	    this.wallet = _wallet;
	    this.userID = _userID;
	    
	    httpRequest = new DBhttpRequest();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    View v = convertView;

	    if (v == null) {

	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(R.layout.user_row, null);

	    }

	    final User user = users.get(position);

	    if (user != null) {

	        TextView v_name = (TextView) v.findViewById(R.id.user_name);
	        final TextView v_wallet_amount = (TextView) v.findViewById(R.id.user_wallet_amount);
	        final TextView v_total_amount = (TextView) v.findViewById(R.id.user_total_amount);
	        ImageView v_profile_pic = (ImageView) v.findViewById(R.id.user_pic);
	        
	        if (v_name != null) {
	            v_name.setText(user.getName());
	        }
	        if (v_wallet_amount != null) {
	        	if(user.isWalletAmountRefresh()){
		        	GetAmounts mGetAmounts = new GetAmounts(httpRequest, "wallet");
		        	
		        	mGetAmounts.setUser(user);
		        	mGetAmounts.setAmountListener(new getAmountsListener(){
			        	@Override
			        	public void gotAmounts(Double _amount){
			        		user.setWalletAmount(_amount);
			        		user.setWalletAmountRefresh(false);   
			        		v_wallet_amount.setText(_amount.toString());
			        	}
		        	});
		        	mGetAmounts.execute(userID.toString(), user.getUserID().toString(), wallet.getID().toString());
	        	}
	        	else{
	        		v_wallet_amount.setText(user.getWalletAmount().toString());
	        	}
	        }
	        if (v_total_amount != null) {
	        	if(user.isTotalAmountRefresh()){
		        	GetAmounts mGetAmounts = new GetAmounts(httpRequest, "total");
		        	
		        	mGetAmounts.setUser(user);
		        	mGetAmounts.setAmountListener(new getAmountsListener(){
			        	@Override
			        	public void gotAmounts(Double _amount){
			        		user.setTotalAmount(_amount);
			        		user.setTotalAmountRefresh(false);   
			        		v_total_amount.setText(_amount.toString());
			        	}
		        	});
		        	mGetAmounts.execute(userID.toString(), user.getUserID().toString());
	        	}
	        	else{
	        		v_total_amount.setText(user.getTotalAmount().toString());
	        	}
	        }
	        if (v_profile_pic != null){
	        	if(user.getPicURL() == ""){
	        		user.findPicURL();
	        	}
	        	if(user.getPicture() == null){
	        		new LoadImage(user.getPicURL(), v_profile_pic, user);
	        	}
	        	else{
	        		v_profile_pic.setImageBitmap(user.getPicture());
	        	}
	        	//mLoadImage = new LoadImage(user.getPicURL(), v_profile_pic);
	        }
	    }

	    return v;

	}
}
