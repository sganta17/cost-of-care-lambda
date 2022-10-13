package com.external;

import org.json.JSONObject;

public class HVSTest {
	public static void main(String[] args){
		
		JSONObject obj = new JSONObject();
		obj.put("state", "AL");
		obj.put("futureCost", "2026");
		obj.put("age", "23");
		
		
		HVS.getHVSData(obj);
	}
}