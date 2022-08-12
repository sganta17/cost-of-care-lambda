package com.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;

public class SmartyValidation {
	
	public static boolean addressValidation(final String street,final String city,final String state,final String zipcode){
		try{
			
			
            String charset = "UTF-8";

			final String query = "auth-id=7c65f708-ed0d-2ece-ef51-3ef641ebde9d&auth-token=IHtD3yyIKidvSsTZByPu&street="+
	                   URLEncoder.encode(street,charset)+"&city="
	                   +URLEncoder.encode(city,charset)+"&state="
	                   +URLEncoder.encode(state,charset)+"&zipcode="
	                   +URLEncoder.encode(zipcode,charset);
			
			
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

	        
			final URL obj = new URL("https://us-street.api.smartystreets.com/street-address?"+query);
			
			
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			
			con.addRequestProperty("Content-Type","application/json");
			con.addRequestProperty("Host","us-street.api.smartystreets.com");
			
			int responseCode = con.getResponseCode();
	        
	       
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
	
				final JSONArray resultArray = new JSONArray(response.toString());
				
				if(resultArray.length() > 0){
					return true;
				}
			}
	        
		}catch(Exception error){
			error.printStackTrace();
		}
		return false;
	}

}
