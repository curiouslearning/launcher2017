package permission_manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by IMFCORP\alok.acharya on 19/12/16.
 */

public class PermissionHandler {


    private Context _context;

    public PermissionHandler(Activity activity){
        activity = (Activity) _context;
    }
    private static String[] requestingPermission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.GET_ACCOUNTS};


    public static void requestForSpecificPermission(Activity _context) {
        ActivityCompat.requestPermissions(_context,requestingPermission, 101);
    }



    public static boolean checkIfAlreadyhavePermission(Context _context) {
        int READ_CONTACTS = ContextCompat.checkSelfPermission(_context, Manifest.permission.READ_CONTACTS);
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(_context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(_context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int LOCATION_ACCESS_Fine = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION);
        int LOCATION_ACCESS_Coarse = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int WIFI_state = ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_WIFI_STATE);
        int GET_ACCOUNTS = ContextCompat.checkSelfPermission(_context, Manifest.permission.GET_ACCOUNTS);

        if (GET_ACCOUNTS == PackageManager.PERMISSION_GRANTED
                && READ_CONTACTS == PackageManager.PERMISSION_GRANTED
                && READ_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED
                && WRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED
                && LOCATION_ACCESS_Fine == PackageManager.PERMISSION_GRANTED
                && LOCATION_ACCESS_Coarse == PackageManager.PERMISSION_GRANTED
                && WIFI_state == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
