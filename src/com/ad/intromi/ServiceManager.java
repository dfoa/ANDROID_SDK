package com.ad.intromi;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.ad.intromi.Utils;
import com.ad.intromi.DiscoveryService.LocalBinder;



public class ServiceManager {

    public static final String ERRORS = "com.ad.intromi.ACTION_ERRORS";		  
    public static final String BT_DISCOVERY_FINISHED = "com.ad.intromi.BT_DISCOVERY_FINISHED";
    public static final String MESSAGE = "com.ad.intromi.ACTION_MESSAGE"; 		
	private static DiscoveryService mService = new DiscoveryService();
	private static boolean mBound = false;
	private static Context context;
	private static ServiceManager   _instance;
	private ServiceArgument  parameters ;
	/** print log  **/
	static Boolean D = true;
	/** TAG description*/
	private static String TAG = "ServiceManager";
	private static String MANUAL_SCAN = "m";

	private ServiceManager(Context c){

		ServiceManager.context = c;
						
		
	}

	public synchronized  static ServiceManager getInstance(Context c)
	{

		if (_instance == null)
		{
			_instance = new ServiceManager(c);
		}
		return _instance;

	}

	public  void  start(){	
		
      if (Utils.isBtDiscoverable() && Utils.isBtenabled()){
        	
        
		parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80");
		Intent intent = new Intent(context, DiscoveryService.class);
		intent.putExtra("args",parameters);
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
        }
        else 
        	Log.v(TAG,"Bluetoth is not enable/discoverable");
		

	}

     public void startManualScan() {
	
    	 if (Utils.isBtDiscoverable() && Utils.isBtenabled()){
         	
	     parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80",MANUAL_SCAN);
	     Intent intent = new Intent(context, DiscoveryService.class);
	     intent.putExtra("args",parameters);
	     context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
 
    	 }
    	 else 
         	Log.v(TAG,"Bluetoth is not enable/discoverable");
	
}
	
	public  void stop() {

		// Unbind from the service
		if (mBound) {
			context.unbindService(mConnection);
			mBound = false;
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private static ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {

			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			if (D) Log.v(TAG,"Service is disconnected");
			mBound = false;
		}
	};

      public  void setLog(boolean yesNo) {
	
       mService.setLog(yesNo);
      }
      
      public void  ScanInterval(int scanIterval){
    	  
    	  mService.setScanInterval(scanIterval);
    	  
      }
      
       public void cleanFoundUsersList() {
    	   
    	   mService.cleanQeue();
       }
}
