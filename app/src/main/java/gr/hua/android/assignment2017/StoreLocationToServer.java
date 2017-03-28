package gr.hua.android.assignment2017;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.ExecutionException;

/**
 * Created by vasilhs12 on 13/1/2017.
 */

//the service does register the current location of the user every 30 seconds
public class StoreLocationToServer extends Service {

    //    Defines the variables that will need
    ContentResolver resolver;   // Creates a resolver to communicate with the ContentProvider
    String TAG ;                // Creates a string with class name
    Uri uri;                    // Creates a uri to connect with the database

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("","Service is in onBind");
        return null;
    }

//    Initializes the variables are as defined above
    @Override
    public void onCreate() {
        super.onCreate();

        TAG = StoreLocationToServer.class.getName();  // Set the class name to the variable
        resolver = this.getContentResolver();         // Get the resolver

        Log.d(TAG,"Service is in onCreate");

    }

//    Connected to the server (and send the data) via a thread and returns the response
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"Service is in onStartCommand");
        String url = intent.getStringExtra("url");                          // Takes the intent's StringExtra with the name "url"
        String tmpUri = intent.getStringExtra("uri");                       // Takes the intent's StringExtra with the name "uri"
        uri = Uri.parse(tmpUri);                                            // Create the uri
        String locationObject = intent.getStringExtra("locationObject");    // Take the intent's StringExtra with the name "locationObject"

//        If the locationObject and the url is not null
        if (locationObject != null || url != null) {
            ConnectionThread connectionThread = new ConnectionThread(); // Creates a ConnectionThread object
            String method = "POST";
            String params [] = {url,method,locationObject}; // Creates a string[] with the parameters which pass to the thread
            String response = null;
            try {
                response = connectionThread.execute(params).get(); // Connect with the Thread and when the execute ends return the response message
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

//            If the response isn't null
            if (response != null) {
//                Displays to the monitor the response message from the server
                Log.e(TAG,"The response from server" +response);
            }

        }
//        If locationObject or url is null
        else {
            Log.e(TAG,"An Object is null");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Service is in onDestroy");
    }
}