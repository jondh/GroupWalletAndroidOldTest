/**
 * Author: Jonathan Harrison
 * Date: 8/6/13
 * Description: This class is used to load an image from a URL and insert it into 
 * 				a given imageView
 */

package com.jondh.groupWallet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadImage {
	private LoadImageAsync mLoadImage;
	private String url;
	private ImageView imageView;
	private Bitmap urlCon;
	private User user;
	
	LoadImage(){
		mLoadImage = new LoadImageAsync();
		url = "";
		imageView = null;
	}
	LoadImage(String _url, User _user){
		mLoadImage = new LoadImageAsync();
		url = _url;
		user = _user;
		if(_url != "" && _user != null){
			mLoadImage.execute();
		}
	}
	LoadImage(String _url, ImageView _imageView){
		mLoadImage = new LoadImageAsync();
		url = _url;
		imageView = _imageView;
		if(_url != "" && _imageView != null){
			mLoadImage.execute();
		}
	}
	LoadImage(String _url, ImageView _imageView, User _user){
		mLoadImage = new LoadImageAsync();
		url = _url;
		imageView = _imageView;
		user = _user;
		if(_url != "" && _imageView != null && _user != null){
			mLoadImage.execute();
		}
	}
	
	public void setImage(String _url, ImageView _imageView, User _user){
		url = _url;
		user = _user;
		imageView = _imageView;
		if(_url != "" && _imageView != null && _user != null){
			mLoadImage.execute();
		}
	}
	
	public void setImage(String _url, ImageView _imageView){
		url = _url;
		imageView = _imageView;
		if(_url != "" && _imageView != null){
			mLoadImage.execute();
		}
	}
	
	public class LoadImageAsync extends AsyncTask<Void, Void, Boolean>{
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				URL picURL = new URL(url);
				urlCon = BitmapFactory.decodeStream(picURL.openConnection().getInputStream());
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean result) {
			if(user == null){
				imageView.setImageBitmap(urlCon);
			}
			else if(imageView == null){
				user.setPicture(urlCon);
			}
			else{
				imageView.setImageBitmap(urlCon);
				user.setPicture(urlCon);
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}
}
