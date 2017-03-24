package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import util.Constants;
import util.LogUtil;

/**
 * Created by IMFCORP\alok.acharya on 21/3/17.
 */

public class APPInfoDB {

    public static final String DATABASE_NAME = "APP_Info_DB";

    public static final int DATABASE_VERSION = 2;

    private static final String CREATE_TABLE_APP_INFO =
            "create table if not exists AppInfo (_id integer primary key autoincrement, "
                    + AppInfoTable.APP_ID+ " integer,"
                    + AppInfoTable.APP_TITLE+ " TEXT,"
                    + AppInfoTable.APP_PACKAGE_NAME+ " TEXT,"
                    + AppInfoTable.APP_APK_DOWNLOAD_PATH+ " TEXT,"
                    + AppInfoTable.APP_CONTENT_TYPE+ " TEXT,"
                    + AppInfoTable.APP_TYPE+ " TEXT,"
                    + AppInfoTable.APP_IS_VISIBLE+ " integer,"
                    + AppInfoTable.APP_VERSION+ " TEXT,"
                    + AppInfoTable.APP_DOWNLOAD_STATUS + " TEXT,"
                    + AppInfoTable.APP_INSTALATION_STATUS + " TEXT,"
                    + AppInfoTable.APP_APK_LOCAL_PATH+ " TEXT,"
                    + AppInfoTable.APP_UPDATE_VERSION + " integer,"
                    + AppInfoTable.UPDATE_AVAILABLE_STATUS + " TEXT,"
                    + AppInfoTable.IS_UPDATED+ " TEXT,"
                    + AppInfoTable.MISC+ " integer,"
                    + AppInfoTable.SYNC_STATUS+ " TEXT,"
                    + AppInfoTable.SYNC_TIME+ " TEXT,"
                    + AppInfoTable.INSTALLATION_PROCESS_INITIATE_STATUS+ " TEXT,"
                    + AppInfoTable.DOWNLOAD_ID+ " integer" + ");";

    private static final String RENAME_APP_INFO_TABLE_TO_TEMP_NAME = "ALTER TABLE AppInfo RENAME TO AppInfo_temp";

    private static final String COPY_APPINFO_TEMP_TABLE_TO_APPINFO = "INSERT INTO AppInfo ("
            + AppInfoTable.APP_ID+ ","
            + AppInfoTable.APP_TITLE+ ","
            + AppInfoTable.APP_PACKAGE_NAME+ ","
            + AppInfoTable.APP_APK_DOWNLOAD_PATH+ ","
            + AppInfoTable.APP_CONTENT_TYPE+ ","
            + AppInfoTable.APP_TYPE+ ","
            + AppInfoTable.APP_IS_VISIBLE+ ","
            + AppInfoTable.APP_VERSION+ ","
            + AppInfoTable.APP_DOWNLOAD_STATUS+ ","
            + AppInfoTable.APP_INSTALATION_STATUS+ " ,"
            + AppInfoTable.APP_APK_LOCAL_PATH+ ","
            + AppInfoTable.APP_UPDATE_VERSION+ ","
            + AppInfoTable.UPDATE_AVAILABLE_STATUS+ ","
            + AppInfoTable.IS_UPDATED+ ","
            + AppInfoTable.MISC+ ","
            + AppInfoTable.SYNC_STATUS+ ","
            + AppInfoTable.SYNC_TIME+ ")"
            +" SELECT "
            + AppInfoTable.APP_ID+ ","
            + AppInfoTable.APP_TITLE+ ","
            + AppInfoTable.APP_PACKAGE_NAME+ ","
            + AppInfoTable.APP_APK_DOWNLOAD_PATH+ ","
            + AppInfoTable.APP_CONTENT_TYPE+ ","
            + AppInfoTable.APP_TYPE+ ","
            + AppInfoTable.APP_IS_VISIBLE+ ","
            +  "appVersion,"
            + "isDownLoaded,"
            + "isInstalled,"
            + AppInfoTable.APP_APK_LOCAL_PATH+ ","
            + AppInfoTable.APP_UPDATE_VERSION+ ","
            + "update_version,"
            + AppInfoTable.IS_UPDATED+ ","
            + AppInfoTable.MISC+ ","
            + AppInfoTable.SYNC_STATUS+ ","
            + AppInfoTable.SYNC_TIME+ " FROM AppInfo_temp;";

    private static final String DROP_TABLE_TEMP_APPINFO_TABLE = "DROP TABLE AppInfo_temp;";

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
        private  Context mcontext;

        DatabaseHelper(Context context)
        {

            //super(context, DATABASE_NAME, null, DATABASE_VERSION);
            super(context, Constants.DATABASE_FILE_PATH
                    + File.separator + "CL_APP_INFO_DB"
                    + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
            this.mcontext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

            db.execSQL(CREATE_TABLE_APP_INFO);
            LogUtil.createLog("DATABASE :",COPY_APPINFO_TEMP_TABLE_TO_APPINFO+":: "+CREATE_TABLE_APP_INFO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            // Adding any table mods to this guy here
            LogUtil.createLog("onUpgrade DB ::","old Version : "+oldVersion+" New Version :"+newVersion);
            switch (oldVersion){
                case 1:
                    upgradeDBTo2Version(db);
                    break;
            }
        }



        private void upgradeDBTo2Version(SQLiteDatabase db){

            db.beginTransaction();
            try {
                db.execSQL(RENAME_APP_INFO_TABLE_TO_TEMP_NAME);
                db.execSQL(CREATE_TABLE_APP_INFO);
                db.execSQL(COPY_APPINFO_TEMP_TABLE_TO_APPINFO);
                db.execSQL(DROP_TABLE_TEMP_APPINFO_TABLE);
                db.setTransactionSuccessful();

            }catch (Exception e){

                e.printStackTrace();
                db.endTransaction();
            }
            finally {
                db.endTransaction();
            }

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
