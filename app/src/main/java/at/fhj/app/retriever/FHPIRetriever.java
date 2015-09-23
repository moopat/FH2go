package at.fhj.app.retriever;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import at.fhj.app.util.Configuration;

public class FHPIRetriever {

    public static final String UTF_8 = "UTF-8";
    private String init_url;
    private Map<String, String> parameters;

    /**
     * Constructor without login parameters
     * <p/>
     * With our free standard membership you won't be able to take advantage of our Premium features.
     * The key is added automagically.
     */
    public FHPIRetriever() {
        parameters = new HashMap<>();

        // Add key
        parameters.put("k", Configuration.API_KEY);
    }

    /**
     * Prepare the request without data.
     * <p/>
     * Internally the URL is set up and the dataset
     * is prepared. The key is included automatically.
     *
     * @param service Name of the service you want to call.
     */
    public void prepareRequest(String service) {
        // Prepare URL
        StringBuilder sb = new StringBuilder();
        sb.append(Configuration.API_ROOT);
        sb.append(service);
        this.init_url = sb.toString();
    }

    /**
     * Prepare the request using data.
     * <p/>
     * Internally the URL is set up and the dataset
     * is prepared. The key is included automatically.
     *
     * @param service Name of the service you want to call.
     * @param keys    The parameter names you want to use.
     * @param values  One value for each parameter.
     */
    public void prepareRequest(String service, String[] keys, String[] values) {
        // Prepare URL
        this.init_url = Configuration.API_ROOT + service;

        // Add values
        for (int i = 0; i < keys.length; i++) {
            parameters.put(keys[i], values[i]);
        }

    }

    /**
     * Retrieve data from server.
     * <p/>
     * Do this after you have prepared the request!
     *
     * @return String Server response
     */
    public String retrieve() {

        try {
            URL connectionUrl = new URL(init_url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) connectionUrl.openConnection();

            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, UTF_8));
            writer.write(getQueryString(parameters));
            writer.flush();
            outStream.close();

            InputStream inStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
                result.append('\r');
            }
            reader.close();
            //Log.i(getClass().getName(), "Request to " + init_url + " returned with " + result.toString());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private String getQueryString(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String key : parameters.keySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(parameters.get(key), UTF_8));

        }

        return result.toString();
    }

}