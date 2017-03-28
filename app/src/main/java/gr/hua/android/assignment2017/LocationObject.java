package gr.hua.android.assignment2017;

/**
 * Created by vasilhs12 on 17/1/2017.
 */

//An object for the data to be sent to the server
public class LocationObject{

//    variables
    int id;                // For the database id
    String userid;         // For users id
    double longitude;      // For current location
    double latitude;       // For current location
    String dt;             // For time stamp with a specific format

//    Constructor
    public LocationObject() {
        this.id = 0;
        this.userid = "";
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.dt = "";
    }

//    Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

//   Creates the locationObject into a string with json format
    @Override
    public String toString() {
        return "{"
                +"\"userid\":\"" + userid
                +"\",\"longitude\":" + longitude
                +",\"latitude\":" + latitude
                +",\"dt\":\"" + dt
                +"\"}";
    }
}
