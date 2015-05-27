package com.ad.proximi;


import java.util.ArrayList;



import android.R.menu;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


/*************************************************
 **Only if using API 21
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
 */

public class MainActivity extends ListActivity {
   
	public boolean mScanning;
	protected static final String TAG = "MainActivity";
	protected static final Boolean D = true;
	protected static final long DELAYBT = 60000;
	public   BluetoothDevice  	 device ;   
	ServiceManager  m;
//	BleServiceManager mService;
	boolean mBound = false;
//	private ServiceManager m;
	private static Context mContext;
	private ArrayList<String> coffeeChoices;
	 ArrayAdapter<String> adapter;
	private Menu  currentMenu;
	String nameToRegister;
	@Override
	protected void onStart() {
		super.onStart();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		//m.cleanFoundUsersList();
  
//To stop the service when application is stopped		
//		m.stop();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
  
	//	m.cleanFoundUsersList();
		  
		
//To stop the service when application is destroyed.		
		m.stop();
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.activity_main);
	    coffeeChoices = new ArrayList<String>();
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coffeeChoices);
	    setListAdapter(adapter);  //Provided by the ListActivity extension
		mContext = getApplicationContext();
		mScanning = false;


		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.ad.proxymi.ACTION_ERRORS"));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.ad.proxymi.ACTION_MESSAGE"));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.ad.proxymi.BT_DISCOVERY_FINISHED"));
		
		m = ServiceManager.getInstance(getApplicationContext());
//		m.start();
//		m.startManualScan();
		m.setLog(true);

		
		
		 //check BLE  service 
		 
		 

//		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.ad.proxymi.ACTION_ERRORS"));
//			m = BleServiceManager.getInstance(getApplicationContext());
//		m.start();
		 
		 //	    String uniqueId = "FIIX_UniqueID";
//		 Register r = Register.getInstance();
//		 System.out.println("Going to register the user ");
//	     r.doRegistration(getApplicationContext(), uniqueId);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_list, menu);
//		return true;
//	}

	
	
	
	

	// Method to start the service
	/*	public void startService() {

         ServiceArgument  parameters  =  new ServiceArgument("Fiix","http://192.168.50.5", "80");
 		 Intent  intent = new Intent(getApplicationContext(),DiscoveryService.class);
 		 intent.putExtra("args",parameters);
		 startService(intent);



	}
	 */
	// Method to stop the service
	public void stopService() {
	//	stopService(new Intent(getBaseContext(), DiscoveryService.class));
	}



	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

		@Override

		public void onReceive(Context context, Intent intent) {

			
			System.out.println("This is the name of intent" + intent.getAction());
			if (intent.getAction().equals("com.ad.proxymi.ACTION_MESSAGE")){
				Profile p = new Profile();
				System.out.println("profile is action message");
			    intent.getParcelableExtra("profile");
			    
				Bundle data = new Bundle();
				data = intent.getExtras();
				p = data.getParcelable("profile");
				System.out.println("This is the profile arrived from Service" + p.getId()  + "and the name is " + p.getName());
			     coffeeChoices.add(p.getName());
					// adding to the UI have to happen in UI thread
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
						    
							adapter.notifyDataSetChanged();
						}
					});
			     
			     
			}
			else 
				if (intent.getAction().equals("com.ad.proxymi.BT_DISCOVERY_FINISHED")){
					System.out.println("Discoveryhas finished12345");
									currentMenu.findItem(R.id.scanning_indicator).setVisible(false); 
									currentMenu.findItem(R.id.scanning_stop).setVisible(false);
									currentMenu.findItem(R.id.scanning_start).setVisible(true);
									m.stop();


					mScanning = false;
					
				}
				
			else 
			{
			switch (intent.getIntExtra("Error", -1)) {
			case Errors.ERR_BT_IS_NOT_DISCOVERABLE:
			{
				Log.v("BT is not discaoverable", null);
				break;
			}
			case Errors.ERR_BTV2_IS_NOT_SUPPORTED:
			{
				Log.v("BT is not supported", null);
				break;
			}


			}

		}
		}

	};    


	 public static Context getContext() {
	        return mContext;
	    }

	

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.scanning, menu);

	        if (mScanning) {
	            menu.findItem(R.id.scanning_start).setVisible(false);
	            menu.findItem(R.id.scanning_stop).setVisible(true);
	            menu.findItem(R.id.scanning_indicator)
	                .setActionView(R.layout.progress_indicator);

	        } else {
	            menu.findItem(R.id.scanning_start).setVisible(true);
	            menu.findItem(R.id.scanning_stop).setVisible(false);
	            menu.findItem(R.id.scanning_indicator).setActionView(null);
	        }
			currentMenu = menu;
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.scanning_start:
	            	mScanning = true;
	                m.startManualScan();
	                mScanning = true;
	                break;
	            case R.id.scanning_stop:
	            	mScanning = false;
	                m.stop();
	                mScanning = false;
	                break;
	            case R.id.show_hr_demo_item:
	                 
	            	System.out.println("Registered prerssed");
	            	final EditText name = new EditText(this);

	            	// Set the default text to a link of the Queen
	            	

	            	new AlertDialog.Builder(this)
	            	  .setTitle("Register to service")
	            	  .setMessage("Pls enter your name")
	            	  .setView(name)
	            	  .setPositiveButton("Save", new DialogInterface.OnClickListener() {
	            	    public void onClick(DialogInterface dialog, int whichButton) {
	            	       nameToRegister = name.getText().toString();
	            	       System.out.println(nameToRegister);
           				Register register = Register.getInstance();
           			    register.doRegistration(getApplicationContext(), "ThisIsmiID",nameToRegister);
	            	    }
	            	  })
	            	  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            	    public void onClick(DialogInterface dialog, int whichButton) {
	            	    }
	            	  })
	            	  .show(); 
            	
	            	break;
	        }
	        
	        invalidateOptionsMenu();
	        return true;
	    }

	 
	
	
	
}



	 

