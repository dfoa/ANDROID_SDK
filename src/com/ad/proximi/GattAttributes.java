package com.ad.proximi;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();

    public static String PROXIMI_SERVICE = "f44b0282-79a4-3d64-b00f-fda2ab64e200";

        
    static {
/*
 * Service here
 * 
 */
    	
        attributes.put(PROXIMI_SERVICE,"IntroMiService");
/*
 * Future characteristics here
 * 
 */
       
 //This the proximi service UUID      #define TRANSFER_SERVICE_UUID             @"f44b0282-79a4-3d64-b00f-fda2ab64e200"
 //This is just an example of some user        #define TRANSFER_CHARACTERISTIC_UUID      @"184cfbf3-4b5e-307d-8da9-6fef40c79113"
      
        
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

