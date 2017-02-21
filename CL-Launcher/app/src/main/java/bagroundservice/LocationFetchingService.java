package bagroundservice;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import database.BackgroundDataCollectionDB;
import database.DBAdapter;
import model.BackgroundDataModel;
import model.LocationModel;
import util.Constants;
import util.LogUtil;
import util.Utils;

/**
 * Created by IMFCORP\alok.acharya on 22/12/16.
 */

public class LocationFetchingService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final String TAG = "fetch-location-intent-service";


    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    //  protected ResultReceiver mReceiver;
    ConnectionResult mConnectionResult;
    private DBAdapter mDbAdapter;
    BackgroundDataCollectionDB backgroundDataCollectionDB;
    // private LocationTable mLocationTable;
    private LocationModel locationModel;
    private String deviceId ="";
    private   Gson gson ;

    public LocationFetchingService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.createLog(TAG, "location service created");
        buildGoogleApiClient();
        createLocationRequest();


        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        backgroundDataCollectionDB = new BackgroundDataCollectionDB(this);
        backgroundDataCollectionDB.open();
        deviceId = Utils.getDeviceId(this);
        gson = new GsonBuilder().create();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.createLog(TAG,"called onStartCommand");
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            } else {
                LogUtil.createLog(TAG,"called mGoogleApiClient.connect");
                mGoogleApiClient.connect();
            }
        }
       /* if (intent != null && intent.getParcelableExtra(Constants.APP_LISTENER_KEY) != null) {
            mReceiver = intent.getParcelableExtra(Constants.APP_LISTENER_KEY);
        }*/
        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            LogUtil.createLog(TAG, "location onConnected");
            this.mCurrentLocation = mCurrentLocation;
            LogUtil.createLog(TAG, "Got location:: latitude :" + mCurrentLocation.getLatitude() + " Longitude ::" + mCurrentLocation.getLongitude());
            deliverResultToReceiver(Constants.LOCATION_RESULT_KEY,mCurrentLocation);
            startLocationUpdates();
        } else {
            LogUtil.createLog(TAG, "No location detected");

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        LogUtil.createLog(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtil.createLog(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        this.mConnectionResult = connectionResult;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        LogUtil.createLog(TAG, "location onLocationChanged");
        LogUtil.createLog(TAG, "Got location:: latitude :" + mCurrentLocation.getLatitude() + " Longitude ::" + mCurrentLocation.getLongitude());
        deliverResultToReceiver(Constants.LOCATION_RESULT_KEY,mCurrentLocation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.createLog(TAG, "location service destroyed");
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        if(mDbAdapter!=null&&backgroundDataCollectionDB!=null){
            backgroundDataCollectionDB.close();
            mDbAdapter.close();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LogUtil.createLog(TAG,"call Location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){

                }
            }
        });

    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if(status.isSuccess()){

                }
            }
        });
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode,Location mLocation) {
        // Bundle bundle = new Bundle();
        //  bundle.putParcelable(Constants.LOCATION_KEY, mCurrentLocation);
        // mReceiver.send(resultCode, bundle);
        LogUtil.createLog("launher", "got location in launcher : lat ::" + mLocation.getLatitude() + ":: longitude:" + mLocation.getLongitude()
                +" : time"+ Utils.getTimeFormat(mLocation.getTime()));
        _insertIntoDB(mLocation);

    }

    private void _insertIntoDB(final Location mLocation){

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mLocation!=null) {
                    locationModel = new LocationModel();
                    locationModel.setKey(Constants.KEY_GPS_LOC_TIME);
                    LocationModel.Value value = locationModel.new Value();
                    value.setLat(mLocation.getLatitude());
                    value.set_longitude(mLocation.getLongitude());
                    value.setTimestamp(mLocation.getTime());
                    value.setTabletID(deviceId);
                    locationModel.setValue(value);
                    String  jsonInfo = gson.toJson(locationModel);
                    LogUtil.createLog(TAG,"JSON INFO ::"+jsonInfo);
                    BackgroundDataModel model = new BackgroundDataModel();
                    model.setName(LocationFetchingService.this.getPackageName());
                    model.setTimeStamp(System.currentTimeMillis()+"");
                    model.setJsonData(jsonInfo);
                    if(backgroundDataCollectionDB!=null){
                        backgroundDataCollectionDB.insertInfo(model);
                    }
                }
            }
        }).start();
    }


    /*private void startConnectionTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtil.createLog(TAG,"timer connecting...");
                if(!isConnected) {
                    LogUtil.createLog(TAG,"timer connecting...isConnected :"+isConnected);
                    startLocationUpdates();
                }else{
                    stopConnectionTimer();
                }
            }
        },100,60000);

    }

    private void stopConnectionTimer(){
        if(timer!=null){
            timer.cancel();
            timer=null;
            LogUtil.createLog(TAG,"stop timer");
        }
    }*/
}
