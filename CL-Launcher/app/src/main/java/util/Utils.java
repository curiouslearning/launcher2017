package util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import model.AppInfoModel;

/**
 * Created by IMFCORP\alok.acharya on 19/12/16.
 */

public class Utils {

    private Context _Context;

    /**
     *
     * @param ctx
     * @param packageName
     * @return
     */

    public static String getAppName(Context ctx,String packageName){
        final PackageManager pm = ctx.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return  applicationName;
    }


    /**
     * Method to find address for current location.
     * @param context
     * @param location
     * @return
     */
    public static String getAddressFromGeoLocation(Context context,Location location){
        if (location == null) {
            LogUtil.createLog("Utill","location should not be null");
            return "";
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            LogUtil.createLog("Util","location ioException");
        } catch (IllegalArgumentException illegalArgumentException) {
            LogUtil.createLog("Util","location illegalArgumentException");
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            LogUtil.createLog("Util","no address found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            return TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }

        return "";
    }


    /**
     * Method to get relevant dd/mm/yyyy
     * @param time
     * @return
     */
    public static String getTimeFormat(long time){
        DateFormat mDateFormat = new SimpleDateFormat();
        return   mDateFormat.format(new Date(time));
    }


    public static void showToast(Context ctx,String msg){
        Toast toast = Toast.makeText(ctx, msg,Toast.LENGTH_SHORT);
       // toast.setGravity(Gravity.TOP,0,150);
        toast.show();
    }



    public static void openBluetooth(Context ctx){
        File file = new File(Constants.APK_PATH + "/app-debug.apk");
        if(file.exists()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.setPackage("com.android.bluetooth");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            ctx.startActivity(Intent.createChooser(sharingIntent, "Share file"));
        }else{
            showToast(ctx,"File doesn't exist.");
        }
    }


    public static void createCLAPPDirectory(){
        File folder = new File(Constants.DATABASE_FILE_PATH);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }

       /* File folder = new File(Constants.DATABASE_FILE_PATH);
        boolean success = true;
        if(folder.exists()){
            folder.delete();
        }
        folder.mkdirs();*/
    }


    public static String getDeviceId(Context ctx){

        return Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getAndroidVersion(){
        String versionRelease = Build.VERSION.RELEASE;
       // return ""+android.os.Build.VERSION.SDK_INT;
        return versionRelease;
    }

    public static String getAppVersion(Context context){
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }




    public static void showSnackBar(String msg, View anchor){
        Snackbar.make(anchor,msg,Snackbar.LENGTH_LONG).show();
    }



    public static void installAPK(Context context,String apkFileName){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Constants.APK_PATH+"/"+apkFileName+".apk")), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }



    /**
     * Converting dp to pixel
     */
    public static int dpToPx(Context ctx,int dp) {
        Resources r = ctx.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static String getCLVersion(Context context) throws PackageManager.NameNotFoundException {
        return  context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    }


    public static int getUTCOffset(){
        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        int offsetFromUtc = tz.getOffset(now.getTime()) / 1000;
        return offsetFromUtc;
    }

    public static void checkStatusForRootedDeviceAndPocessUninstall(AppInfoModel model) {
    }

    public static void checkStatusForRootedDeviceAndPocessInstall(AppInfoModel model) {
    }
}
