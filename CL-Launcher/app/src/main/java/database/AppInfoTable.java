package database;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.AppInfoModel;
import util.Constants;
import util.LogUtil;

import static database.DBAdapter.DATABASE_NAME;
import static database.DBAdapter.DATABASE_VERSION;


/**
 * Created by IMFCORP\alok.acharya on 28/2/17.
 */

public class AppInfoTable {

    public static final String ROW_ID = "_id";
    public static final String APP_ID = "app_id";
    public static final String APP_TITLE = "app_title";
    public static final String APP_PACKAGE_NAME = "app_package_name";
    public static final String APP_APK_DOWNLOAD_PATH = "app_downloadPath";
    public static final String APP_CONTENT_TYPE = "app_contentType";
    public static final String APP_TYPE = "app_type";
    public static final String APP_IS_VISIBLE = "isVisible";
    public static final String APP_VERSION = "appVersion";
    public static final String APP_IS_DOWNLOADED = "isDownLoaded";
    public static final String APP_IS_INSTALLED = "isInstalled";
    public static final String APP_APK_LOCAL_PATH = "appLocalPath";
    public static final String SYNC_STATUS = "sync_status";
    public static final String SYNC_TIME = "sync_time";

    private final String  TAG = "AppInfoTable";



    private static final String APP_INFO_TABLE = "AppInfo";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            //super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
            super(context, Constants.DATABASE_FILE_PATH
                    + File.separator + "CL_DB"
                    + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx
     *            the Context within which to work
     */
    public AppInfoTable(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public AppInfoTable open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close return type: void
     */
    public void close() {
        this.mDbHelper.close();
    }

    /**
     * Create a new AppUsageModel. If the AppUsageModel is successfully created return the new
     * rowId for that AppUsageModel, otherwise return a -1 to indicate failure.

     * @return rowId or -1 if failed
     */


    public long insertAppInfo(AppInfoModel info){
        LogUtil.createLog(TAG,"inserting data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_ID, info.getId());
        initialValues.put(APP_TITLE, info.getTitle());
        initialValues.put(APP_PACKAGE_NAME, info.getAppFilePath());
        initialValues.put(APP_APK_DOWNLOAD_PATH, info.getApkName());
        initialValues.put(APP_CONTENT_TYPE, info.getContentType());
        initialValues.put(APP_TYPE, info.getType());
        initialValues.put(APP_IS_VISIBLE, info.getVisible());
        initialValues.put(APP_VERSION, info.getVersion());
        initialValues.put(APP_IS_DOWNLOADED, ""+info.isDownloaded());
        initialValues.put(APP_IS_INSTALLED, ""+info.isInstalled());
        initialValues.put(APP_APK_LOCAL_PATH, info.getLocalPath());
        initialValues.put(SYNC_STATUS,"");
        initialValues.put(SYNC_TIME,"");
        return this.mDb.insert(APP_INFO_TABLE, null, initialValues);
    }


    public boolean updateAppInstallationInfo(int id,boolean installStatus){

        System.out.println("updating data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_IS_INSTALLED, ""+installStatus);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }


    public boolean updateAppDownLoadInfo(int id,boolean downloadStatus){

        System.out.println("updating data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_IS_DOWNLOADED, ""+downloadStatus);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }



    public ArrayList<AppInfoModel> getAppInfo(List<ResolveInfo> apps, PackageManager manager) {

        String selectQuery = "SELECT * FROM " + APP_INFO_TABLE;
        ArrayList<AppInfoModel> appInfoList = new ArrayList<AppInfoModel>();
        AppInfoModel info ;
        Cursor cur = this.mDb.rawQuery(selectQuery, null);
        if(cur.moveToFirst()){
            do{
                info = new AppInfoModel();
                info.setId(cur.getInt(1));
                info.setTitle(cur.getString(2));
                info.setAppFilePath(cur.getString(3));
                info.setApkName(cur.getString(4));
                info.setContentType(cur.getString(5));
                info.setType(cur.getString(6));
                info.setVisible(cur.getInt(7));
                info.setVersion(cur.getString(8));
                info.setDownloaded(Boolean.parseBoolean(cur.getString(9)));
                info.setInstalled(Boolean.parseBoolean(cur.getString(10)));
                info.setLocalPath(cur.getString(11));
                filterApps(apps,info,manager);
                appInfoList.add(info);
            }while(cur.moveToNext());
        }
        return appInfoList;


    }


    private void filterApps(List<ResolveInfo> apps,AppInfoModel model, PackageManager manager) {

        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            if (model.getAppFilePath().
                    equals(info.activityInfo.applicationInfo.packageName)) {
                model.setTitle(info.loadLabel(manager).toString());
                model.setIntent(model.setActivity(new ComponentName(
                                info.activityInfo.applicationInfo.packageName,
                                info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                model.setIcon(info.activityInfo.loadIcon(manager));
                model.setInstalled(true);


            }
        }
    }


    //Delete all info

    public void deleteAllAppInfoDetails(){

        String deletequery ="DELETE from "+ APP_INFO_TABLE;
        this.mDb.execSQL(deletequery);
    }


    //Delete single info

    public void deleteAllAppInfoDetails(String id){

        String deletequery ="DELETE from "+ APP_INFO_TABLE +" WHERE "+ROW_ID+" = '"+id+"' AND "+SYNC_STATUS+" = '"+ Constants.STATUS_SYNC+"'";
        this.mDb.execSQL(deletequery);
    }




    // Getting  Count
    public int getCount() {
        String countQuery  = "SELECT * FROM " + APP_INFO_TABLE;
        System.out.println("countQuery......"+countQuery);
        Cursor cursor = this.mDb.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        // return count
        return c;

    }
}
