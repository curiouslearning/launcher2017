package backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.File;

import database.APPInfoDB;
import database.AppInfoTable;
import util.Constants;
import util.LogUtil;

/**
 * Created by IMFCORP\alok.acharya on 3/3/17.
 */
public class PackageRemoveReciever extends BroadcastReceiver {
    APPInfoDB maDbAdapter;
    AppInfoTable mAppInfoTable;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
                String remove_package = intent.getData().toString().split(":")[1];
                // Utils.showToast(context,"alok PackageRemoveReciever::"+added_package);
                LogUtil.createLog("alok","PackageRemoveReciever ::"+remove_package);
                doUpdateAppInfo(context,remove_package);
            }
        }
    }


    private void doUpdateAppInfo(Context context,String pckgId){
        try {
            maDbAdapter = new APPInfoDB(context);
            maDbAdapter.open();
            mAppInfoTable = new AppInfoTable(context);
            mAppInfoTable.open();
          //  mAppInfoTable.updateAppVisibilityInfo(pckgId.trim(), Constants.APP_NOT_VISIBLE);
            mAppInfoTable.deleteAppDetails(pckgId);
            File deleteFile = new File(Constants.APK_PATH+"/"+pckgId+".apk");
            if(deleteFile.exists()){
                deleteFile.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mAppInfoTable.close();
            maDbAdapter.close();
            context.sendBroadcast(new Intent(Constants.ACTION_APP_RELOAD));
        }
    }
}
