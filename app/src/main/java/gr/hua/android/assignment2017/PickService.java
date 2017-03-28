package gr.hua.android.assignment2017;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vasilhs12 on 23/1/2017.
 */

// A BroadcastReceiver who chooses which service will start depending on the value of the variable "method" that are within the intent
public class PickService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String method = intent.getExtras().getString("method"); // Take the extra with name "method"
        String url = intent.getStringExtra("url");              // Take the extra with name "url"
        String uri = intent.getExtras().getString("uri");       // Take the extra with name "uri"

        final Intent serviceForTakeData = new Intent(context,TakeLocationsFromServer.class); // Creates an intent for the service which takes the data from the server
        final Intent serviceForStoreData = new Intent(context,StoreLocationToServer.class);  // Creates an intent for the service which stores the data to the server

//        If the variable method is "GET"
        if ( method.equals("GET") ) {
//            Send to the service the url , the uri and the method
            serviceForTakeData.putExtra("url",url);
            serviceForTakeData.putExtra("uri",uri);
            context.startService(serviceForTakeData);
        }
//        If the variable method is "POST"
        else if ( method.equals("POST") ) {
//            Send to the service the url, the uri, the method, and the locationObject cast as string
            serviceForStoreData.putExtra("url",url);
            serviceForStoreData.putExtra("uri",uri);
            serviceForStoreData.putExtra("locationObject", intent.getStringExtra("locationObject"));
            context.startService(serviceForStoreData);

        }
//        If the method is not GET or POST
        else {
            Log.e(context.getPackageName(),"No method!");
        }
    }
}
