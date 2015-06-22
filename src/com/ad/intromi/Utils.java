package com.ad.intromi;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils  {


private static String TAG = "Utils"; 
	
	protected static JSONStringer buildJson(String selfMac,String foundMac,long time)  {
		
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
				mJSONStringer.key("timestamp");
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
	
	protected static boolean isNetworkAvailable(Activity activity) {
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
 
	
protected static String[] split (String s)
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
	    
		profile.setName(Utils.convertFromUTF8(jObject.getString("name")));
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

  protected static boolean isBtDiscoverable(){
	  
	  BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	  if(mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
		{
			Log.v(TAG,"Bluetooth is not discoverable");
			return false;
		}
	  return true;
  }
    
  protected  static  boolean isBtenabled () {
	  
	  
	  BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	  return (mBtAdapter.isEnabled());		       
  }
  
  
  public static  boolean ifBleSupported (Context c) {
	 if (c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    	 System.out.println("BLE is  supported");
	    return true;
	 }
	 return false;
  }
  /**
   * Initializes a reference to the local Bluetooth adapter.
 * @return 
   *
   * @return Return true if the initialization is successful.
   */
 /*
  protected static boolean initialize() {
      // For API level 18 and above, get a reference to BluetoothAdapter through
      // BluetoothManager.
	  BluetoothManager mBluetoothManager;
          mBluetoothManager = (BluetoothManager)   getSystemService(Context.BLUETOOTH_SERVICE);
          
          if (mBluetoothManager == null) {
              Log.e(TAG, "Unable to initialize BluetoothManager.");
              return false;
          }
      
      BluetoothAdapter mBluetoothAdapter;
      mBluetoothAdapter = mBluetoothManager.getAdapter();
      if (mBluetoothAdapter == null) {
          Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
          return false;
      }
      else {
    	  
          
      
      
    	  return true;
      }
      }
  
  */
  
  public static String convertFromUTF8(String s) {
      String out = null;
      try {
          out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
      } catch (java.io.UnsupportedEncodingException e) {
          return null;
      }
      return out;
  }

  // convert from internal Java String format -> UTF-8
  public static String convertToUTF8(String s) {
      String out = null;
      try {
          out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
      } catch (java.io.UnsupportedEncodingException e) {
          return null;
      }
      return out;
  }
  
}
