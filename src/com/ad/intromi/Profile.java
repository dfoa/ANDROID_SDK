package com.ad.intromi;



import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class Profile implements Parcelable {


  public Profile() {
 
    
    
  }

  public  String getSelfMac() {
	return selfMac;
}
  public  void setSelfMac(String selfMac) {
	this.selfMac = selfMac;

}

  
  public  String getRemoteMac() {
	return remoteMac;
}
  public  void setRemoteMac(String remoteMac) {
	this.remoteMac = remoteMac;
}

  public  String getId() {
	return id;
}
  public  void setId(String id) {
	this.id = id;;
}
  



	public String getFirstName() {
	return firstName;
}





public void setFirstName(String firstName) {
	this.firstName = firstName;
}





	public String getLastName() {
	return lastName;
}





public void setLastName(String lastName) {
	this.lastName = lastName;
}





	public String getName() {
	return name;
}





public void setName(String name) {
	this.name = name;
}





	public String getMobilePhoneNum() {
	return MobilePhoneNum;
}





public void setMobilePhoneNum(String mobilePhoneNum) {
	MobilePhoneNum = mobilePhoneNum;
}





	public String getHomePhoneNum() {
	return homePhoneNum;
}





public void setHomePhoneNum(String homePhoneNum) {
	this.homePhoneNum = homePhoneNum;
}





	public String getPicture() {
	return picture;
}





public void setPicture(String picture) {
	this.picture = picture;

}





	public String getProfessionalHeadLine() {
	return professionalHeadLine;
}





public void setProfessionalHeadLine(String professionalHeadLine) {
	this.professionalHeadLine = professionalHeadLine;
}





	public String getEmail() {
	return email;
}





public void setEmail(String email) {
	this.email = email;
}





	public String getHomeAddress() {
	return homeAddress;
}





public void setHomeAddress(String homeAddress) {
	this.homeAddress = homeAddress;
}





	public String getWorkAddress() {
	return workAddress;
}





public void setWorkAddress(String workAddress) {
	this.workAddress = workAddress;
}





	public String getSite() {
	return site;
}





public void setSite(String site) {
	this.site = site;
}




public  String getPosition() {
	return position;
}





public  void setPosition(String position) {
	this.position = position;
}





	public String getDescription() {
	return description;
}





public void setDescription(String description) {
	this.description = description;
}





	public String getMission() {
	return mission;
}





public void setMission(String mission) {
	this.mission = mission;
}





	public String getCompany() {
	return company;
}





public void setCompany(String company) {
	this.company = company;
}





	public String getOption1() {
	return option1;
}





public void setOption1(String option1) {
	this.option1 = option1;
}





public String getOption2() {
	return option2;
}

public void setToken(String token) {
	this.token = token;
}

public String getToken() {
	return token;
}




public void setOption2(String option2) {
	this.option2 = option2;
}

public Bitmap getImg() {
	
	return bm;
}



	//members
    private  String token;
    private  String selfMac;
	private  String remoteMac;
	private  String id;
	private  String firstName;
	private  String lastName;
	private  String name;
	private  String MobilePhoneNum;
	private  String homePhoneNum; 
	private  String picture;
	private  String professionalHeadLine;
	private  String email;
	private  String homeAddress;
	private  String workAddress;
	private  String site;
	private  String position;
	private  String description;
	private  String mission;
	private  String company;
	private  String option1;
	private  String option2;
	private transient Bitmap bm;

	
	

	
	

	
	
	
	public void setImg(String img) {


		
		
		
		
		
		
		
		
		 //    ImageLoader	                    DisplayImage("http://192.168.1.28:8082/ANDROID/images/BEVE.jpeg", holder.itemImage);
				
				
				//decode from base_64 to real PNG format

				
				byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				//im.setImageBitmap(decodedByte);
				bm = decodedByte;
			}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeStringArray(new String[] 
				{this.selfMac,

				this.id,

				this.remoteMac});

		
		// TODO Auto-generated method stub
		
	}

	
	   public static final Parcelable.Creator<Profile> CREATOR = new Creator<Profile>() { 
		   	  public Profile createFromParcel(Parcel source) { 
		   	      Profile profile = new Profile(); 
//		   	      profile.selfMac = BluetoothAdapter.getDefaultAdapter().getAddress(); 
		   	      profile.remoteMac = source.readString();
                  profile.id =  source.readString();

		   	     
		   	      return profile; 
		   	  } 

		public Profile[] newArray(int size) {

			return new Profile[size];

		}
		
		

	};
	
	

} 