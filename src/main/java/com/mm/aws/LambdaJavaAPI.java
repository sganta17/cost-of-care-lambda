package com.mm.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.common.GatewayResponse;
import com.external.HVS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * 
 * @author mmu9402
 *
 */

public class LambdaJavaAPI implements RequestHandler<Object,GatewayResponse> {
	  
	 
	 
	final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public GatewayResponse handleRequest(Object map , Context context) {
        final LambdaLogger logger = context.getLogger();
        

        String message = "Success";
    	int responseCode = 200;
    	try{
    		
    		// Parsing input request
    		final String object = gson.toJson(map);
    		

    		logger.log("Came inside request  :::exception");
    		final JsonObject leadFormData = gson.fromJson(object, JsonObject.class);
    		    		
    		final String httpMethod = leadFormData.get("httpMethod").getAsString();

    		logger.log("Came inside request  :::"+httpMethod);
    		//If it is options return success
    		if(StringUtils.equalsIgnoreCase(httpMethod, "OPTIONS")){
    			message = null;
    			responseCode = 200;
    		}else{
    		
	    		final JsonElement element = leadFormData.get("body");

	    		logger.log("Came inside request  :::"+element);
	    		// Converting input object as JSONObject
	    		JSONObject objectEle = null;
	    		if(element != null){
	    			objectEle = new JSONObject(element.getAsString());
	    		}
	    		
	    		message = HVS.getHVSData(objectEle).toString();

	    		logger.log("Came inside request message  :::"+message);
    		}
    		
    	}catch(Exception e){
    		logger.log(e.getMessage()+"  :::exception");
    	}

        //Parse response 
        final GatewayResponse response = new GatewayResponse(
        		message,
        		responseCode,getResponseHeader(),
                false
        );
        return response;
    }
    
   
    
    private static Map<String, String> getResponseHeader(){
    	   final Map<String, String> headers = new HashMap<String,String>();
           headers.put("Access-Control-Allow-Headers", "*");
           headers.put("Access-Control-Allow-Origin", "*");
           headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET,PUT");
           headers.put("X-Powered-By", "MM");
           
           return headers;
    }
   

	
}
