package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import model.BackgroundDataModel;
import util.Constants;
import util.LogUtil;

import static database.DBAdapter.DATABASE_NAME;
import static database.DBAdapter.DATABASE_VERSION;

/**
 * Created by IMFCORP\alok.acharya on 15/2/17.
 */

public class BackgroundDataCollectionDB {


    public static final String ROW_ID = "_id";
    public static final String NAME = "name";
    public static final String TIME_STAMP = "timestamp";
    public static final String JSON_DATA = "value";
    private final String  TAG = "BackgroundDataCollectionTable";
    private static final String BACKGROUND_DATA_COLLECTION_TABLE = "BackgroundDataCollectionTable";

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
    public BackgroundDataCollectionDB(Context ctx) {
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
    public BackgroundDataCollectionDB open() throws SQLException {
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


    public synchronized long insertInfo(BackgroundDataModel info){
        LogUtil.createLog(TAG,"inserting data into  data base...");
        ContentValues initialValues = new ContentValues();
        initialValues.put(NAME, info.getName());
        initialValues.put(TIME_STAMP, info.getTimeStamp());
        initialValues.put(JSON_DATA, info.getJsonData());
        return this.mDb.insert(BACKGROUND_DATA_COLLECTION_TABLE, null, initialValues);
    }




    //Delete all info

    public synchronized void deleteAllDetails(){

        String deletequery ="DELETE from "+ BACKGROUND_DATA_COLLECTION_TABLE;
        System.out.println("countQuery......"+deletequery);
        this.mDb.execSQL(deletequery);
    }




    // Getting  Count
    public synchronized int getCount() {
        String countQuery  = "SELECT * FROM " + BACKGROUND_DATA_COLLECTION_TABLE;
        System.out.println("countQuery......"+countQuery);
        Cursor cursor = this.mDb.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        // return count
        return c;

    }
}
