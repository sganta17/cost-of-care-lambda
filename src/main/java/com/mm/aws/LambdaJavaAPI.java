package com.mm.aws;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.common.GatewayResponse;
import com.common.SmartyValidation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pojo.Leads;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * 
 * @author mmu9402
 *
 */

public class LambdaJavaAPI implements RequestHandler<Object,GatewayResponse> {
	  
	 private static DynamoDB dynamoDb;
	 private static String DYNAMO_DB_TABLE_NAME = "leads";
	 private static Regions REGION = Regions.US_EAST_1;
	 
	 
	final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public GatewayResponse handleRequest(Object map , Context context) {
        final LambdaLogger logger = context.getLogger();
        

    	String message = "Saved Successfully!";
    	int responseCode = 200;
    	try{
    		
    		// Parsing input request
    		final String object = gson.toJson(map);
    		
    		
    		final JsonObject leadFormData = gson.fromJson(object, JsonObject.class);
    		    		
    		final String httpMethod = leadFormData.get("httpMethod").getAsString();
    		
    		//If it is options return success
    		if(StringUtils.equalsIgnoreCase(httpMethod, "OPTIONS")){
    			message = "OK";
    			responseCode = 200;
    		}else{

    	    	this.initDynamoDbClient();
    		
	    		final JsonElement element = leadFormData.get("body");
	    		
	    		// Converting input object as JSONObject
	    		final JSONObject objectEle = new JSONObject(element.getAsString());
	    		
	    		//JSON Object data validation
	    		final String validateResponse = validateAndProcessData(objectEle);
	    		
	    		//Executes if there are validation errors 
	    		if(StringUtils.isNotBlank(validateResponse)){
	    			message = validateResponse;
	    			responseCode = 400;
	    		}	
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
    
    private void initDynamoDbClient() {
    	  AmazonDynamoDBClient client = new AmazonDynamoDBClient();
    	  client.setRegion(Region.getRegion(REGION));
    	  dynamoDb = new DynamoDB(client);
    }
    
    /**
     * 
     * @param objectEle
     * @return
     */
    private static String validateAndProcessData(final JSONObject objectEle){
    	
    	final Leads leads = new Leads();
		

		final StringBuilder builder = new StringBuilder();

		//Email Validation
		final String email = objectEle.getString("email");
    	final boolean isEmailValid = validateWithRegex(email,"^[A-Za-z0-9+_.-]+@(.+)$",true);
    	
    	if(!isEmailValid){
    		builder.append("Email is not Valid! /n");
    	}
    	leads.leadEmail = email;
    	
    	//Name Validation
		final String name = objectEle.getString("name");

    	final boolean isNameValid = validateWithRegex(name,"^[a-zA-Z0-9'._ -]+$",true);
    	
    	if(!isNameValid){
    		builder.append("Name is not Valid! /n");
    	}
    	leads.leadName = name;
    	
    	//Mobile Number Validation based on client
    	final String mobileNumber = objectEle.getString("mobileNumber");

    	boolean isNoValid = true;
    	

		final String clientname = objectEle.getString("clientname");

        if (StringUtils.equals(clientname, "mmbu")) {
        	isNoValid = validateWithRegex(mobileNumber,"[\\(]\\d{3}[\\)] \\d{3}[\\-]\\d{4}$",true);
        } else if (StringUtils.equals(clientname, "mmjebit")) {

        	isNoValid = validateWithRegex(mobileNumber,"[\\(]\\d{3}[\\)] \\d{3}[\\-]\\d{4}$",false);
        } 

    	leads.leadMobile = mobileNumber;

    	if(!isNoValid){
    		builder.append("Mobile is not Valid! /n");
    	}
        
        long start = System.currentTimeMillis();

    	//Address Validation using Smarty
    	final String address = objectEle.getString("address");
    	
    	if(StringUtils.isNotBlank(address)){
    		final String addressArry[] = address.split(",");
    		
    		boolean isAddressValid = SmartyValidation.addressValidation(addressArry[0], addressArry[1], addressArry[2], addressArry[3]);

        	leads.street1 = addressArry[0];
        	leads.city = addressArry[1];
        	leads.state = addressArry[2];
        	leads.zipcode = addressArry[3];
        	
    		if(!isAddressValid){
        		builder.append("Address is not Valid! /n");
        	}
    	}else{
    		builder.append("Address is not Valid! /n");
    	}

    	long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; 
        System.out.println(sec + " Excution time to validate Address");

    	if(StringUtils.isBlank(builder.toString())){
    		persistData(leads);
    	}
        
    	return builder.toString();
    }
    
    
    
    
    private static boolean validateWithRegex(final String fieldValue,final String regex,final boolean isFieldMandatory ){
    	
    	if(isFieldMandatory && StringUtils.isBlank(fieldValue)){
    		return false;
    	}else if(StringUtils.isNotBlank(fieldValue)){
    		
	    	final Pattern pattern = Pattern.compile(regex);
	    	 
	    	final Matcher matcher = pattern.matcher(fieldValue);
	    	return matcher.matches();
    	}
    	
    	return true;
    }
    
    private static Map<String, String> getResponseHeader(){
    	   final Map<String, String> headers = new HashMap<String,String>();
           headers.put("Access-Control-Allow-Headers", "*");
           headers.put("Access-Control-Allow-Origin", "*");
           headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET,PUT");
           headers.put("X-Powered-By", "MM");
           
           return headers;
    }
    
    private static PutItemOutcome persistData(final Leads leads) {
    	  final Table table = dynamoDb.getTable(DYNAMO_DB_TABLE_NAME);
          final UUID uuid = UUID.randomUUID();

    	  final PutItemOutcome outcome = table.putItem(new PutItemSpec().withItem(
    	    new Item().withString("lead_id",uuid.toString())
    	               .withString("lead_name", leads.getLeadName())
    	               .withString("lead_email", leads.getLeadEmail())
    	               .withString("lead_mobile", leads.getLeadEmail())
    	               .withString("city", leads.getCity())
    	               .withString("state", leads.getState())
    	               .withString("street1", leads.getStreet1())
    	               .withString("zipcode", leads.getZipcode())
    	               ));
    	  return outcome;
   }

	
}
