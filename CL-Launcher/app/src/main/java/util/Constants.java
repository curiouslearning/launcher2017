package util;

import android.os.Environment;

import java.io.File;

/**
 * Created by IMFCORP\alok.acharya on 14/12/16.
 */

public class Constants {
    public static final String  DATABASE_FILE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()+"/CL-APP";

    public static final String  APK_PATH =
           DATABASE_FILE_PATH+ File.separator+"APK";

    public static final String  STATUS_SYNC = "0";
    public static final String  STATUS_NOT_SYNC = "1";

    public static final String  APP_LISTENER_KEY = "iAppListener";
    public static final String  LOCATION_KEY = "location";
    public static final int  LOCATION_RESULT_KEY = 129;
    public static final int  PREVENT_STATUS_BAR_PERMISSION = 130;
    public static final int  USAGE_STATE_PERMISSION = 131;

    public static final int  EXIT_LAUNCHER_REQUEST = 111;
    public static final int  OPEN_APP_REQUEST = 112;
    public static final int  OPEN_SETTING = 113;


    public static final String  PWD_DEFAULT = "0";
    public static final String  PWD_CL = "1";
    public static final String  PWD_ANDROID = "2";

    public static final String KEY_MEMORY_USAGE ="MEMORY_USAGE";
    public static final String KEY_GPS_LOC_TIME ="GPS_LOC_TIME";
    public static final String KEY_IN_APP ="IN_APP";
    public static final String KEY_IN_APP_SECTION ="IN_APP_SECTION";
    public static final String KEY_IN_APP_SCORE ="IN_APP_SCORE";
    public static final String KEY_IN_APP_RESPONSE ="IN_APP_RESPONSE";
    public static final String KEY_IN_APP_TOUCH ="IN_APP_TOUCH";


}
