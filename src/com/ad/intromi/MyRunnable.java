package com.ad.intromi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONStringer;




public class MyRunnable implements  Runnable {

	
	private long mTime;
	private String mFounfMac;
	private String mSelfMac;
	private String id;
	private int  action;
	private static final int FIND_NEARBY = 1;
	private static final int REGISTER =2;
	public MyRunnable(String selfMac , String foundMac,int action){
		
		this.mTime = System.currentTimeMillis();
		this.mFounfMac = foundMac; 
	    this.mSelfMac = selfMac;
	    this.action = action;
	
	 
	} 
	
	@Override
	public void run() {
		
		System.out.println("In runnable");
		String URL_STRING="http://31.168.241.149/cgi-bin/json.cgi";
		System.out.println("This is the action  " + FIND_NEARBY);
		if (action == FIND_NEARBY) {
			
			 System.out.println("Going to open socket for FIND");
			
		try {
		   
//			MyRunnable.executeHttpPost(URL_STRING, Utils.buildJson(this.mSelfMac,this.mFounfMac,this.mTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Problem socket");
			e.printStackTrace();
		}
			 }
		
		else {
			if (action == REGISTER){
				System.out.println("Going to open socket for REGISTER");
				try {
					
//					CustomHttpClient.executeHttpPost(URL_STRING, Utils.buildJson(this.mSelfMac,this.id,this.mTime));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		

	}
		
		
//		System.out.println("My string is: " + str);
//		System.out.println("My int is: " + i);
	
	  public static String executeHttpPost(String url, JSONStringer postParameters) throws Exception {
	        BufferedReader in = null;
	        try {
	            HttpClient client = new DefaultHttpClient();
	            
	            HttpPost request = new HttpPost(url);
	            String s = "found_nearby=";            
	            StringEntity entity = new StringEntity(s+ postParameters.toString());       
	            request.setHeader("Accept", "application/json");
	            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
	            request.setEntity(entity);
	            HttpResponse response = client.execute(request);
	            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); 
	            StringBuffer sb = new StringBuffer("");
	            String line = "";
	            String NL = System.getProperty("line.separator");
	            while ((line = in.readLine()) != null) {
	                sb.append(line + NL);
	            }
	            in.close();
	 
	            String result = sb.toString();
	            System.out.println("Tis is the result" + result);
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
		
	}

	
	

