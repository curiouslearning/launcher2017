package util;

import android.os.Environment;

import java.io.File;

/**
 * Created by IMFCORP\alok.acharya on 14/12/16.
 */

public class Constants {
    public static final String  DATABASE_FILE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/excelsoft.com.cl_launcher/CL-DB";

    public static final String  APK_PATH =
            DATABASE_FILE_PATH+ File.separator+"APK";

    public static final String  COPY_DB_FILE_PATH =
            DATABASE_FILE_PATH+ File.separator+"copied_db_file";

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

    public static final String KEY_MEMORY_USAGE ="‘SYSTEM_INFO’";
    public static final String KEY_GPS_LOC_TIME ="GPS_LOC_TIME";
    public static final String KEY_IN_APP ="IN_APP";
    public static final String KEY_IN_APP_SECTION ="IN_APP_SECTION";
    public static final String KEY_IN_APP_SCORE ="IN_APP_SCORE";
    public static final String KEY_IN_APP_RESPONSE ="IN_APP_RESPONSE";
    public static final String KEY_IN_APP_TOUCH ="IN_APP_TOUCH";


    //App Data parsing keys
    public static final String KEY_VERSION ="version";
    public static final String KEY_ID ="id";
    public static final String KEY_APK_NAME ="apkName";
    public static final String KEY_FILE="file";
    public static final String KEY_TITTLE ="title";
    public static final String KEY_CONTENT_TYPE ="content_type";
    public static final String KEY_TYPE ="type";
    public static final String KEY_VISIBLE ="visible";
    public static final String KEY_APPS ="apps";

    public static final int UPDATE_AVAILABLE = 1;
    public static final int UPDATE_NOT_AVAILABLE = 0;

    public static final int APP_VISIBLE = 1;
    public static final int APP_NOT_VISIBLE = 0;

    public static final String ACTION_IN_APP_RECORD_ONE = "org.curiouslearning.RECORD";
    public static final String ACTION_IN_APP_RECORD_TWO = "edu.mit.media.funf.RECORD";




    public static final int ACTION_NOT_DOWNLOAD_YET = 0;
    public static final int ACTION_DOWNLOAD_STARTED = 1;
    public static final int ACTION_DOWNLOAD_FAILED = 2;
    public static final int ACTION_DOWNLOAD_COMPLETED = 3;
    public static final int ACTION_DOWNLOAD_RUNNING = 4;


    public static final int ACTION_INSTALL_COMPLETED = 4;


    public static final String  KEY_BACKGROUND = "BACKGROUND_APP";
    public static final String  KEY_FOREGROUND = "FOREGROUND_APP";

    public static final int  KEY_BACKGROUND_APP = 2;
    public static final int  KEY_FOREGROUND_APP = 1;


    //QR Code deployments json keys


    public static final String  KEY_DEPLOYMENT_ID = "deployentId";
    public static final String  KEY_TABLET_LEBEl= "tablet_label";
    public static final String  KEY_GROUP_ID = "groupId";

    //ERROR CONSTANTS
    public static final String  KEY_INVALID_TOKEN = "Invalid token: access token has expired";


}
