package backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import util.InAppRecordStats;
import util.LogUtil;


/**
 * Created by IMFCORP\alok.acharya on 2/3/17.
 */
public class InAppDataCollectionReceiver extends BroadcastReceiver {

    private static final String TAG = "InAppDataCollectionReceiver";
    private static final String NAME_KEY = "NAME";
    private static final String VALUE_KEY = "VALUE";
    private static final String TIMESTAMP_KEY = "TIMESTAMP";
    private InAppRecordStats mInAppRecordStats;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent!=null) {
            Bundle dataBundle = intent.getExtras();
            if(dataBundle!=null){
                String name = dataBundle.getString(NAME_KEY);
                String value = dataBundle.getString(VALUE_KEY);
                long timeStamp = dataBundle.getLong(TIMESTAMP_KEY);
                LogUtil.createLog(TAG, "Funf Record: " + name + " = " + value);
                mInAppRecordStats = InAppRecordStats.getInstance(context);
                mInAppRecordStats.doInsertInAppData(name,timeStamp,value);
            }
        }
    }
}
