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
import java.util.HashMap;
import java.util.List;

import model.AppInfoModel;
import util.Constants;
import util.LogUtil;

import static database.APPInfoDB.DATABASE_NAME;
import static database.APPInfoDB.DATABASE_VERSION;


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
    public static final String APP_VERSION = "installedVersion";
    public static final String APP_DOWNLOAD_STATUS = "downloadStatus";
    public static final String APP_INSTALATION_STATUS = "instalationStatus";
    public static final String APP_APK_LOCAL_PATH = "appLocalPath";
    public static final String SYNC_STATUS = "sync_status";
    public static final String SYNC_TIME = "sync_time";
    public static final String APP_UPDATE_VERSION = "updateVersion";
    public static final String UPDATE_AVAILABLE_STATUS = "update_available_status";
    public static final String IS_UPDATED = "is_updated";
    public static final String DOWNLOAD_ID = "downloadId";
    public static final String INSTALLATION_PROCESS_INITIATE_STATUS = "installationProcessInitiateStatus";
    public static final String MISC = "misc";

    private final String  TAG = "AppInfoTable";

    private static final String APP_INFO_TABLE = "AppInfo";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            //super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
            super(context, Constants.DATABASE_FILE_PATH
                    + File.separator + "CL_APP_INFO_DB"
                    + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LogUtil.createLog("onUpgrade DB AppInfoTable::","old Version : "+oldVersion+" New Version :"+newVersion);

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
        LogUtil.createLog(TAG,"inserting data into  data base..."+info.getApkDownloadPath());
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_ID, info.getAppId());
        initialValues.put(APP_TITLE, info.getAppTitle());
        initialValues.put(APP_PACKAGE_NAME, info.getAppPckageName());

        initialValues.put(APP_APK_DOWNLOAD_PATH, info.getApkDownloadPath());
        initialValues.put(APP_CONTENT_TYPE, info.getContentType());
        initialValues.put(APP_TYPE, info.getType());

        initialValues.put(APP_IS_VISIBLE, info.getVisible());
        initialValues.put(APP_VERSION, info.getAppVersion());

        initialValues.put(APP_DOWNLOAD_STATUS, ""+info.getDownloadStatus());
        initialValues.put(APP_INSTALATION_STATUS, ""+info.isInstalationStatus());

        initialValues.put(APP_APK_LOCAL_PATH, info.getLocalPath());
        initialValues.put(APP_UPDATE_VERSION, info.getUpdateVersion());

        initialValues.put(UPDATE_AVAILABLE_STATUS, ""+info.getIsUpdateVersionExist());
        initialValues.put(IS_UPDATED, ""+info.isUpdated());

        initialValues.put(MISC, "");
        initialValues.put(SYNC_STATUS,"");
        initialValues.put(SYNC_TIME,"");

        initialValues.put(INSTALLATION_PROCESS_INITIATE_STATUS, ""+info.isInstalationProcessInitiate());
        initialValues.put(DOWNLOAD_ID, ""+info.getDownloadId());

        return this.mDb.insert(APP_INFO_TABLE, null, initialValues);
    }


    public boolean updateAppInfo(AppInfoModel info){

        System.out.println("updating data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_ID, info.getAppId());
        initialValues.put(APP_TITLE, info.getAppTitle());
        initialValues.put(APP_PACKAGE_NAME, info.getAppPckageName());

        initialValues.put(APP_APK_DOWNLOAD_PATH, info.getApkDownloadPath());
        initialValues.put(APP_CONTENT_TYPE, info.getContentType());
        initialValues.put(APP_TYPE, info.getType());

        initialValues.put(APP_IS_VISIBLE, info.getVisible());
        initialValues.put(APP_VERSION, info.getAppVersion());

        initialValues.put(APP_DOWNLOAD_STATUS, ""+info.getDownloadStatus());
        initialValues.put(APP_INSTALATION_STATUS, ""+info.isInstalationStatus());

        initialValues.put(APP_APK_LOCAL_PATH, info.getLocalPath());
        initialValues.put(APP_UPDATE_VERSION, info.getUpdateVersion());

        initialValues.put(UPDATE_AVAILABLE_STATUS, ""+info.getIsUpdateVersionExist());
        initialValues.put(IS_UPDATED, ""+info.isUpdated());

        initialValues.put(MISC, "");
        initialValues.put(SYNC_STATUS,"");
        initialValues.put(SYNC_TIME,"");

        initialValues.put(INSTALLATION_PROCESS_INITIATE_STATUS, ""+info.isInstalationProcessInitiate());
        initialValues.put(DOWNLOAD_ID, ""+info.getDownloadId());


        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + info.getAppId(), null) >0;
    }

    public boolean updateAppInstallationInfo(int id,boolean installStatus){

        System.out.println("updating updateAppInstallationInfo into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_INSTALATION_STATUS, ""+installStatus);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }

    public boolean updateAppUpdateAvailableInfo(String packageId,int status){

        System.out.println("updating updateAppUpdateAvailableInfo into  data base..."+status);
        ContentValues initialValues = new ContentValues();
        initialValues.put(UPDATE_AVAILABLE_STATUS, status);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_PACKAGE_NAME + " = " + "?", new String[]{packageId}) >0;
    }

    public boolean updateAppVisibilityInfo(String packageId,int status){

        System.out.println("updating updateAppVisibilityInfo into  data base..."+status);
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_IS_VISIBLE, status);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_PACKAGE_NAME + " = " + "?", new String[]{packageId}) >0;
    }


    public boolean updateAppInstallationProcessInfo(int id,boolean installStatusProcess){

        System.out.println("updating updateAppInstallationProcessInfo into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(INSTALLATION_PROCESS_INITIATE_STATUS, ""+installStatusProcess);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }


    public boolean updateAppUpdateInfo(int id,boolean isUpdated){

        System.out.println("updating updateAppInstallationProcessInfo into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(IS_UPDATED, ""+isUpdated);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }


    public boolean updateAppDownLoadInfo(int id,int downloadStatus){

        System.out.println("updating downloadStatus data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(APP_DOWNLOAD_STATUS, ""+downloadStatus);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }


    public boolean updateAppDownLoadID(int id,int downLoadId){

        System.out.println("updating downLoadId data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(DOWNLOAD_ID, ""+downLoadId);
        return this.mDb.update(APP_INFO_TABLE, initialValues, APP_ID + " = " + id, null) >0;
    }



    public ArrayList<AppInfoModel> getAppInfo(List<ResolveInfo> apps, PackageManager manager) {

        String selectQuery = "SELECT * FROM " + APP_INFO_TABLE+" WHERE "+APP_IS_VISIBLE+" = '"+Constants.APP_VISIBLE+"'";
        ArrayList<AppInfoModel> appInfoList = new ArrayList<AppInfoModel>();
        AppInfoModel info ;
        Cursor cur = this.mDb.rawQuery(selectQuery, null);
        if(cur.moveToFirst()){
            do{
                info = new AppInfoModel();
                info.setAppId(cur.getInt(1));
                info.setAppTitle(cur.getString(2));
                info.setAppPckageName(cur.getString(3));
                info.setApkDownloadPath(cur.getString(4));
                info.setContentType(cur.getString(5));
                info.setType(cur.getString(6));
                info.setVisible(cur.getInt(7));
                info.setAppVersion(cur.getString(8));
                int status = 0;
                if(cur.getString(9).equalsIgnoreCase("true"))
                    status = Constants.ACTION_DOWNLOAD_COMPLETED;
                else  if(cur.getString(9).equalsIgnoreCase("false"))
                    status = Constants.ACTION_NOT_DOWNLOAD_YET;
                else
                status = Integer.parseInt(cur.getString(9));
                info.setDownloadStatus(status);
                if(cur.getString(10)!=null)
                info.setInstalationStatus(Boolean.parseBoolean(cur.getString(10)));
                info.setLocalPath(cur.getString(11));
                if(cur.getString(13)!=null)
                info.setIsUpdateVersionExist(Integer.parseInt(cur.getString(13)));
                if(cur.getString(14)!=null)
                info.setUpdated(Boolean.parseBoolean(cur.getString(14)));
                if(cur.getString(18)!=null)
                info.setInstalationProcessInitiate(Boolean.parseBoolean(cur.getString(18)));
                info.setDownloadId(cur.getInt(19));
                filterApps(apps,info,manager);
                appInfoList.add(info);
            }while(cur.moveToNext());
        }
        return appInfoList;


    }


    public HashMap<String,AppInfoModel> getAppInfoMap() {

        String selectQuery = "SELECT * FROM " + APP_INFO_TABLE+" WHERE "+APP_IS_VISIBLE+" = '"+Constants.APP_VISIBLE+"'";
        HashMap<String,AppInfoModel> map = new HashMap<String,AppInfoModel>();
        AppInfoModel info ;
        Cursor cur = this.mDb.rawQuery(selectQuery, null);
        if(cur.moveToFirst()){
            do{
                info = new AppInfoModel();
                info.setAppId(cur.getInt(1));
                info.setAppTitle(cur.getString(2));
                info.setAppPckageName(cur.getString(3));
                info.setApkDownloadPath(cur.getString(4));
                info.setContentType(cur.getString(5));
                info.setType(cur.getString(6));
                info.setVisible(cur.getInt(7));
                info.setAppVersion(cur.getString(8));
                int status = 0;
                if(cur.getString(9).equalsIgnoreCase("true"))
                    status = Constants.ACTION_DOWNLOAD_COMPLETED;
                else  if(cur.getString(9).equalsIgnoreCase("false"))
                    status = Constants.ACTION_NOT_DOWNLOAD_YET;
                else
                    status = Integer.parseInt(cur.getString(9));
                info.setDownloadStatus(status);
                if(cur.getString(10)!=null)
                    info.setInstalationStatus(Boolean.parseBoolean(cur.getString(10)));
                info.setLocalPath(cur.getString(11));
                if(cur.getString(13)!=null)
                    info.setIsUpdateVersionExist(Integer.parseInt(cur.getString(13)));
                if(cur.getString(14)!=null)
                    info.setUpdated(Boolean.parseBoolean(cur.getString(14)));
                if(cur.getString(18)!=null)
                    info.setInstalationProcessInitiate(Boolean.parseBoolean(cur.getString(18)));
                info.setDownloadId(cur.getInt(19));
                map.put(info.getAppPckageName(),info);
            }while(cur.moveToNext());
        }
        return map;


    }


    private void filterApps(List<ResolveInfo> apps,AppInfoModel model, PackageManager manager) {

        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            if (model.getAppPckageName().
                    equals(info.activityInfo.applicationInfo.packageName)) {
                model.setAppTitle(info.loadLabel(manager).toString());
                model.setIntent(model.setActivity(new ComponentName(
                                info.activityInfo.applicationInfo.packageName,
                                info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                model.setIcon(info.activityInfo.loadIcon(manager));
                model.setInstalationStatus(true);
                model.setDownloadStatus(Constants.ACTION_DOWNLOAD_COMPLETED);
                updateAppInstallationInfo(model.getAppId(),true);
                break;
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


    // Getting  Count
    public boolean isExist(int appId,String pckgId) {
        String countQuery  = "SELECT * FROM " + APP_INFO_TABLE+" WHERE "+APP_ID+" = '"+appId+"' AND "+APP_PACKAGE_NAME+" = '"+pckgId+"'";
        System.out.println("countQuery......"+countQuery);
        Cursor cursor = this.mDb.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        // return count
        if(c==0){
            return false;
        }else{
            return true;
        }

    }


    // Getting  Count
    public String getVersionNo(int appId,String pckgId) {
        String versionNo = "";
        String countQuery  = "SELECT "+APP_VERSION+" FROM " + APP_INFO_TABLE+" WHERE "+APP_ID+" = '"+appId+"' AND "+APP_PACKAGE_NAME+" = '"+pckgId+"'";
        System.out.println("countQuery......"+countQuery);
        Cursor cur = this.mDb.rawQuery(countQuery, null);
        if(cur.moveToFirst()){
            do{
                versionNo = cur.getString(0);
            }while(cur.moveToNext());
        }
        cur.close();
        // return count
        return versionNo;

    }


}
