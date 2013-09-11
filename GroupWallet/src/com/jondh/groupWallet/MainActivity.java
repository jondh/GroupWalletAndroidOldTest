/**
 * Author: Jonathan Harrison
 * Date: 8/25/13
 * Description: This class controls the main function of the application.
 * 				It creates the action bar and tabs for the pages of the app.
 * 				Each fragment for the tabs is also defined inside this class along
 * 				with some support function for each fragment (tab).
 * 			TODO: Create separate classes for each fragment and some support functions / classes
 */

package com.jondh.groupWallet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jondh.groupWallet.GetRecords.getRecordsListener;
import com.jondh.groupWallet.R.menu;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	/**
	 * Fragments used in this activity
	 */
	WalletFragment walletFragment = new WalletFragment();
	RecordFragment recordFragment = new RecordFragment();
	UserFragment userFragment = new UserFragment();
	SettingsFragment settingsFragment = new SettingsFragment();
	//InsertRecordFragment insertRecordFragment = new InsertRecordFragment();
	DummySectionFragment dummySectionFragment = new DummySectionFragment();
	
	/**
	 *  Http request class
	 */
	DBhttpRequest mHttpRequest;
	
	/**
	 * Image Loader
	 */
	LoadImage mLoadImage = new LoadImage();
	LoadImage profileImageLoader;
	
	/**
	 * Global user variables
	 */
	static Profile profile = new Profile();
	static ArrayList<Wallet> walletList = new ArrayList<Wallet>();
	static Wallet currentWallet;
	static ArrayList<User> userList = new ArrayList<User>();
	ArrayList<Record> oweRecords = new ArrayList<Record>();
	ArrayList<Record> owedRecords = new ArrayList<Record>();
	Boolean recordButton = true;
	int currentTab = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get userID from login
		Intent intent = getIntent();
		profile.setUserID(intent.getIntExtra("userID", 0));
		profile.setFirstName(intent.getStringExtra("fN"));
		profile.setLastName(intent.getStringExtra("lN"));
		profile.setFB(intent.getIntExtra("fbID", 0));
		
		mHttpRequest = new DBhttpRequest();
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		//getMenuInflater().inflate(R.menu.main, menu);
		inflater.inflate(R.menu.main, menu);
		Log.i("onCreateOptionsMenu", "Tab = " + currentTab);
		setMenuItems(menu);
	    return true;
	}
	
	private void setMenuItems(Menu menu){
		if(currentTab == 1){
			menu.findItem(R.id.action_add_wallet).setVisible(true);
			menu.findItem(R.id.action_find_users).setVisible(false);
		}else if(currentTab == 2){
			menu.findItem(R.id.action_add_wallet).setVisible(false);
			menu.findItem(R.id.action_find_users).setVisible(false);
		}else if(currentTab == 3){
			menu.findItem(R.id.action_add_wallet).setVisible(false);
			menu.findItem(R.id.action_find_users).setVisible(true);
		}else if(currentTab == 4){
			menu.findItem(R.id.action_add_wallet).setVisible(false);
			menu.findItem(R.id.action_find_users).setVisible(false);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private static final int FRAGMENT_COUNT = 4;
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		private FragmentManager mFM;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			
			mFM = fm;
			
			// add fragments
			mFragments.add(walletFragment);
			mFragments.add(recordFragment);
			mFragments.add(userFragment);
			mFragments.add(settingsFragment);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = mFragments.get(position);
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return FRAGMENT_COUNT;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
		
		public Fragment getActiveFragment(ViewPager container, int pos){
			String name = "android:switcher:" + container.getId() + ":" + pos;
			return mFM.findFragmentByTag(name);
		}
	}

	@SuppressLint("ValidFragment")
	public class WalletFragment extends ListFragment{
		
		WalletTabAsync mWalletList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.wallet_list, null);
            return view;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);

			if(mWalletList == null){
				mWalletList = new WalletTabAsync();
				mWalletList.execute(profile.getUserID().toString());
			}
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);

	        final int index = position;
	        
	        currentWallet = walletList.get(index);
        	
	        mViewPager.setCurrentItem(2); // goto users tab
		}
		
		 @Override
		 public void setMenuVisibility(final boolean visible) {
			 super.setMenuVisibility(visible);
			 if (visible) {
				 currentTab = 1;
				 setHasOptionsMenu(true);
				 Log.i("WalletFragment", "wallet fragment now visible");
			 }
		 }
		
		public void setWalletList(){
			WalletListAdapter adapter = new WalletListAdapter(getActivity(),
					R.layout.wallet_row, walletList);
			setListAdapter(adapter);
		}
		
		public class WalletTabAsync extends AsyncTask<String, Void, Integer>{
			private ProgressDialog mPDialog;
			
			@Override
			protected void onPreExecute(){ 
			    mPDialog = new ProgressDialog(getActivity());
			    mPDialog.setMessage("Loading...");
			    mPDialog.show();    
			}
			
			@Override
			protected Integer doInBackground(String... arg0) {
				String url = "http://jondh.com/GroupWallet/android/getWallets.php";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				System.out.println(arg0[0]);
				nameValuePairs.add(new BasicNameValuePair("userID",arg0[0]));
				String result = mHttpRequest.sendRequest(nameValuePairs, url);
				//String result = DBhttpRequest(nameValuePairs, url);
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
					return 0;
				}
				return 0;
			}
			
			@Override
			protected void onPostExecute(final Integer result) {
				if(currentWallet == null){
					currentWallet = walletList.get(0);
				}
				setWalletList();
				mPDialog.dismiss();
			}

			@Override
			protected void onCancelled() {
				
			}
		}
	}
	
	@SuppressLint("ValidFragment")
	public class RecordFragment extends ListFragment{
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.records, null);
            return view;
		}
		
		private void setRecordList(final Boolean owe){
			GetRecords getRecord = new GetRecords(userList, getActivity(), mHttpRequest);
        	getRecord.setRecordListener(new getRecordsListener(){
        		@Override
        		public void gotRecords(ArrayList<Record> _records) {
        			if(owe == true){
	        			if(_records != null && _records != oweRecords){
	        				oweRecords = _records;
	            			RecordListAdapter adapter = new RecordListAdapter(getActivity(),
	        						R.layout.record_row, oweRecords);
	        				setListAdapter(adapter);
	        			}else{
	        				RecordListAdapter adapter = new RecordListAdapter(getActivity(),
	        						R.layout.record_row, oweRecords);
	        				setListAdapter(adapter);
	        			}
        			}else{
        				if(_records != null && _records != owedRecords){
	        				owedRecords = _records;
	            			RecordListAdapter adapter = new RecordListAdapter(getActivity(),
	        						R.layout.record_row, owedRecords);
	        				setListAdapter(adapter);
	        			}else{
	        				RecordListAdapter adapter = new RecordListAdapter(getActivity(),
	        						R.layout.record_row, owedRecords);
	        				setListAdapter(adapter);
	        			}
        			}
        		}
        	});
        	if(owe == true){
        		getRecord.execute(profile.getUserID().toString(), currentWallet.getID().toString(), "owe");
        	}else{
        		getRecord.execute(profile.getUserID().toString(), currentWallet.getID().toString(), "owed");
        	}
        }

		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			
			Button oweButton = (Button) findViewById(R.id.owe);
			Button owedButton = (Button)findViewById(R.id.owed);
			
			oweButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	recordButton = true;
	            	setRecordList(recordButton);
//	            	GetRecord getRecord = new GetRecord();
//	            	getRecord.execute(profile.getUserID().toString(), currentWallet.getID().toString(), "owe");
	            }
	        });
			owedButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					recordButton = false;
					setRecordList(recordButton);
//					GetRecord getRecord = new GetRecord();
//	            	getRecord.execute(profile.getUserID().toString(), currentWallet.getID().toString(), "owed");
				}
	        });
		}
		
		@Override
		 public void setMenuVisibility(final boolean visible) {
			 super.setMenuVisibility(visible);
			 if (visible) {
				 currentTab = 2;
				 setHasOptionsMenu(true);
				 Log.i("RecordFragment", "record fragment now visible");
			 }
		 }
		
//		public class GetRecord extends AsyncTask<String, Void, ArrayList<Record>>{
//			private ProgressDialog mPDialog;
//			
//			@Override
//			protected void onPreExecute(){ 
//			    mPDialog = new ProgressDialog(getActivity());
//			    mPDialog.setMessage("Loading...");
//			    mPDialog.show();    
//			}
//			
//			@Override
//			protected ArrayList<Record> doInBackground(String... arg0) {
//				String url = "http://jondh.com/GroupWallet/android/getRecords.php";
//				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//				System.out.println(arg0[0]);
//				nameValuePairs.add(new BasicNameValuePair("userID",arg0[0]));
//				nameValuePairs.add(new BasicNameValuePair("walletID",arg0[1]));
//				nameValuePairs.add(new BasicNameValuePair("o",arg0[2]));
//				String result = mHttpRequest.sendRequest(nameValuePairs, url);
//				//String result = DBhttpRequest(nameValuePairs, url);
//				ArrayList<Record> records = new ArrayList<Record>();
//				try {
//					JSONArray jArr = new JSONArray(result);
//					for(int i = 0; i < jArr.length(); i++){
//						JSONObject jObj = jArr.getJSONObject(i);
//						Integer otherUID = jObj.getInt("otherUID");
//						String otherName = "";
//						for(int j = 0; j < userList.size(); j++){
//							if(userList.get(j).getUserID() == otherUID){
//								otherName = userList.get(j).getName();
//							}
//						}
//						System.out.println(jObj.getString("comments"));
//						Record curRecord = new Record(otherName,
//								(int) jObj.getDouble("amount"),
//								jObj.getString("comments"));
//						records.add(curRecord);
//						
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					return null;
//				}
//				return records;
//			}
//			
//			@Override
//			protected void onPostExecute(final ArrayList<Record> result) {
//				RecordListAdapter adapter = new RecordListAdapter(getActivity(),
//						R.layout.record_row, result);
//				setListAdapter(adapter);
//				mPDialog.dismiss();
//			}
//
//			@Override
//			protected void onCancelled() {
//				
//			}
//		}
	}
	
	@SuppressLint("ValidFragment")
	public class UserFragment extends ListFragment{
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.users, null);
            return view;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			
			execute();
		}
		
		private void execute(){
			UserTabAsync mUserList = new UserTabAsync();
			mUserList.execute(currentWallet.getID().toString());
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);

	        final int index = position;
	        
//	        RecordPopup rd = new RecordPopup();
//	        rd.show(getSupportFragmentManager(), "RecordDialogForm");
	        
	        //showNoticeDialog();
	        RecordDialog rd = new RecordDialog(getSupportFragmentManager(), profile, currentWallet.getID(), mHttpRequest);
	        rd.show(userList.get(index));
		}
		
		private void setUserList(){
			UserListAdapter adapter = new UserListAdapter(getActivity(),
					R.layout.user_row, userList, currentWallet, profile.getUserID());
			setListAdapter(adapter);
		}
		
		@Override
		 public void setMenuVisibility(final boolean visible) {
			 super.setMenuVisibility(visible);
			 if (visible) {
				 currentTab = 3;
				 setHasOptionsMenu(true);
				 Log.i("UserFragment", "user fragment now visible");
			 }
		 }
		
		public class UserTabAsync extends AsyncTask<String, Void, Integer>{
			private ProgressDialog mPDialog;
			
			@Override
			protected void onPreExecute(){ 
			    mPDialog = new ProgressDialog(getActivity());
			    mPDialog.setMessage("Loading...");
			    mPDialog.show();    
			}
			
			@Override
			protected Integer doInBackground(String... arg0) {
				String url = "http://jondh.com/GroupWallet/android/getWalletUsers.php";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				System.out.println(arg0[0]);
				nameValuePairs.add(new BasicNameValuePair("walletID",arg0[0]));
				String result = mHttpRequest.sendRequest(nameValuePairs, url);
				//String result = DBhttpRequest(nameValuePairs, url);
				try {
					JSONArray jArr = new JSONArray(result);
					userList.clear();
					for(int i = 0; i < jArr.length(); i++){
						JSONObject jObj = jArr.getJSONObject(i);
						if(jObj.getInt("userID") != profile.getUserID()){
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
					return 0;
				}
				return 0;
			}
			
			@Override
			protected void onPostExecute(final Integer result) {
				setUserList();
				recordFragment.setRecordList(recordButton);
				mPDialog.dismiss();
			}

			@Override
			protected void onCancelled() {
				
			}
		}
	}
	
	@SuppressLint("ValidFragment")
	public class SettingsFragment extends Fragment{
	
		ProfileAsync mProfile;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.settings, null);
            return view;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			if(mProfile == null){
				//mProfile = new ProfileAsync();
				setProfile();
				//mProfile.execute(profile.getUserID().toString());
			}
		}
		
		@Override
		public void onResume() {
		    super.onResume();
		    setProfile();
		}
		
		@Override
		 public void setMenuVisibility(final boolean visible) {
			 super.setMenuVisibility(visible);
			 if (visible) {
				 currentTab = 4;
				 setHasOptionsMenu(true);
				 Log.i("SettingsFragment", "settings fragment now visible");
			 }
		 }
		
		public class ProfileAsync extends AsyncTask<String, Void, Integer>{
			@Override
			protected Integer doInBackground(String... arg0) {
				String url = "http://jondh.com/GroupWallet/android/getUserInfo.php";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				System.out.println(arg0[0]);	
				nameValuePairs.add(new BasicNameValuePair("userID",arg0[0]));
				String result = mHttpRequest.sendRequest(nameValuePairs, url);
				//String result = DBhttpRequest(nameValuePairs, url);
				try {
					JSONObject jObj = new JSONObject(result);
					profile.setFB(jObj.getInt("fbID"));
					profile.setUserName(jObj.getString("userName"));
					profile.setFirstName(jObj.getString("firstName"));
					profile.setLastName(jObj.getString("lastName"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
				return 0;
			}
			
			@Override
			protected void onPostExecute(final Integer result) {
				setProfile();
			}

			@Override
			protected void onCancelled() {
				
			}
		}
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	

	
	/*
	 * Set the user profile on the settings page
	 */
	private void setProfile(){
		// get views
		TextView profileName = (TextView)findViewById(R.id.profileName);
		TextView profileUserName = (TextView)findViewById(R.id.profileUserName);
		ImageView profilePic = (ImageView)findViewById(R.id.profilePic);
		
		// set text fields
		profileName.setText(profile.getName());
		profileUserName.setText(profile.getUserName());
		
		// set user image, find and get if doesn't exist
		if(profile.getPicURL() == ""){
			profile.findPicURL();
		}
		if(profile.getPicture() == null){
			if(profileImageLoader == null){
				Log.i("setProfile()", profile.getPicURL());
				profileImageLoader = new LoadImage(profile.getPicURL(), profilePic, profile);
			}
		}
		else{
			profilePic.setImageBitmap(profile.getPicture());
		}
	}
}
