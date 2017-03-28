package gr.hua.android.assignment2017;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vasilhs12 on 30/1/2017.
 */

//  An AsyncTask which take as parameter an url, a method ("POST" OR "GET")
//  and if the method is "POST" take as parameter a string in json form and send it to the server
//  Depended the method connected to url
//  Return the response from the server
public class ConnectionThread extends AsyncTask <String,Void,String>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

//        Variables
        String TAG = ConnectionThread.class.getName();  // For the class Name
        String reqUrl = params[0];                      // Take the first parameter which is the url
        String method = params[1];                      // Take the second parameter which is the method
        String locationObject = params[2];              // Take the third parameter which is the locationObject as a string with json format
        String response = null;                         // The response code which return to the user

        try {
            URL url = new URL(reqUrl); // Create a url
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); // Connect with the server

//            If the method is GET
            if (method == "GET") {
                httpURLConnection.setRequestMethod("GET"); // Make GET request

//                Take the return massage from server
                BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));

//                Modify the response message
                response = convertStreamToString(br);

//                Disconnect from the server
                httpURLConnection.disconnect();
            }

            else if (method == "POST") {
                httpURLConnection.setRequestMethod("POST"); // Make POST request
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

    //            Writes the data to the server
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                wr.writeBytes(locationObject.toString());
                wr.flush();
                wr.close();

//                Displays to the monitor the location which stored to the server
                Log.e(TAG, locationObject);

//                Check the response code
//                If is OK then continue otherwise throw Runtime Exception
                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
                }

//                Take the return massage from server
                BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));

//                Modify the response message
                response = convertStreamToString(br);

//                Disconnect from the server
                httpURLConnection.disconnect();
            }
//            If the method is not GET or POST
            else{
                new RuntimeException("INVALID METHOD : method must be POST or GET");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Return the response message
        return response;
    }

    @Override
    protected void onPostExecute(String response)
    {
        super.onPostExecute(response);
    }

//    Modify the response message
//    Takes as parameter a BufferReader which contains the response from the server
    private String convertStreamToString(BufferedReader reader) {
        String result = "";

        String line;
        try {
//            While the bufferReader have data
            while ((line = reader.readLine()) != null) {
                result += line +("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}



