package util;

import android.content.Context;
import android.os.AsyncTask;

import database.BackgroundDataCollectionDB;
import database.DBAdapter;
import model.BackgroundDataModel;

/**
 * Created by IMFCORP\alok.acharya on 6/3/17.
 */

public class InAppRecordStats {
    private DBAdapter mDbAdapter;
    private BackgroundDataCollectionDB backgroundDataCollectionDB;
    private Context _Context;
    private static InAppRecordStats _instance = null;


    public static InAppRecordStats getInstance(Context _Context){
        if(_instance==null){
            return new InAppRecordStats(_Context);
        }
        return  _instance;
    }


    private InAppRecordStats(Context _Context){
        this._Context=_Context;
    }


    public void doInsertInAppData(final String name, final long timeStamp,final String jsonData){


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(mDbAdapter==null){
                    mDbAdapter = new DBAdapter(_Context);
                    mDbAdapter.open();
                }
                if(!mDbAdapter.isOpen()){
                    mDbAdapter.open();
                }

                if(backgroundDataCollectionDB==null){
                    backgroundDataCollectionDB = new BackgroundDataCollectionDB(_Context);
                    backgroundDataCollectionDB.open();

                }
                if(!backgroundDataCollectionDB.isOpen()){
                    backgroundDataCollectionDB.open();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {

                BackgroundDataModel model = new BackgroundDataModel();
                model.setName(name);
                model.setTimeStamp(timeStamp+"");
                model.setJsonData(jsonData);
                if(backgroundDataCollectionDB!=null){
                    backgroundDataCollectionDB.insertInfo(model);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(mDbAdapter.isOpen()){
                    mDbAdapter.close();
                }
                if(backgroundDataCollectionDB.isOpen()){
                    backgroundDataCollectionDB.close();
                }
            }
        }.execute();
    }
}
