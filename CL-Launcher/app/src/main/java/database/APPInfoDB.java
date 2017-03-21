package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import util.Constants;

/**
 * Created by IMFCORP\alok.acharya on 21/3/17.
 */

public class APPInfoDB {

    public static final String DATABASE_NAME = "APP_Info_DB";

    public static final int DATABASE_VERSION = 1;


    private static final String CREATE_TABLE_APP_INFO =
            "create table AppInfo (_id integer primary key autoincrement, "
                    + AppInfoTable.APP_ID+ " integer,"
                    + AppInfoTable.APP_TITLE+ " TEXT,"
                    + AppInfoTable.APP_PACKAGE_NAME+ " TEXT,"
                    + AppInfoTable.APP_APK_DOWNLOAD_PATH+ " TEXT,"
                    + AppInfoTable.APP_CONTENT_TYPE+ " TEXT,"
                    + AppInfoTable.APP_TYPE+ " TEXT,"
                    + AppInfoTable.APP_IS_VISIBLE+ " integer,"
                    + AppInfoTable.APP_VERSION+ " TEXT,"
                    + AppInfoTable.APP_IS_DOWNLOADED+ " TEXT,"
                    + AppInfoTable.APP_IS_INSTALLED+ " TEXT,"
                    + AppInfoTable.APP_APK_LOCAL_PATH+ " TEXT,"
                    + AppInfoTable.AVAILABLE_UPDATE_VERSION+ " integer,"
                    + AppInfoTable.SYNC_STATUS+ " TEXT,"
                    + AppInfoTable.SYNC_TIME+ " TEXT" + ");";




    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param ctx
     */
    public APPInfoDB(Context ctx)
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
                    + File.separator + "CL_APP_INFO_DB"
                    + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

            db.execSQL(CREATE_TABLE_APP_INFO);
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
    public APPInfoDB open() throws SQLException
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
