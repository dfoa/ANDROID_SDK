package com.ad.proximi;




import com.ad.proximi.BluetoothLeService.LocalBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class BleServiceManager {

	private static BluetoothLeService mService = new BluetoothLeService();
	private static boolean mBound = false;
	private static Context context;
	private static BleServiceManager   _instance;
	/** print log  **/
	static Boolean D = true;
	/** TAG description*/
	static String TAG = "BleServiceManager";

	private BleServiceManager(Context c){

		BleServiceManager.context = c;
						
		
	}

	public synchronized  static BleServiceManager getInstance(Context c)
	{

		if (_instance == null)
		{
			_instance = new BleServiceManager(c);
		}
		return _instance;

	}

	public  void  start(){	
        System.out.println("Im going to start BLEservoce");
		ServiceArgument  parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80");
		Intent intent = new Intent(context, BluetoothLeService.class);
		intent.putExtra("args",parameters);
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 

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
              if (D) Log.v(TAG,"++++Service connected");
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
}
