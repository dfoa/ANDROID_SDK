package com.ad.intromi;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceArgument implements Parcelable{

	private String mCompanyId;
	
	private String mServer;
	
	private String mPort;
	
	private String  mMode;

	// Constructor

	public ServiceArgument(String companyId, String server, String port,String mode){

		this.mCompanyId = companyId;

		this.mServer = server;

		this.mPort = port;
		
		this.mMode = mode;
	}
	
	public ServiceArgument(String companyId, String server, String port){

		this.mCompanyId = companyId;

		this.mServer = server;

		this.mPort = port;
		

	}

	// Getter and setter methods

   public String getCompanyId(){
	   return this.mCompanyId;
   }
   
   public String getServer(){
	   return this.mServer;
   }
   
   public String getPort(){
	   return this.mPort;
   }
   
   public String getMode(){
	   return this.mMode;
   }


	// Parcelling part

	public ServiceArgument(Parcel in){

		String[] data = new String[4];



		in.readStringArray(data);

		this.mCompanyId = data[0];

		this.mServer = data[1];

		this.mPort = data[2];
		
		this.mMode  = data[3];

	}



	@Override
	public int describeContents(){

		return 0;

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeStringArray(new String[] {this.mCompanyId,

				this.mServer,

				this.mPort,
				
		        this.mMode});

	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		public ServiceArgument createFromParcel(Parcel in) {

			return new ServiceArgument(in); 

		}


		public ServiceArgument[] newArray(int size) {

			return new ServiceArgument[size];

		}

	};

}