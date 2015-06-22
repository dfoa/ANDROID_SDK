

package com.ad.intromi;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import java.util.Queue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONStringer;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private boolean mScanning;
	private Handler mHandler;
	/** Yes/No device in array list  */
	private boolean mFound = false ;
	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 5000;
	private Thread mScanThread;
	private static final String TAG ="BluetoothLeService" ;
	private static  boolean D = true;
	private ArrayList<String> mArrayOfMacs;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED =
			"com.ad.proxymi.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED =
			"com.ad.proxymi.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED =
			"com.ad.proxymi.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE =
			"com.ad.proxymi.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA =
			"com.ad.proxymi.le.EXTRA_DATA";
	public final static UUID UUID_PROXIMI_SERVICES =
			UUID.fromString(GattAttributes.PROXIMI_SERVICE);

	public static final String BT_DISCOVERY_FINISHED = "com.ad.intromi.BT_DISCOVERY_FINISHED";
	private String URL_STRING="http://intromi.biz/exec/user_lookup";

	/**hold array with  all device that were found for maintaining connections**/
	private Item conProperty;
	private ArrayList<Item>  mArrayOfconnections = new ArrayList<>();
	private ArrayList<BluetoothDevice> mArrayOfDevices = new ArrayList<>();
	private Queue queue ;
	private boolean lock = false;
	private Thread t;
	private int globalInd = 0;
	private boolean threadRunning = true;
	public static Handler handler = null;
	private  BluetoothDevice mLastDevice; 
	
	/** The service is starting, due to a call to startService() */

	//	@Override
	public int onStartCommand(Intent intent, int flags, int startId) throws SecurityException{

		if (D) Log.v(TAG,"In onstart command");

		return START_REDELIVER_INTENT;
	}



	public void onCreate() {
		if (D)  Log.i(TAG,"+++onCreate++++");
		// Use this check to determine whether BLE is supported on the device.  Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			if (D) Log.e(TAG,"BLE is not supported");

		}

		mArrayOfMacs = new ArrayList<String>();

		// Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
		// BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			//    Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			//   finish();
			return;
		}

		mHandler = new Handler();
		//Thread to scan every x minutes		


		//this is for manual scan

		scanLeDevice(true);
		//	scanner.startScan(null);
		t = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (threadRunning) {
					if (!lock) {
						if (D) Log.d(TAG,"lock is " + lock);

						if (!mArrayOfMacs.isEmpty()){
							if(D) Log.d(TAG, "global index  is " + globalInd);

							for (int i = globalInd ;i<mArrayOfDevices.size() && !lock; globalInd++) {
								lock = true;
								if (D) Log.d(TAG,"working on device "+mArrayOfDevices.get(i).getAddress());
								//check if last device to handle
								if(i+1 == mArrayOfDevices.size()){

									//means that this is last device to handle
									mLastDevice = mArrayOfDevices.get(i);
									if (D) Log.d(TAG,"This will be the last one " + mLastDevice);
									threadRunning = false;
								}
								connect(mArrayOfDevices.get(i));

								Message msg = new Message();
								msg.what = 0; 

								msg.obj  = "Request Received. Service is Running"; // you can put extra message here

								ServiceManager.msgHandler.sendMessage(msg);							

							}
						} 
						else 
							threadRunning = false;

					}		 

				}
			}



			// TODO Auto-generated method stub


		}); t.start();





		/*
Thread thread = new Thread("discoverBLE"){

	public void run(){


		//	while(!Thread.currentThread().isInterrupted()){

		Looper.prepare(); 
		//               BluetoothAdapter mBluetoothAdapter =BluetoothAdapter.getDefaultAdapter();

		for(;;)
		{ 


			scanLeDevice(true);
			try {
				if (D) Log.v(TAG, "scan Thread is going to sleep");
				Thread.sleep(60000);
				if (D) Log.v(TAG, "scan Thread is going to wakeup");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Looper.loop();
		}



	} 

	//	}
}; thread.start();

		 */

	}




	// Implements callback methods for GATT events that the app cares about.  For example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

			boolean last = ifLastDevice(gatt); 
			if (status==133) {
				gatt.disconnect();
				gatt.close();
				lock = false;

				if (last) {
					updateFinishedDiscovery();
				}

			}
			if (newState == BluetoothProfile.STATE_CONNECTED) {

				mConnectionState = STATE_CONNECTED;
				//              broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery");

				if (!gatt.discoverServices()) {

					gatt.disconnect();
					gatt.close();
					lock = false;
					if (last) {


					}
				}


			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				gatt.close();
				//          broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {

			if (D) Log.i(TAG,"++++onServicesDiscovere+++++");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				//   broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);


				readServices(gatt.getService(UUID_PROXIMI_SERVICES),gatt);



			} else {
				//In case I got any other answer than success I need to close  down the connection.

				Log.w(TAG, "onServicesDiscovered received: " + status);
				boolean last = ifLastDevice(gatt); 
				if (last) {
					Intent intent = new Intent(ServiceManager.BLE_DISCOVERY_FINISHED);
					LocalBroadcastManager.getInstance(getApplicationContext());
					LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
				}

				gatt.close();
				lock = false;
				return;
			}
		}



		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if(status == BluetoothGatt.GATT_SUCCESS)
				if (D) Log.d(TAG,"In characteristics write");
			gatt.close();
		}
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic,
				int status) {

			if(D) Log.i(TAG,"++In onCharacteristicRead++++++");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				//               broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
				if(D) Log.i(TAG,"new characteristics read from device " +characteristic.toString());
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			if (D) Log.i(TAG,"+++++onCharacteristicChanged+++++++");
			//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);



	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile.  Data parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		System.out.println("In broadcastUpdate checking characteristic");
		if (UUID_PROXIMI_SERVICES.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				Log.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				Log.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			Log.d(TAG, String.format("Received heart rate: %d", heartRate));
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(data.length);
				for(byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
				intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
			}
		}
		sendBroadcast(intent);
	}

	public class BleLocalBinder extends Binder {
		BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if(D) Log.v(TAG,"+++onBind BLE service+++");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that BluetoothGatt.close() is called
		// such that resources are cleaned up properly.  In this particular example, close() is
		// invoked when the UI is disconnected from the Service.
        if(D) Log.i(TAG," ++++ in onUnbind. Clean everything");
         
        if (D) Log.d(TAG,"Setting scanLeDevice to false");
		scanLeDevice(false);
		  if (D) Log.d(TAG,"Setting lock to false");
		lock = false;
		if(D) Log.d(TAG,"lock=" + lock);
		if  (t.isAlive()) {
			if (D) Log.d(TAG,"Trying to stop gatt connect queue thread"); 
			stopThread(t);
			if (t.isAlive()){

				if (D) Log.d(TAG,"Thread is still running");
			}  
			else
				if (D) Log.d(TAG,"Thread is not running");

		}
		
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new BleLocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. The connection result
	 *         is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public  boolean connect(final BluetoothDevice device) {
		if (mBluetoothAdapter == null ||device.getAddress() == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			lock = false;
			if(D) Log.d(TAG ,"lock=" + lock);
			return false;

		}


		// Previously connected device.  Try to reconnect.
		//       if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
		//               && mBluetoothGatt != null) {
		//            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
		//            if (mBluetoothGatt.connect()) {
		//                mConnectionState = STATE_CONNECTING;
		//               return true;
		//           } else {
		//               return false;
		//           }
		//      }

		//      final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		//       if (device == null) {
		//           Log.w(TAG, "Device not found.  Unable to connect.");
		//            return false;
		//       }
		// We want to directly connect to the device, so we are setting the autoConnect
		// parameter to false.
		//     Item item = new Item();
		Log.d(TAG, "Trying to create a new connection.");
		device.connectGatt(this, false, mGattCallback);
		//device.connectGatt(this, false, mGattCallback);
		//     mArrayOfconnections.add(item);

		//    mBluetoothDeviceAddress = device.getAddress();
		mConnectionState = STATE_CONNECTING;
		return true;

	}
	/**
	 * Disconnects an existing connection or cancel a pending connection. The disconnection result
	 * is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect(BluetoothGatt  gatt) {
		if (mBluetoothAdapter == null || gatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		gatt.disconnect();
		lock = false;
		if (D) Log.d(TAG,"lock=" + lock);
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */
	public void close(BluetoothGatt gatt) {
		if (gatt == null) {
			lock = false;
			if(D) Log.d(TAG,"lock=" + lock);
			return;
		}
		gatt.close();
		gatt = null;
		lock = false;
		if(D) Log.d (TAG,"lock=" + lock);

	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
	 * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic Characteristic to act on.
	 * @param enabled If true, enable notification.  False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
			boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		// This is specific to Heart Rate Measurement.
		if (UUID_PROXIMI_SERVICES.equals(characteristic.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
					UUID.fromString(GattAttributes.PROXIMI_SERVICE));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This should be
	 * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null) return null;

		return mBluetoothGatt.getServices();
	}


	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);

				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);

		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);

		}

	}
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

			if(D) Log.i(TAG,"This is the device I found " + device.getName());
			//    System.out.println("And this is the UUID of the device " +  device.getUuids().toString());
			//found here deveice 
			//need to check if mac address is IOS
			//going to connect to gat 
			//check in the list if this device has not already found



			if (D) Log.i(TAG,"current list " + mArrayOfMacs.toString());
			//IntroMi

			if (D) Log.i(TAG,"Found device "+ device.getAddress());
			// if (device.getName().equalsIgnoreCase("IntroMi")) {
			//future implementation	
			//mRssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

			// If it's already paired or already listed, skip it, because it's has been listed already
			//           System.out.println("This is the device name" + device.getName());
			//				           System.out.println("This is the length of the name :\n" +  mDevice.getName().length()
			//            showToast("Device name is\n " + device.getName() +"length:\n" + device.getName().length());

			//check if array first is first used

			mFound = false;
			if (mArrayOfMacs.isEmpty()){			
				printToLog("Devices list is Empty");
				mFound = true;
				//add mac address to the list
				if (D) Log.i(TAG,"adding" + device.getAddress()+ "to the List, and will later create gatt connect");
				mArrayOfMacs.add(device.getAddress());
				mArrayOfDevices.add(device);
				

				// connect(device);

			}
			else {
				for(int i=0 ;i<mArrayOfMacs.size() && !mFound;i++) {

					if (D) Log.v(TAG,"item " +i+" inth array is" + mArrayOfMacs.get(i));
					if (mArrayOfMacs.get(i).equalsIgnoreCase(device.getAddress())) {

						if (D) Log.v(TAG,"device " + device.getAddress() + "is in the list already ");

						mFound = true;
						if (D) Log.v(TAG, "The device " + device.getAddress() + "device name "  + device.getName()+  " is already in the list -- ignore this mac");	
					}


				}


				//									System.out.println("update device  " + device.getAddress() +" with RSSI: "+mRssi );

			}

			if (!mFound ) {


				if (D) Log.v(TAG,"Adding device " + device.getAddress() +" to array");
				if (D) Log.v(TAG,"This is the current devices array" + mArrayOfMacs.toString());
				if (mArrayOfMacs.size() >= 1){
					mArrayOfMacs.add(device.getAddress());
					mArrayOfDevices.add(device); 
				}

				if (D) Log.v(TAG,"connect to device" + device.getAddress());		
				//	 connect(device);
			}

			mFound = false;



		}

	};

	public void  setLog(boolean print){

		if (print) D = true;
		else D = false;    	
	}

	private void printToLog(String msg){

		if (D) Log.v(TAG, msg);
	}


	protected void readServices(BluetoothGattService bluetoothGattService, BluetoothGatt gatt){

		if(D) Log.i(TAG," ++++++in readServices+++++++");
		boolean last = ifLastDevice(gatt); 

		if (bluetoothGattService == null){


			if(D) Log.i(TAG,"Bluetooth service of device " +gatt.getDevice().getAddress() +"id not matching our serice --ignore thus device");
			gatt.disconnect();
			gatt.close();

			if (last) {
				Intent intent = new Intent(ServiceManager.BLE_DISCOVERY_FINISHED);
				LocalBroadcastManager.getInstance(getApplicationContext());
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			}
			else lock = false;
			return;
		}

		String  foundDevice = null;

		foundDevice = bluetoothGattService.getCharacteristics().get(0).getUuid().toString(); 
		BluetoothGattCharacteristic characteristic =
				bluetoothGattService.getCharacteristics().get(0);

		if (characteristic == null) {
			Log.w(TAG, "Characteriscis is null");

		}
		byte[] data = new byte[3];
		data[1]=0x01;
		characteristic.setValue(mBluetoothAdapter.getAddress());
		characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
		if(D) Log.i(TAG,"Write data to device " + foundDevice);
		gatt.writeCharacteristic(characteristic);
		new QueryIdentityFromServer().execute(BluetoothAdapter.getDefaultAdapter().getAddress(),foundDevice.toUpperCase());



		if(last) {
			if (D) Log.i(TAG,"Working on last device in gatt connect queue");
			Intent intent = new Intent(ServiceManager.BLE_DISCOVERY_FINISHED);
			LocalBroadcastManager.getInstance(getApplicationContext());
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
		else {
			lock = false;
		}

		//	        for (BluetoothGattService gattService : bluetoothGattService) {

		//	            uuid = gattService.getUuid();
		//	             if(uuid.equals(UUID_PROXIMI_SERVICES)){
		//	            System.out.println("This is the service UUID\n" + uuid);
		//	             System.out.println(gattService.getCharacteristics().get(0).getUuid().toString());
		//	             }
		//	             
		//	        }
	}


	private class QueryIdentityFromServer extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected  String doInBackground(String...string) {

			String s = null;
			if (D) Log.v(TAG,"source device  " + string[0]);
			if (D) Log.v(TAG,"found  device  " + string[1]);

			try {				
				s = executeHttpPost(URL_STRING, Utils.buildJson(string[0],string[1],System.currentTimeMillis()));
			}catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			return s;
		}

		@Override
		protected void onPostExecute(String s) {

			if (!s.isEmpty()) {


				BroadcastMeesage(Utils.parseJson(s));
			}
		}
	}

	public String executeHttpPost(String url, JSONStringer postParameters)   {
		//   public String executeHttpPost(String url) throws Exception {

		BufferedReader in = null;
		StringEntity entity = null;
		HttpResponse response = null;
		HttpPost request;
		try {
			HttpClient client = new DefaultHttpClient();
			request = new HttpPost(url);
			String s = "found_nearby=";            	
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

				HttpEntity h = response.getEntity();
				if(h.getContentLength() == 0)
				{
					if (D) Log.v(TAG, "Couldnt find this device in DB");
					//BroadcastErrors(Errors.ERR_INTROMI_SERVER_IS_UNREACHABLE);
				}
				break;

			case 500:
				BroadcastErrors(Errors.ERR_INTROMI_SERVER_IS_UNREACHABLE);

				break;


			default:
				//				  BroadcastErrors(Errors.ERR_INTROMI_SERVER_IS_UNREACHABLE);
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
			printToLog("This is the result" + result);

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

	protected void  BroadcastMeesage(Profile p) {

		Intent intent = new Intent(ServiceManager.MESSAGE);
		Profile profile = new Profile();
		profile = p;
		profile.setSelfMac(BluetoothAdapter.getDefaultAdapter().getAddress());

		intent.putExtra("Profile",profile);
		LocalBroadcastManager.getInstance(getApplicationContext());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public void  BroadcastErrors(int e) {

		Intent intent = new Intent(ServiceManager.ERRORS);	
		intent.putExtra("Error", e);
		if (D) Log.v(TAG,"Got error number " + e);
		LocalBroadcastManager.getInstance(getApplicationContext());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private synchronized void stopThread(Thread t)
	{
		if (t != null)
		{
		    if(D) Log.d(TAG,"stopping thread");
			threadRunning = false;
			notify();
		}
	}


	private boolean ifLastDevice (BluetoothGatt g) {

		if (g!=null && mLastDevice!=null) {
			if (mLastDevice.getAddress().equalsIgnoreCase(g.getDevice().getAddress()))
				return true;  	    
		}
		return false;
	}

	private void updateFinishedDiscovery(){

		Intent intent = new Intent(ServiceManager.BLE_DISCOVERY_FINISHED);
		LocalBroadcastManager.getInstance(getApplicationContext());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}




}







