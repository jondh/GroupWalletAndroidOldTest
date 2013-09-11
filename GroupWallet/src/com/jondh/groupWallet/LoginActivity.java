/**
 * Author: Jonathan Harrison
 * Date: 8/22/13
 * Description: This class is used to control the login screen. It loads the layout
 * 				for the login page and controls the login procedures.
 * 				TODO: Put login / newuser / login or newuser with facebook into separate
 * 						classes and implement them is this class
 */

package com.jondh.groupWallet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		//
		
		final Activity mActivity = this;
		Button fbLogin = (Button) findViewById(R.id.fbLoginButton);
		fbLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Session.openActiveSession(mActivity, true, new Session.StatusCallback() {
					
					// callback when session changes state
					@Override
					public void call(Session session, SessionState state, Exception exception) {
						if (session.isOpened()) {
							// make request to the /me API
							
					        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						        // callback after Graph API response with user object
						        @Override
						        public void onCompleted(GraphUser fbuser, Response response) {
						        	if (fbuser != null) {
						        		UserLoginFB mFBlogin = new UserLoginFB();
						        		mFBlogin.execute(fbuser.getId(), "0", fbuser.getFirstName(), fbuser.getLastName());
						        		showProgress(true);
						        	}
					            }
					        });
					    }
					}
				});
			}
		});
		
		
		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(mEmail, mPassword);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, Profile> {
		@Override
		protected Profile doInBackground(String... arg0) {
			// TODO: attempt authentication against a network service.

			String url = "http://jondh.com/GroupWallet/android/login.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("USER",arg0[0]));
			nameValuePairs.add(new BasicNameValuePair("PASS",arg0[1]));
			String result = DBhttpRequest(nameValuePairs, url);
			System.out.println(result);
			
			try {
				JSONObject jObj = new JSONObject(result);
				Integer _userID = jObj.getInt("userID");
				if(_userID != 0 && _userID != null && jObj.getInt("error") == 0){
					Profile myProfile = new Profile();
					myProfile.setUserID(_userID);
					myProfile.setFirstName(jObj.getString("fN"));
					myProfile.setLastName(jObj.getString("lN"));
					myProfile.setFB(jObj.getInt("fbID"));
					return myProfile;
				}
				else{
					return null;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(final Profile _profile) {
			mAuthTask = null;
			showProgress(false);

			if (_profile != null) {
				gotoMain(_profile);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	public class UserLoginFB extends AsyncTask<String, Void, Profile> {
		@Override
		protected Profile doInBackground(String... arg0) {
			// TODO: attempt authentication against a network service.

			String url = "http://jondh.com/GroupWallet/android/loginFB.php";
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("fbUID",arg0[0]));
			nameValuePairs.add(new BasicNameValuePair("fbNew",arg0[1]));
			nameValuePairs.add(new BasicNameValuePair("fN",arg0[2]));
			nameValuePairs.add(new BasicNameValuePair("lN",arg0[3]));
			String result = DBhttpRequest(nameValuePairs, url);
			System.out.println(result);
			
			try {
				JSONObject jObj = new JSONObject(result);
				Integer _userID = jObj.getInt("userID");
				if(_userID != 0 && _userID != null){
					Profile myProfile = new Profile();
					myProfile.setUserID(_userID);
					myProfile.setFirstName(jObj.getString("fN"));
					myProfile.setLastName(jObj.getString("lN"));
					myProfile.setFB(jObj.getInt("fbID"));
					return myProfile;
				}
				else{
					return null;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(final Profile _profile) {
			mAuthTask = null;
			showProgress(false);

			if (_profile != null) {
				gotoMain(_profile);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private String DBhttpRequest(ArrayList<NameValuePair> nameValuePairs, String url){
		InputStream is;
		String result = "";
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    HttpEntity entity = response.getEntity();
		    is = entity.getContent();
		
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        is.close(); 
	        httpclient.getConnectionManager().shutdown();
	        result=sb.toString();
	        return result;
	              
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	protected void gotoMain(Profile p){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("userID", p.getUserID());
		intent.putExtra("fN", p.getFirstName());
		intent.putExtra("lN", p.getLastName());
		intent.putExtra("fbID", p.getFbID());
		startActivity(intent);
	}
}
