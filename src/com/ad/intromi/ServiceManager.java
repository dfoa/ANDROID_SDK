package com.ad.intromi;



import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ad.intromi.Utils;
import com.ad.intromi.DiscoveryService.LocalBinder;
import com.ad.intromi.BluetoothLeService.BleLocalBinder;


 

public class ServiceManager {

    public static final String ERRORS = "com.ad.intromi.ACTION_ERRORS";		  
    public static final String BT_DISCOVERY_FINISHED = "com.ad.intromi.BT_DISCOVERY_FINISHED";
    public static final String MESSAGE = "com.ad.intromi.ACTION_MESSAGE"; 		
    public static final String BLE_DISCOVERY_FINISHED =  "com.ad.intromi.BLE_DISCOVERY_FINISHED";
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
	protected static Handler msgHandler = null;
	/**BLE parameters**/
	 private static BluetoothLeService mBleService = new BluetoothLeService();
	 private static boolean mBleBound = false;
	 private static String  BLE_SERVICE = "com.ad.intromi.BluetoothLeService";
	 private static String  BT2_SERVICE = "com.ad.intromi.DiscoveryService";
	private ServiceManager(Context c){

		ServiceManager.context = c;
		LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter(ServiceManager.BT_DISCOVERY_FINISHED));	
		LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter(ServiceManager.BLE_DISCOVERY_FINISHED));	
		
		 msgHandler = new Handler()  {
        	 
	         
		    	public void handleMessage(Message msg)
		    	{
		      		switch(msg.what)
		    		{
		          		case 0:
		          			// add the status which came from service and show on GUI
		          			if(D) Log.i(TAG,"got got ++message++ from service for next feature");

		        		default:
		    			break;
		    		}
		    	}
		    };
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
        	
		parameters  =  new ServiceArgument("uniqueId","http://server", "port");
		Intent intent = new Intent(context, DiscoveryService.class);
		intent.putExtra("args",parameters);
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
        }
        else 
        	Log.e(TAG,"Bluetoth is not enable/discoverable");
		

	}

     public void startManualScan() {
    	 
    	 BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();

    	 if(D) Log.i(TAG,"Start manual scanning");
    	 if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    		 
        	 startBleManual();  
        }
    	 
    	 else   
    		 {
    		    Log.i(TAG,"This device does not support Bluetooth low energy");
    		    Log.i(TAG,"Start working with Bluetooth v2");
    		    startBt2Manual();
    		 }
        
         
         

         
	
}
	
	public  void stop() {

		stopBle();
//		 Unbind from the service
		if (mBound) {
			if(D) Log.i(TAG,"Unbind service DiscoveryService");
			context.unbindService(mConnection);
			mService.stopSelf();
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
			if (D) Log.i(TAG,"Service is disconnected");
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
       
/*
 * BLE section
 *  
 */
       public  void  startBle(){	
        if (D) Log.i(TAG,"Ready for running BluetoothLeService");
   		ServiceArgument  parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80");
   		Intent intent = new Intent(context, BluetoothLeService.class);
   		intent.putExtra("args",parameters);
   		context.bindService(intent, mBleConnection, Context.BIND_AUTO_CREATE); 

   	}

   	public  void stopBle() {
	    		 		 
		 if (mBleBound) {
			 if (D) Log.i(TAG,"Unbind service BluetoothLeService");	
			context.unbindService(mBleConnection);
			mBleBound = false;
			mBleService.stopSelf();
			
		}
	}
   	
   	
   	
   	
       /** Defines callbacks for service binding, passed to bindService() */
   	private static ServiceConnection mBleConnection = new ServiceConnection() {

   		@Override
   		public void onServiceConnected(ComponentName className,
   				IBinder service) {
   			// We've bound to LocalService, cast the IBinder and get LocalService instance
   			BleLocalBinder bleBinder = (BleLocalBinder) service;
			mBleService = bleBinder.getService();
   			mBleBound = true;
   		}

   		@Override
   		public void onServiceDisconnected(ComponentName arg0) {
   			if (D) Log.v(TAG,"Service BLE is disconnected");
   			mBleBound = false;
   		}
   	};

         public  void setBleLog(boolean yesNo) {
   	
            mService.setLog(yesNo);
         }
       
private void  startBt2Manual(){
  
	if (Utils.isBtDiscoverable() && Utils.isBtenabled()){         	
	     parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80",MANUAL_SCAN);
	     Intent intent = new Intent(context, DiscoveryService.class);
	     intent.putExtra("args",parameters);
	     context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
 
    	 }
    	 else 
         	Log.w(TAG,"Bluetoth is not enable/discoverable");
  }


private void  startBleManual(){
	
      
	
//first check if device support ble .
//	if (isServiceRunning(BLE_SERVICE)) System.out.println("Yes, service  is running");
//	else 
//		System.out.println("No , Service is not running");
		
	    if (D) Log.i(TAG,"Ready for running BluetoothLeService in manual mode");
		ServiceArgument  parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80");
		Intent intent = new Intent(context, BluetoothLeService.class);
		intent.putExtra("args",parameters);
		context.bindService(intent, mBleConnection, Context.BIND_AUTO_CREATE); 
//		if (isServiceRunning(BLE_SERVICE)) System.out.println("Yes, service  is running");
//		else 
//			System.out.println("No , Service is not runnign ");
}

private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

	@Override

	public void onReceive(Context context, Intent intent) {
                  
			if (ServiceManager.BLE_DISCOVERY_FINISHED.equals(intent.getAction())){

	               if (D) Log.i(TAG,"BLE discovery has finished");
	               if (D) Log.i(TAG,"Start BT2  discovery");
	                startBt2Manual(); 
	              // Need to start BT2 service  discovery/ 
//     			bleService.stop();
		                           
   //send broadcast that discovery has finished 
	    	}
    	}	
	 
	
 };

 private boolean isServiceRunning(String theService) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (theService.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}




	
	

