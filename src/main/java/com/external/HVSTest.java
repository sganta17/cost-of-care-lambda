package com.external;

import org.json.JSONObject;

public class HVSTest {
	public static void main(String[] args){
		
		JSONObject obj = new JSONObject();
		obj.put("age", 23);
		obj.put("inflationRate", 3);
		obj.put("state", "AL");
		
		HVS.getHVSData(obj);
	}
}