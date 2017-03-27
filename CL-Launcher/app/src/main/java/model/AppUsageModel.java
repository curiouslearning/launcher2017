package model;

/**
 * Created by IMFCORP\alok.acharya on 19/12/16.
 */

public class AppUsageModel {

    private String  _id;
    private String  app_name;
    private String  app_package_name;
    private String  app_first_time_stamped;
    private String  app_last_time_stamped;
    private String  app_last_time_used;
    private String app_foreground_background_status;

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getApp_foreground_background_status() {
        return app_foreground_background_status;
    }

    public void setApp_foreground_background_status(String app_foreground_background_status) {
        this.app_foreground_background_status = app_foreground_background_status;
    }

    private long eventTime;



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getApp_last_time_stamped() {
        return app_last_time_stamped;
    }

    public void setApp_last_time_stamped(String app_last_time_stamped) {
        this.app_last_time_stamped = app_last_time_stamped;
    }

    public String getApp_first_time_stamped() {
        return app_first_time_stamped;
    }

    public void setApp_first_time_stamped(String app_first_time_stamped) {
        this.app_first_time_stamped = app_first_time_stamped;
    }

    public String getApp_last_time_used() {
        return app_last_time_used;
    }

    public void setApp_last_time_used(String app_last_time_used) {
        this.app_last_time_used = app_last_time_used;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_package_name() {
        return app_package_name;
    }

    public void setApp_package_name(String app_package_name) {
        this.app_package_name = app_package_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getTime_in_foreground() {
        return time_in_foreground;
    }

    public void setTime_in_foreground(String time_in_foreground) {
        this.time_in_foreground = time_in_foreground;
    }

    private String  time_in_foreground;
    private String  latitude;
    private String  longitude;
    private String  sync_status;
    private String  sync_time;



}
