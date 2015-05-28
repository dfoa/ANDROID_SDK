package com.ad.IntroMi;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.R.array;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	
	
	public static JSONStringer buildJson(String selfMac,String foundMac,long time){
		
		  JSONStringer 	mJSONStringer = new JSONStringer();
			try {
				mJSONStringer.object();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				mJSONStringer.key("source_id");
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
				mJSONStringer.key("found_id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mJSONStringer.value(foundMac);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				mJSONStringer.key("time");
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

			try {
				mJSONStringer.endObject();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return mJSONStringer;
			
		}
	
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
 
	
public static String[] split (String s)
{
   
	String delims = "[;]";
	String[] items = s.split(delims);
	
	return items;
	
}

protected  static Profile parseJson(String str) {
	
  Profile  profile = new Profile();
  JSONObject jObject = null;

  
  try {
	jObject = new JSONObject(str);
       } catch (JSONException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
 
  try {
	profile.setId(jObject.getString("user_id"));
      } catch (JSONException e) {
	// TODO Auto-generated catch block
	    e.printStackTrace();
      }
    
  try {
		profile.setName(jObject.getString("name"));
	      } catch (JSONException e) {
		// TODO Auto-generated catch block
		    e.printStackTrace();
	      }
  
  try {
		profile.setToken(jObject.getString("token"));
	      } catch (JSONException e) {
		// TODO Auto-generated catch block
		    e.printStackTrace();
	      }
 
	
	return profile;
}    


	
}