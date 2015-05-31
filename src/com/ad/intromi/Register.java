/*
 * Singeton for user registration)
 */
package com.ad.intromi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONStringer;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class Register
{
	private static Register _instance;
	private static Context mContext;
	private static String URL_STRING = "http://intromi.biz/exec/register_user";
	private String mSelfMac;
	private String mUniqueId;
	private String mTime; 
	private String mName;
	
	private Register()
	{
	      
		
	}

	public static Register getInstance()
	{
		if (_instance == null)
		{
			_instance = new Register();
		}
		return _instance;
	}
		
	public void doRegistration(Context context , String uniqueId,String name ){
	
		  
		 BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 System.out.println("In do registration");
		 this.mSelfMac = bluetoothAdapter.getAddress();
	     this.mUniqueId = uniqueId;
	     this.mName = name;
	     
	     
		 
		

 	     new registerUser().execute(this.mSelfMac,uniqueId,this.mName);
	}
	
//	public void doRegistration(Context context, String uniqueId){
		
//		 BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();	 
// 	     new registerUser().execute(bluetoothAdapter.getAddress(),uniqueId);
//	}
	

	private static String executeHttpPost(String url, JSONStringer postParameters)   {
		//   public String executeHttpPost(String url) throws Exception {

		BufferedReader in = null;
		StringEntity entity = null;
		HttpResponse response = null;
		HttpPost request;
		try {
			HttpClient client = new DefaultHttpClient();
			request = new HttpPost(url);
			String s = "register=";            	
			try {
				entity = new StringEntity(s+ postParameters.toString());
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}                   
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setEntity(entity);

			try {
				response = client.execute(request);
			} catch (ClientProtocolException e) {
				Log.e("OTHER EXCEPTIONS", e.toString());
			}
			catch (IOException e) {
				e.printStackTrace();
				BroadcastErrors(Errors.ERR_NETWORK_IS_DOWN);
		
			}
			int responseCode = response.getStatusLine().getStatusCode();
			switch(responseCode)
			{
			case 200:
//				HttpEntity h = response.getEntity();
//				if(h != null)
	//			{
//                 BroadcastErrors(Errors.ERR_INTROMI_SERVER_IS_UNREACHABLE);
//				}
				break;

			case 500:
				  BroadcastErrors(Errors.ERR_INTROMI_SERVER_IS_UNREACHABLE);

				break;


			default:
				  BroadcastErrors(Errors.ERR_INTROMI_SERVER_IS_UNREACHABLE);
			} 
			try {
				in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			try {
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String result = sb.toString();
			return result;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * send broadcast to application with Error number 
	 * all errors  in Errors.java* 
	 * @param e as int
	 */
	public static void  BroadcastErrors(int e) {

		Intent intent = new Intent("com.ad.proxymi.ACTION_ERRORS");	
		intent.putExtra("Error", e);
		System.out.println("This is the error i am going to send " + e);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	
	private class registerUser extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected  String doInBackground(String...string) {

			String s = null;		

                try {				
			   	executeHttpPost(URL_STRING, buildJson(string[0],string[1],string[2],System.currentTimeMillis()));
                }catch (Throwable e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
			  		

			return s;
		}

		@Override
		protected void onPostExecute(String s) {

		}
	}
	
	public static JSONStringer buildJson(String selfMac,String uniqueId,String name,long time){
		
		  JSONStringer 	mJSONStringer = new JSONStringer();
			try {
				mJSONStringer.object();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				mJSONStringer.key("intromi_id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mJSONStringer.value(selfMac);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mJSONStringer.key("company_id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mJSONStringer.value(uniqueId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				mJSONStringer.key("time_stamp");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mJSONStringer.value(time);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			try {
				mJSONStringer.key("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mJSONStringer.value(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			

			try {
				mJSONStringer.endObject();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return mJSONStringer;
			
		}
	

}














