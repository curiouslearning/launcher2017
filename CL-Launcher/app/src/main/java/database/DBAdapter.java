package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import util.Constants;

public class DBAdapter {

	public static final String DATABASE_NAME = "CL_Launcher";

	public static final int DATABASE_VERSION = 1;


	// DATABASE ENTRY

	private static final String CREATE_TABLE_APP_USAGE_INFO =
			"create table AppUsageInfo (_id integer primary key autoincrement, "
					+ DeviceAppUsageTable.APP_NAME+ " TEXT,"
					+ DeviceAppUsageTable.APP_PACKAGE_NAME+ " TEXT,"
					+ DeviceAppUsageTable.APP_FIRST_TIMESTAMP+ " TEXT,"
					+ DeviceAppUsageTable.APP_LAST_TIMESTAMP+ " TEXT,"
					+ DeviceAppUsageTable.APP_LAST_TIME_USED+ " TEXT,"
					+ DeviceAppUsageTable.APP_TOT_TIME_IN_FOREGROUND+ " TEXT,"
					+ DeviceAppUsageTable.LATITUDE+ " TEXT,"
					+ DeviceAppUsageTable.LONGITUDE+ " TEXT,"
					+ DeviceAppUsageTable.SYNC_STATUS+ " TEXT,"
					+ DeviceAppUsageTable.SYNC_TIME+ " TEXT" + ");";



	private static final String CREATE_TABLE_LOCATION_INFO =
			"create table LocationTable (_id integer primary key autoincrement, "
					+ LocationTable.LATITUDE+ " TEXT,"
					+ LocationTable.LONGITUDE+ " TEXT,"
					+ LocationTable.ADDRESS+ " TEXT,"
					+ LocationTable.SYNC_STATUS+ " TEXT,"
					+ LocationTable.SYNC_TIME+ " TEXT" + ");";




	private static final String CREATE_TABLE_INFO =
			"create table BackgroundDataCollectionTable (_id integer primary key autoincrement, "
					+ BackgroundDataCollectionDB.NAME+ " TEXT,"
					+ BackgroundDataCollectionDB.TIME_STAMP+ " TEXT,"
					+ BackgroundDataCollectionDB.JSON_DATA+ " TEXT" + ");";







	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
	 * @param ctx
	 */
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			//super(context, DATABASE_NAME, null, DATABASE_VERSION);
			super(context, Constants.DATABASE_FILE_PATH
					+ File.separator + "CL_DB"
					+ File.separator + DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{

			//db.execSQL(CREATE_TABLE_APP_USAGE_INFO);
			//db.execSQL(CREATE_TABLE_LOCATION_INFO);
			db.execSQL(CREATE_TABLE_INFO);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
							  int newVersion)
		{
			// Adding any table mods to this guy here
		}
	}

	/**
	 * open the db
	 * @return this
	 * @throws SQLException
	 * return type: DBAdapter
	 */
	public DBAdapter open() throws SQLException
	{
		this.db = this.DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * close the db
	 * return type: void
	 */
	public void close()
	{
		this.DBHelper.close();
	}


	public boolean isOpen(){
		return this.db.isOpen();
	}

}
