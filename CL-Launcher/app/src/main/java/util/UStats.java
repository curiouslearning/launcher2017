package util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import database.BackgroundDataCollectionDB;
import database.DBAdapter;
import model.AppUsageModel;
import model.BackgroundDataModel;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by User on 3/2/15.
 */
public class UStats {
    private DateFormat mDateFormat = new SimpleDateFormat();
    public static final String TAG = UStats.class.getSimpleName();
    private DBAdapter mDbAdapter;
    // private DeviceAppUsageTable mDeviceAppUsageTable;
    private BackgroundDataCollectionDB backgroundDataCollectionDB;
    private Context _Context;
    private long TimeInforground = 500 ;
    private int minutes=500,seconds=500,hours=500 ;
    private static UStats _instance;

    public static UStats getInstance(Context _Context){
        if(_instance==null){
            _instance = new UStats(_Context);
        }
        return _instance;
    }

    private UStats(Context _Context){
        this._Context=_Context;
    }

    public void setDBHandler(DBAdapter mDbAdapter,
                             BackgroundDataCollectionDB backgroundDataCollectionDB){
        this.mDbAdapter = mDbAdapter;
        this.backgroundDataCollectionDB = backgroundDataCollectionDB;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public List<UsageStats> getUsageStatsList(Context context,long startTime){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        //  calendar.add(Calendar.YEAR, -1);
        // long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + mDateFormat.format(new Date(startTime)) );
        Log.d(TAG, "Range end:" + mDateFormat.format(new Date(endTime)));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void printUsageStats(Context ctx,List<UsageStats> usageStatsList){
        AppUsageModel mAppUsageModel;
        for (UsageStats u : usageStatsList){
            mAppUsageModel = new AppUsageModel();

            mAppUsageModel.setApp_name(Utils.getAppName(ctx,u.getPackageName()));
            mAppUsageModel.setApp_package_name(u.getPackageName());
            mAppUsageModel.setApp_first_time_stamped(mDateFormat.format(new Date(u.getFirstTimeStamp())));
            mAppUsageModel.setApp_last_time_stamped(mDateFormat.format(new Date(u.getLastTimeStamp())));
            mAppUsageModel.setApp_last_time_used(mDateFormat.format(new Date(u.getLastTimeStamp())));
            mAppUsageModel.setTime_in_foreground(convertTotalTimeInForegroundString(u.getTotalTimeInForeground()));
            mAppUsageModel.setLatitude(getLatitude());
            mAppUsageModel.setLongitude(getLongitude());
            mAppUsageModel.setSync_status(Constants.STATUS_NOT_SYNC);
            mAppUsageModel.setSync_time(mDateFormat.format(new Date(Calendar.getInstance().getTimeInMillis())));
            LogUtil.createLog(TAG, "Pkg: " + u.getPackageName() +  "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground()) ;
            _insertToDB(mAppUsageModel);
        }

    }

    public void getCurrentUsageStatus(Context context,long startTime){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            printUsageStats(context,getUsageStatsList(context,startTime));
        else
            doInsertPrevVersionUsageDetail(context);

    }
    @SuppressWarnings("ResourceType")
    private UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + mDateFormat.format(new Date(startTime)));
        Log.d(TAG, "Range end:" + mDateFormat.format(new Date(endTime)));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        return usageStatsList;
    }

    private String getProcessName(Context context,long startTime,long endTime) {
        String foregroundProcess = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        // Process running
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = getUsageStatsManager(context);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
            // Sort the stats by the last time used
            if(stats != null) {
                SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if(mySortedMap != null && !mySortedMap.isEmpty()) {
                    String topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    foregroundProcess = topPackageName;
                }
            }
        } else {
            @SuppressWarnings("deprecation") ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
            foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();

        }
        return foregroundProcess;
    }


    /**
     *
     * @param appUsageModel
     */
    private void _insertToDB(AppUsageModel appUsageModel){
        try{
            if(mDbAdapter!=null)
                mDbAdapter.open();
            if(backgroundDataCollectionDB !=null){
                backgroundDataCollectionDB.open();
                BackgroundDataModel model = new BackgroundDataModel();
                model.setName(_Context.getPackageName());
                model.setTimeStamp(System.currentTimeMillis()+"");
                model.setJsonData(getJsonFromModel(appUsageModel));
                if(backgroundDataCollectionDB!=null){
                    backgroundDataCollectionDB.insertInfo(model);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private String getJsonFromModel(AppUsageModel appUsageModel){
        String jsonString = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("key",Constants.KEY_IN_APP);
            JSONObject valueObject = new JSONObject();
            valueObject.put("tabletID",Utils.getDeviceId(_Context));
            valueObject.put("appID",appUsageModel.getApp_package_name());
            valueObject.put("time_started",appUsageModel.getApp_first_time_stamped());
            valueObject.put("time_used_sec",appUsageModel.getTime_in_foreground());
            jsonObject.put("value",valueObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonString = jsonObject.toString();
        LogUtil.createLog(TAG,"JSON INFO ::"+jsonString);
        return jsonString;
    }



    /**
     *
     * @param totTimeForeground
     * @return
     */

    private String convertTotalTimeInForegroundString(long totTimeForeground){
        minutes = (int) ((totTimeForeground / (1000*60)) % 60);
        seconds = (int) (totTimeForeground / 1000) % 60 ;
        hours   = (int) ((totTimeForeground / (1000*60*60)) % 24);
        return hours+"h"+":"+minutes+"m"+seconds+"s";
    }

    /**
     *
     * @return
     */

    private String getLatitude(){
        /*if(LocationFetchingService.mCurrentLocation!=null){

            LogUtil.createLog("Launcher location","lat :"+LocationFetchingService.mCurrentLocation.getLatitude());
            return  String.valueOf(LocationFetchingService.mCurrentLocation.getLatitude());
        }*/
        return "0.0";
    }

    /**
     *
     * @return
     */

    private String getLongitude(){
       /* if(LocationFetchingService.mCurrentLocation!=null){
            LogUtil.createLog("Launcher location","longitude :"+LocationFetchingService.mCurrentLocation.getLatitude());
            return  String.valueOf(LocationFetchingService.mCurrentLocation.getLongitude());
        }*/
        return "0.0";
    }


    private void   doInsertPrevVersionUsageDetail(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < recentTasks.size(); i++)
        {
            Log.d("Executed app", "Application executed : " +recentTasks.get(i).baseActivity.toShortString()+ "\t\t ID: "+recentTasks.get(i).id+"");
        }

        @SuppressWarnings("deprecation")
        ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
        String foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();
    }

}
