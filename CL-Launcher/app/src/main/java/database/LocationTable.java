package database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import util.Constants;

import static database.DBAdapter.DATABASE_NAME;
import static database.DBAdapter.DATABASE_VERSION;

/**
 * Created by IMFCORP\alok.acharya on 27/12/16.
 */

public class LocationTable {

    public static final String ROW_ID = "_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ADDRESS = "address";
    public static final String SYNC_STATUS = "sync_status";
    public static final String SYNC_TIME = "sync_time";

    private final String  TAG = "LocationTable";



    private static final String LOCATION_INFO_TABLE = "LocationTable";

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
    public LocationTable(Context ctx) {
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
    public LocationTable open() throws SQLException {
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

    public boolean isOpen(){
        return this.mDb.isOpen();
    }

    /**
     * Create a new AppUsageModel. If the AppUsageModel is successfully created return the new
     * rowId for that AppUsageModel, otherwise return a -1 to indicate failure.

     * @return rowId or -1 if failed
     */

/*
    public long insertLocationInfo(LocationModel info){
        LogUtil.createLog(TAG,"inserting data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(LATITUDE, info.getLatitude());
        initialValues.put(LONGITUDE, info.getLongitude());
        initialValues.put(ADDRESS, info.getAddress());
        initialValues.put(SYNC_STATUS, info.getSync_status());
        initialValues.put(SYNC_TIME, info.getSync_time());
        return this.mDb.insert(LOCATION_INFO_TABLE, null, initialValues);
    }*/



   /* public ArrayList<LocationModel> getLocationInfo() {
        String selectQuery = "SELECT * FROM " + LOCATION_INFO_TABLE;
        ArrayList<LocationModel> appUsageModelList = new ArrayList<LocationModel>();
        LocationModel info = new LocationModel();
        Cursor cur = this.mDb.rawQuery(selectQuery, null);
        if(cur.moveToFirst()){
            do{
                info.set_id(cur.getString(0));
                info.setLatitude(cur.getString(1));
                info.setLongitude(cur.getString(2));
                info.setAddress(cur.getString(3));
                info.setSync_status(cur.getString(4));
                info.setSync_time(cur.getString(5));
                appUsageModelList.add(info);
            }while(cur.moveToNext());
        }
        return appUsageModelList;


    }*/



    //Delete all info

    public void deleteAll_LocationDetails(){

        String deletequery ="DELETE from "+ LOCATION_INFO_TABLE;
        this.mDb.execSQL(deletequery);
    }


    //Delete single info

    public void deleteAll_LocationDetails(String id){

        String deletequery ="DELETE from "+ LOCATION_INFO_TABLE +" WHERE "+ROW_ID+" = '"+id+"' AND "+SYNC_STATUS+" = '"+ Constants.STATUS_SYNC+"'";
        this.mDb.execSQL(deletequery);
    }


    //Delete single info

    public void deleteAll_SyncLocationInfo(){

        String deletequery ="DELETE from "+ LOCATION_INFO_TABLE +" WHERE "+SYNC_STATUS+" = '"+ Constants.STATUS_SYNC+"'";
        this.mDb.execSQL(deletequery);
    }



    // Getting  Count
    public int getCount() {
        String countQuery  = "SELECT * FROM " + LOCATION_INFO_TABLE;
        System.out.println("countQuery......"+countQuery);
        Cursor cursor = this.mDb.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        // return count
        return c;

    }

}
