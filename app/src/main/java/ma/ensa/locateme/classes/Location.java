package ma.ensa.locateme.classes;



import java.util.Date;

public class Location {

    private int id;
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;


    private Date date;
    private String imei;


    public Location() {
    }

    public Location(int id, double latitude, double longitude, Date date, String imei) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.imei = imei;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}