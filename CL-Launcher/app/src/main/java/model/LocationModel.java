package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IMFCORP\alok.acharya on 27/12/16.
 */

public class LocationModel {


    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("value")
    @Expose
    private Value value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


    public class Value {

        @SerializedName("long")
        @Expose
        private double _longitude;
        @SerializedName("lat")
        @Expose
        private double lat;
        @SerializedName("tabletID")
        @Expose
        private String tabletID;
        @SerializedName("timestamp")
        @Expose
        private long timestamp;

        public double get_longitude() {
            return _longitude;
        }

        public void set_longitude(double _longitude) {
            this._longitude = _longitude;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public String getTabletID() {
            return tabletID;
        }

        public void setTabletID(String tabletID) {
            this.tabletID = tabletID;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

    }

   /* private String _id;
    private String latitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }

    public String getdoubleitude() {
        return doubleitude;
    }

    public void setdoubleitude(String doubleitude) {
        this.doubleitude = doubleitude;
    }

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    private String doubleitude;
    private String sync_time;
    private String sync_status;*/



}
