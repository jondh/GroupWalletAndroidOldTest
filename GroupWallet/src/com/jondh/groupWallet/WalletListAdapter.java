/**
 * Author: Jonathan Harrison
 * Date: 8/25/13
 * Description: This class is a list view adapter for a list of wallets.
 */

package com.jondh.groupWallet;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WalletListAdapter extends ArrayAdapter<Wallet> {
	
	public WalletListAdapter(Context context, int textViewResourceId) {
	    super(context, textViewResourceId);
	    // TODO Auto-generated constructor stub
	}

	private List<Wallet> wallets;

	public WalletListAdapter(Context context, int resource, List<Wallet> _wallets) {

	    super(context, resource, _wallets);

	    this.wallets = _wallets;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    View v = convertView;

	    if (v == null) {

	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        v = vi.inflate(R.layout.wallet_row, null);

	    }

	    Wallet wallet = wallets.get(position);

	    if (wallet != null) {

	        TextView v_name = (TextView) v.findViewById(R.id.wallet_name);

	        if (v_name != null) {
	            v_name.setText(wallet.getName());
	        }
	    }

	    return v;

	}
}
