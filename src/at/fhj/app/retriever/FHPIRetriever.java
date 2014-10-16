package at.fhj.app.retriever;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import at.fhj.app.util.Configuration;

public class FHPIRetriever {

	private String init_url;
	private List<NameValuePair> values;
		
	/**
	 * Constructor without login parameters
	 * 
	 * With our free standard membership you won't be able to take advantage of our Premium features.
	 * The key is added automagically.
	 */
	public FHPIRetriever(){
		values = new ArrayList<NameValuePair>();
		
		// Add key
		values.add(new BasicNameValuePair("k", Configuration.API_KEY));
	}
	
	/**
	 * Prepare the request without data.
	 * 
	 * Internally the URL is set up and the dataset
	 * is prepared. The key is included automatically.
	 * 
	 * @param service Name of the service you want to call.
	 */
	public void prepareRequest(String service){
		// Prepare URL
		StringBuilder sb = new StringBuilder();
		sb.append(Configuration.API_ROOT);
		sb.append(service);
		this.init_url = sb.toString();	
	}
	
	/**
	 * Prepare the request using data.
	 * 
	 * Internally the URL is set up and the dataset
	 * is prepared. The key is included automatically.
	 * 
	 * @param service Name of the service you want to call.
	 * @param arguments The parameter names you want to use.
	 * @param values One value for each parameter.
	 */
	public void prepareRequest(String service, String[] arguments, String[] values){
		// Prepare URL
		StringBuilder sb = new StringBuilder();
		sb.append(Configuration.API_ROOT);
		sb.append(service);
		this.init_url = sb.toString();
		
		// Add values
		for(int i = 0; i < arguments.length; i++){
			this.values.add(new BasicNameValuePair(arguments[i], values[i]));
		}
		
	}
	
	/**
	 * Retrieve data from server.
	 * 
	 * Do this after you have prepared the request!
	 * 
	 * @return String Server response
	 */
	public String retrieve(){
		StringBuilder sb = new StringBuilder();
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(this.init_url);
	    
	    int tries = 0;
	    boolean success = false;
	    
	    while(tries < Configuration.NETWORK_TRIES && !success){
		    try {
		    	tries++;
		        httppost.setEntity(new UrlEncodedFormEntity(this.values));
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        BufferedReader inreader = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String line;
	            while ((line = inreader.readLine()) != null) {
	                    sb.append(line);
	            }
	            inreader.close();
	            success = true;
	            return sb.toString();
		    } catch (Exception e) {
		    	e.printStackTrace();
		        return null;
		    }
	    }
	    
	    return null;
	}

}