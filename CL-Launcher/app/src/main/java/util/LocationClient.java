package util;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by IMFCORP\alok.acharya on 12/1/17.
 */

public class LocationClient {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;


    private static LocationClient instance;
    private LocationClient(){}

    public static LocationClient getInstance(){
        if(instance==null){
            return new LocationClient();
        }
        return instance;
    }
}
