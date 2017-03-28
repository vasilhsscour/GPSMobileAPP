package gr.hua.android.assignment2017;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by vasilhs12 on 17/1/2017.
 */

public class TakeLocationsFromServer extends IntentService {

    public TakeLocationsFromServer() {
        super(TakeLocationsFromServer.class.getName());
    }

//    Defines the variables that will need
    private ContentResolver resolver;               // Creates a resolver to communicate with the ContentProvider
    private ArrayList<LocationObject> locationList; // Creates a list with LocationObjects
    private String TAG;                             // Creates a string with class name
    private Uri uri;                                // Creates a uri to connect with the database


//    Initializes the variables are as defined above
    @Override
    public void onCreate() {
        super.onCreate();

        TAG = TakeLocationsFromServer.class.getSimpleName(); // Set the class name to the variable
        locationList = new ArrayList<>();                    // Initialize the list
        resolver = this.getContentResolver();                // Get the resolver

        Log.d(TAG,"Service onCreate");
    }

//    Connected to the server and take the data
    @Override
    protected void onHandleIntent(Intent intent) {
        String  url = intent.getExtras().getString("url");       // Take the intent's StringExtra with name "url"
        String  tmpUri = intent.getExtras().getString("uri");    // Take the intent's StringExtra with name "url"
        uri = Uri.parse(tmpUri);                                  // Creates the uri
        String response = null;

//        If the url is not null
        if ( url != null ) {
            ConnectionThread connectionThread = new ConnectionThread(); // Creates a ConnectionThread object
            String method = "GET";
            String params [] = {url,method,null}; // Creates a string[] with the parameters which pass to the thread
            try {
                response = connectionThread.execute(params).get(); // Connect with the Thread and when the execute ends return the response message
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
//            If the response isn't null
            if (response != null) {
                Log.e(TAG, "Response from server: " + response);
                try {
//                    Getting JSON Array node
                    JSONArray locations = new JSONArray(response);

//                    For all objects
                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject jsonObj = locations.getJSONObject(i);

//                        Creates a LocationObject
                        LocationObject locationObject = new LocationObject();

//                        Initial all the LocationObject's variables
                        locationObject.setId(jsonObj.getInt("id"));
                        locationObject.setUserid(jsonObj.getString("userid"));
                        locationObject.setLongitude(jsonObj.getDouble("longitude"));
                        locationObject.setLatitude(jsonObj.getDouble("latitude"));
                        locationObject.setDt(jsonObj.getString("dt"));

//                        Store the location Object in a list
                        locationList.add(locationObject);
                    }

//                   Take how many entries have the database
                    int dataBaseSize = takeLastId();

//                    If the database have the same size with the server's database
                    if (locationList.size() == dataBaseSize) {
//                       Displays to the monitor that the database is update
                        Log.e(TAG,"The database is updated!");
                    }
//                    If the database size is -1
                    else if (dataBaseSize == -1 ) {
//                       Displays to the monitor an error message
                        Log.e(TAG,"Error to take last id!");
                    }
//                     if the database size is 0
                    else if (dataBaseSize == 0){
//                        Store all the data to the database
                        insertAllDataToDataBase();
                    }
                    else {
//                        Store the extra data to the server
                        insertDataToDataBase(dataBaseSize);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                //          If the response is null
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
            }
        }
//        If url is null
        else {
            Log.e(TAG,"The url is null");
        }
    }

//    When the Service Destroy
    @Override
    public void onDestroy()
    {
        Log.d(TAG,"Service onDestroy");

    }

//    Store the new data from server to dataBase
    private void insertDataToDataBase(int databaseSize) {
        Log.d(TAG,"Waiting...");
        ContentValues locationValues = new ContentValues();

        for (int i=databaseSize; i<locationList.size(); i++) {

//            Creates a LocationObject
            LocationObject tmpLocation = locationList.get(i);

//            put the data in a ContentValue Object
            locationValues.put("USERID",tmpLocation.getUserid());
            locationValues.put("LONGITUDE",tmpLocation.getLongitude());
            locationValues.put("LATITUDE",tmpLocation.getLatitude());
            locationValues.put("DT",tmpLocation.getDt());

//            Connect with the Content Provider and insert the data to the database
            resolver.insert(uri, locationValues);
        }
//        Displays to the user how much data stored to the database
        Log.e( TAG ,(locationList.size()-databaseSize)+" location(s) stored in the database!" ) ;
    }

//    Store all the data to the database
    private void insertAllDataToDataBase() {
        Log.d(TAG,"Waiting...");
        ContentValues locationValues = new ContentValues();
        resolver.delete(uri,null,null); // Delete all the data from the database

//        for each object
        for (int i=0; i<locationList.size(); i++) {
//            Creates a LocationObject
            LocationObject tmpLocation = locationList.get(i);

//            put the data in a ContentValue Object
            locationValues.put("USERID",tmpLocation.getUserid());
            locationValues.put("LONGITUDE",tmpLocation.getLongitude());
            locationValues.put("LATITUDE",tmpLocation.getLatitude());
            locationValues.put("DT",tmpLocation.getDt());

//            Connect with the Content Provider and insert the data to the database
            resolver.insert(uri, locationValues);
        }
//        Displays to the user how much data stored to the database
        Log.e( TAG ,locationList.size()+" locations stored in the database!" ) ;
    }

//    Finds the last entry in the database
    private int takeLastId() {
//        Connect with the ContentResolver and take all the data from the database
        Cursor cursor = this.getContentResolver().query(uri,null,null,null,null);

//        If the cursor is null
//        Or the query returns null
        if (cursor != null) {
//            Mon=ve the cursor to the last entry
            if (cursor.moveToLast()) {
                int size = cursor.getInt(0); // Take the id
                cursor.close();
//                Return the last id
                return size;
            }
//            If the cursor can't move to last
            else{
                Log.e(TAG, "Cursor can not move to last!" );
                return 0;
            }
        }
//        If the cursor is null
        else {
            Log.e(TAG,"Cursor is null");
        }
        return -1;
    }



}
