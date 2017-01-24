package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;

import model.AppUsageModel;
import util.Constants;
import util.LogUtil;

import static database.DBAdapter.DATABASE_NAME;
import static database.DBAdapter.DATABASE_VERSION;


public class DeviceAppUsageTable {

	public static final String ROW_ID = "_id";
	public static final String APP_NAME = "app_name";
	public static final String APP_PACKAGE_NAME = "app_package_name";
	public static final String APP_FIRST_TIMESTAMP = "app_first_time_stamped";
	public static final String APP_LAST_TIMESTAMP = "app_last_time_stamped";
	public static final String APP_LAST_TIME_USED = "app_last_time_used";
	public static final String APP_TOT_TIME_IN_FOREGROUND = "time_in_foreground";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String SYNC_STATUS = "sync_status";
	public static final String SYNC_TIME = "sync_time";

	private final String  TAG = "DeviceAppUsageDB";



	private static final String APP_USAGE_INFO_TABLE = "AppUsageInfo";

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
	public DeviceAppUsageTable(Context ctx) {
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
	public DeviceAppUsageTable open() throws SQLException {
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


	public long insertAppUsageInfo(AppUsageModel info){
		LogUtil.createLog(TAG,"inserting data into  data base...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(APP_NAME, info.getApp_name());
		initialValues.put(APP_PACKAGE_NAME, info.getApp_package_name());
		initialValues.put(APP_FIRST_TIMESTAMP, info.getApp_first_time_stamped());
		initialValues.put(APP_LAST_TIMESTAMP, info.getApp_last_time_stamped());
		initialValues.put(APP_LAST_TIME_USED, info.getApp_last_time_used());
		initialValues.put(APP_TOT_TIME_IN_FOREGROUND, info.getTime_in_foreground());
		initialValues.put(LATITUDE, info.getLatitude());
		initialValues.put(LONGITUDE, info.getLongitude());
		initialValues.put(SYNC_STATUS, info.getSync_status());
		initialValues.put(SYNC_TIME, info.getSync_time());
		return this.mDb.insert(APP_USAGE_INFO_TABLE, null, initialValues);
	}



	public ArrayList<AppUsageModel> getAppUsageInfo() {

		String selectQuery = "SELECT * FROM " + APP_USAGE_INFO_TABLE;
		ArrayList<AppUsageModel> appUsageModelList = new ArrayList<AppUsageModel>();
		AppUsageModel info = new AppUsageModel();
		Cursor cur = this.mDb.rawQuery(selectQuery, null);
		if(cur.moveToFirst()){
			do{
				info.set_id(cur.getString(0));
				info.setApp_name(cur.getString(1));
				info.setApp_package_name(cur.getString(2));
				info.setApp_first_time_stamped(cur.getString(3));
				info.setApp_last_time_stamped(cur.getString(4));
				info.setApp_last_time_used(cur.getString(5));
				info.setTime_in_foreground(cur.getString(6));
				info.setLatitude(cur.getString(7));
				info.setLongitude(cur.getString(8));
				info.setSync_status(cur.getString(9));
				info.setSync_time(cur.getString(10));
				appUsageModelList.add(info);
			}while(cur.moveToNext());
		}
		return appUsageModelList;


	}



	//Delete all info

	public void deleteAll_AppUsageDetails(){

		String deletequery ="DELETE from "+ APP_USAGE_INFO_TABLE;
		this.mDb.execSQL(deletequery);
	}


	//Delete single info

	public void deleteAll_AppUsageDetails(String id){

		String deletequery ="DELETE from "+ APP_USAGE_INFO_TABLE+" WHERE "+ROW_ID+" = '"+id+"' AND "+SYNC_STATUS+" = '"+ Constants.STATUS_SYNC+"'";
		this.mDb.execSQL(deletequery);
	}


	//Delete single info

	public void deleteAll_SyncAppInfo(){

		String deletequery ="DELETE from "+ APP_USAGE_INFO_TABLE+" WHERE "+SYNC_STATUS+" = '"+ Constants.STATUS_SYNC+"'";
		this.mDb.execSQL(deletequery);
	}



	// Getting  Count
	public int getCount() {
		String countQuery  = "SELECT * FROM " + APP_USAGE_INFO_TABLE;
		System.out.println("countQuery......"+countQuery);
		Cursor cursor = this.mDb.rawQuery(countQuery, null);
		int c = cursor.getCount();
		cursor.close();
		// return count
		return c;

	}


}
