package com.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.pojo.Leads;

public class Sfmc {
	
	public static String authToken(){
		try{
			
			
			 /* Start of Fix */
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()  {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

	        } };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) { return true; }
	        };
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	        /* End of the fix*/

	        
			final URL obj = new URL("https://mcp3jpq9-bxqhwnvnnh7l2c82d84.auth.marketingcloudapis.com/v2/token");
			
			final JsonObject reqObj = new JsonObject();
			reqObj.addProperty("grant_type", "client_credentials");
			reqObj.addProperty("client_id", "mh6lg20p237d7mzsd26byixc");
			reqObj.addProperty("client_secret", "NAyomMyuBcrqpXR6D2Fnj0nH");
			reqObj.addProperty("account_id", "100037923");
			
			
			final String jsonInputString =  reqObj.toString();

			
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);

			con.setRequestMethod("POST");
			
			con.addRequestProperty("Content-Type","application/json");
		    con.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

			
			int responseCode = con.getResponseCode();
	        
	       
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				final StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				final JSONObject resultData = new JSONObject(response.toString());
				
				return resultData.getString("access_token");
			}
	        
		}catch(Exception error){
			error.printStackTrace();
		}
		return "";
	}
	
	public static void postSFMCData(final Leads lead){
		final String authToken = authToken();
		if(StringUtils.isNotBlank(authToken)){
			saveSFMCData(lead,authToken);
		}
		
	}
	
	public static void saveSFMCData(final Leads lead,final String authToken){
		try{
			
			
			 /* Start of Fix */
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()  {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

	        } };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) { return true; }
	        };
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	        /* End of the fix*/

			final URL obj = new URL("https://mcp3jpq9-bxqhwnvnnh7l2c82d84.rest.marketingcloudapis.com/interaction/v1/events");
			
			
			final String jsonInputString =  parseLeadData(lead);
			
			
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("Authorization","Bearer "+authToken);

			con.setDoOutput(true);

			con.setRequestMethod("POST");
			
			con.addRequestProperty("Content-Type","application/json");
		    con.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

			
			int responseCode = con.getResponseCode();
			
			System.out.println("Final response"+responseCode);
	        
	       
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
			}
	        
		}catch(Exception error){
			error.printStackTrace();
		}
	}

	public static String parseLeadData(final Leads lead){
	
		final JsonObject mainObj = new JsonObject();
		mainObj.addProperty("ContactKey", lead.leadID);

		mainObj.addProperty("EventDefinitionKey","APIEvent-ba2a0c9e-9f02-773a-6a8c-ccc3769dfe66");
		

		final JsonObject valuesObj = new JsonObject();
		
		valuesObj.addProperty("SubscriberKey", lead.leadID);
		valuesObj.addProperty("LeadID", lead.leadID);
		
		valuesObj.addProperty("FirstName", lead.leadName);
		valuesObj.addProperty("Address_Line_1", lead.street1);
		valuesObj.addProperty("City", lead.city);
		valuesObj.addProperty("ZipCode", lead.zipcode);
		valuesObj.addProperty("EmailAddress", lead.leadEmail);
		valuesObj.addProperty("PhoneNumber", lead.leadMobile);
		valuesObj.addProperty("State", lead.state);
		valuesObj.addProperty("ModeOfCommunication", lead.commMode);
		valuesObj.addProperty("SubscriberKey", "");

        mainObj.add("Data", valuesObj);
        System.out.println(mainObj.toString());
        
        return mainObj.toString();
		
		
	}
}
