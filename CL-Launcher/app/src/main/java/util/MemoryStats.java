package util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import database.BackgroundDataCollectionDB;
import database.DBAdapter;
import model.BackgroundDataModel;
import model.MemoryUsage;

import static android.content.Context.ACTIVITY_SERVICE;
import static util.Constants.KEY_MEMORY_USAGE;
import static util.Utils.getAndroidVersion;
import static util.Utils.getDeviceId;

/**
 * Created by IMFCORP\alok.acharya on 16/2/17.
 */

public class MemoryStats {
    private DBAdapter mDbAdapter;
    private BackgroundDataCollectionDB backgroundDataCollectionDB;
    private Context _Context;
    private static MemoryStats _instance = null;

    public static MemoryStats getInstance(DBAdapter mDbAdapter,BackgroundDataCollectionDB backgroundDataCollectionDB,
                                          Context _Context){
        if(_instance==null){
            return new MemoryStats(mDbAdapter,backgroundDataCollectionDB,
                    _Context);
        }
        return  _instance;
    }


    private MemoryStats(DBAdapter mDbAdapter,BackgroundDataCollectionDB backgroundDataCollectionDB,
                Context _Context){
        this.mDbAdapter=mDbAdapter;
        this.backgroundDataCollectionDB=backgroundDataCollectionDB;
        this._Context=_Context;
    }

    private String getMemoryInfoJson(Context context){
        String jsonInfo = "";
        MemoryUsage memoryUsage = new MemoryUsage();
        MemoryUsage.Value memValue = new MemoryUsage().new Value();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long memUsed = memoryInfo.totalMem-memoryInfo.availMem;
        Log.i("Info", " memoryInfo.availMem " + memoryInfo.availMem + "\n" );
        Log.i("Info", " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n" );
        Log.i("Info", " memoryInfo.threshold " + memoryInfo.threshold + "\n" );
        Log.i("Info", " memoryInfo.memUsed " + memUsed + "\n" );

       /* MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;*/

        memoryUsage.setKey(KEY_MEMORY_USAGE);
        memValue.setTabletID(getDeviceId(context));
        memValue.setAndroidVersion(getAndroidVersion());
        memValue.setSpaceAvailable(memoryInfo.availMem+"");
        memValue.setSpaceInUse(memUsed+"");

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        Map<Integer, String> pidMap = new TreeMap<Integer, String>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses)
        {
            pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);
        }

        Collection<Integer> keys = pidMap.keySet();

        for(int key : keys)
        {
            int pids[] = new int[1];
            pids[0] = key;
            android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
            for(android.os.Debug.MemoryInfo pidMemoryInfo: memoryInfoArray)
            {
                Log.i("Info", String.format("** MEMINFO in pid %d [%s] **\n",pids[0],pidMap.get(pids[0])));
                Log.i("Info", " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");
                Log.i("Info", " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");
                Log.i("Info", " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");
                // Log.i("Info", " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getMemoryStats() + "\n"
                memValue.setClSoftwareSpace(pidMemoryInfo.getTotalPss()+"");

            }
            memValue.setClDataSpace("");
        }

        memoryUsage.setValue(memValue);
        Gson gson = new GsonBuilder().create();
        jsonInfo = gson.toJson(memoryUsage);
        LogUtil.createLog("JSON INFO",jsonInfo);

        return jsonInfo;

    }

    public void _doInsertInfo(){
                BackgroundDataModel model = new BackgroundDataModel();
                model.setName(_Context.getPackageName());
                model.setTimeStamp(System.currentTimeMillis()+"");
                model.setJsonData(getMemoryInfoJson(_Context));
                if(backgroundDataCollectionDB!=null){
                    backgroundDataCollectionDB.insertInfo(model);
                }
    }
}
