package gr.hua.android.assignment2017;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

//    Initial values
    private LocationObject locationObject;                                           // A LocationObject
    private Intent broadcast;                                                        // A intent for the broadcast Receiver
    private final String USERID = "it21361";                                         // My id
    private final String TAG = this.getClass().getSimpleName();                      // The class name for Log
    private int id;                                                                  // The last id for the location
    private final String uri = "content://gr.hua.android.locationprovider/location"; // The uri to connect with the database
    private final String url = "http://62.217.127.19:8000/location";                 // The url to connect with the server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Creates a location Object
        locationObject = new LocationObject();
//        Creates an intent
        broadcast = new Intent();
//        Start the method startGetService
        startGetService();
//        Init the LocationObject variable userid
        locationObject.setUserid(USERID);

//        Creates a Location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Takes the current Location every 30 minutes
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*30, 10, new LocationListener() {
//            When the location changed
            @Override
            public void onLocationChanged(Location location) {
//                Format the double to have only one number after the comma
                DecimalFormat formatDouble = new DecimalFormat("###.#");

//                Format the date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
//                Take the date with a format
                String currentDateAndTime = sdf.format(new Date());

//                Set the dt, longitude, latitude and id
                locationObject.setDt(currentDateAndTime);
                locationObject.setLongitude( Double.parseDouble( formatDouble.format( location.getLongitude() ) ) );
                locationObject.setLatitude(Double.parseDouble( formatDouble.format( location.getLatitude() ) ) );
                id ++;
                locationObject.setId(id);

//                Send a intent in a broadcast Receiver
                broadcast.setAction("gr.hua.android.assignment2017.broadcast");
//                Put extras in the intent
                broadcast.putExtra("method","POST");
                broadcast.putExtra("url",url);
                broadcast.putExtra("uri",uri);
                broadcast.putExtra("locationObject",locationObject.toString());
                sendBroadcast(broadcast);
            }
//           When the status changed
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(TAG,"your status has changed to " + status);
                Log.e(TAG,"yours provider name is " + provider);
            }

//            When the provider Enabled
            @Override
            public void onProviderEnabled(String provider) {
                Log.e(TAG,"Your provider " + provider + " is enable");
            }

//            When the provider Disabled
            @Override
            public void onProviderDisabled(String provider) {
                Log.e(TAG,"Your provider " + provider + " is disable");
            }
        });

//        Crates a button onClickListener
        findViewById(R.id.takeData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = getLastId();
//                Send a intent in a broadcast Receiver
                broadcast.setAction("gr.hua.android.assignment2017.broadcast");
//                Put extras to intent
                broadcast.putExtra("uri",uri);
                broadcast.putExtra("url",url);
                broadcast.putExtra("method","GET");
                sendBroadcast(broadcast);
            }
        });
    }
//        My methods

//    Finds the last entry in the database
    public int getLastId() {
        Cursor cursor = this.getContentResolver().query(Uri.parse(uri),null,null,null,null);

        if (cursor != null) {
//            Return the last id
            if (cursor.moveToLast()) {
                int id = cursor.getInt(0);
                cursor.close();
                return id;
            }
            else{
                Log.e(TAG, "Cursor can not move to last!" );
                return  0;
            }
        }
        else {
            Log.e(TAG,"Cursor is null");
        }
        return -1;
    }

    public void startGetService () {
        Timer timer = new Timer ();

        TimerTask minuteTask = new TimerTask () {
            @Override
            public void run () {
                id = getLastId();
//                Send a intent in a broadcast Receiver
                broadcast.setAction("gr.hua.android.assignment2017.broadcast");
//                Put extras to intent
                broadcast.putExtra("method","GET");
                broadcast.putExtra("uri",uri);
                broadcast.putExtra("url",url);
                sendBroadcast(broadcast);
            }
        };
        // schedule the task to run starting now and then every ten minute...
        timer.schedule (minuteTask, 0l, 1000*60*10);   // 1000*10*60 every 10 minute
    }

}
