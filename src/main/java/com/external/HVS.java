package com.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.YearMonth;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.google.gson.JsonObject;

public class HVS {
	
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

	        
			final URL obj = new URL("https://authdev.hvsfinancial.com/Token");
			
			
			final String jsonInputString =  "username=massmutualltcdemo&password=IL19SeO6$H@v3e8*E&grant_type=password";
			
			System.out.println(jsonInputString);

			
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setDoOutput(true);

			con.setRequestMethod("POST");
			
			con.addRequestProperty("Content-Type","application/json");
		    con.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

			
			int responseCode = con.getResponseCode();
			
			System.out.println(responseCode);
	        
	       
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
	
	
	public static JSONObject getHVSData(final JSONObject objectEle){
		final String authToken = authToken();
		if(StringUtils.isNotBlank(authToken)){
			return pullHVSData(objectEle,authToken);
		}
		return null;
	}
	
	
	public static JSONObject pullHVSData(final JSONObject objectEle,final String authToken){
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

			final URL obj = new URL("https://apidemo.hvsfinancial.com/api/CoreReports/LongTermCareReport");
			
			
			final String jsonInputString =  parseInputData(objectEle);
			
			
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("Authorization","Bearer "+authToken);

			con.setDoOutput(true);

			con.setRequestMethod("POST");
			
			con.addRequestProperty("Content-Type","application/json");
		    con.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

			
			int responseCode = con.getResponseCode();
			
			System.out.println("Final response"+responseCode);
	        
	       
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				final StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				return new JSONObject(response.toString());
				
			}
	        
		}catch(Exception error){
			error.printStackTrace();
		}
		return null;
	}

	public static String parseInputData(final JSONObject objectEle){
	
		final JsonObject mainObj = new JsonObject();
		
		final JsonObject valuesObj = new JsonObject();
		valuesObj.addProperty("investmentItems","{\"investmentItems\":[]}");

		valuesObj.addProperty("FrmHasPartner", false);
		
		//Name
		valuesObj.addProperty("FrmName0", "test");
		valuesObj.addProperty("FrmName1","");
		valuesObj.addProperty("FrmGender0",1);
		
		if(objectEle == null || !objectEle.has("age")){
			valuesObj.addProperty("FrmCurrentAge0",0);
			valuesObj.addProperty("FrmPlanningAge0",89);
		}else{
			final int age = objectEle.getInt("age");
			valuesObj.addProperty("FrmCurrentAge0",age);
			

			if(objectEle == null || !objectEle.has("futureCost")){
				valuesObj.addProperty("FrmPlanningAge0",89);
			}else{
				final int futureCost = objectEle.getInt("futureCost");
				final int year = YearMonth.now().getYear();
				final int futureCostAge = age + (futureCost - year);
				valuesObj.addProperty("FrmPlanningAge0",futureCostAge);
			}
		}
		
		
		
		valuesObj.addProperty("FrmGender1",0);
		valuesObj.addProperty("FrmCurrentAge1",0);
		
		if(objectEle == null || !objectEle.has("state")){
			valuesObj.addProperty("FrmLTCState0",0);
		}else{
			valuesObj.addProperty("FrmLTCState0",objectEle.getString("state"));
		}
		
		
		valuesObj.addProperty("FrmHealth0",0);
		valuesObj.addProperty("FrmHealth1",0);
		valuesObj.addProperty("FrmLTCState1","");
		
		if(objectEle == null || !objectEle.has("inflationRate")){
			valuesObj.addProperty("FrmPhase1Rate",6);
		}else{
			valuesObj.addProperty("FrmPhase1Rate",objectEle.getInt("inflationRate"));
		}
		
		valuesObj.addProperty("FrmPhase1Periods",0);
		valuesObj.addProperty("FrmPhase2Rate",0);
		valuesObj.addProperty("FrmPhase2Periods",0);
		valuesObj.addProperty("FrmPhase3Rate",0);
		valuesObj.addProperty("FrmLTCMonths0",12);
		valuesObj.addProperty("FrmLTCMonths1",12);
		valuesObj.addProperty("FrmPlanningAge1",0);
		
		if(objectEle == null || !objectEle.has("region")){
			valuesObj.addProperty("MetroRegion0",0);
		}else{
			valuesObj.addProperty("MetroRegion0",objectEle.getInt("region"));
		}
		valuesObj.addProperty("MetroRegion1",0);
		valuesObj.addProperty("InvestmentYears",12);
		valuesObj.addProperty("InvestmentOption",2);
		valuesObj.addProperty("CareType",1);
		valuesObj.addProperty("CareType1",1);


        mainObj.add("ltcData", valuesObj);
        System.out.println(mainObj.toString());
        
        return mainObj.toString();
		
		
	}
}
