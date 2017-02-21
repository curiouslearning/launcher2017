package bagroundservice;

import android.app.IntentService;
import android.content.Intent;

import java.util.Calendar;

import database.BackgroundDataCollectionDB;
import database.DBAdapter;
import preference_manger.SettingManager;
import util.MemoryStats;
import util.UStats;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class AppUsageSchedulingService extends IntentService {
    public AppUsageSchedulingService() {
        super("SchedulingService");
    }


    long startTime =0;
    DBAdapter mDbAdapter;
    BackgroundDataCollectionDB backgroundDataCollectionDB;



    @Override
    public void onCreate() {
        super.onCreate();
        mDbAdapter = new DBAdapter(this);
        mDbAdapter.open();
        backgroundDataCollectionDB = new BackgroundDataCollectionDB(this);
        backgroundDataCollectionDB.open();

    }


    @Override
    public void onDestroy() {
        if(backgroundDataCollectionDB!=null)
            backgroundDataCollectionDB.close();
        if(mDbAdapter!=null)
            mDbAdapter.close();

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
        // The URL from which to fetch content.
        handleAppUsageData();
        // Release the wake lock provided by the BroadcastReceiver.
        AppUsageAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }


    /**
     * Method to get app usage data from last sync time.
     * If last sync time == 0 then, the last sync time will be the previous date time.
     */

    private void handleAppUsageData(){
        //  Log.i("startTime",""+SettingManager.getInstance(this).getLastSyncTime());
        startTime = SettingManager.getInstance(this).getLastSyncTime();
        if(startTime==0){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            startTime = calendar.getTimeInMillis();
            SettingManager.getInstance(this).setLastSyncTime(startTime);
        }
        UStats instance =  UStats.getInstance(this);
        instance.setDBHandler(mDbAdapter,backgroundDataCollectionDB);
        instance.getCurrentUsageStatus(this,startTime);
        MemoryStats.getInstance(mDbAdapter,backgroundDataCollectionDB,this)._doInsertInfo();
    }

}
