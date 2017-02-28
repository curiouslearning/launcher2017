package util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.io.File;

import apihandler.ApiConstant;
import preference_manger.SettingManager;

/**
 * Created by IMFCORP\alok.acharya on 28/2/17.
 */

public class DownloadAPKFileManger {

  /*  private SettingManager settingManager;
    private Context mContext;

    public DownloadAPKFileManger(Context mContext){
        this.mContext = mContext;
        this.settingManager = new SettingManager(mContext);
    }*/

    private static DownloadManager downloadManager =null;
    public static DownloadManager getDownloadManager(Context mContext){
        if(downloadManager == null){
            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        return downloadManager;
    }



    public static long startDownloadManager(Context mContext,String downloadURL,
                                      String title, String storagePath) {
        File file = new File(storagePath);
        if (file.exists()) {
            file.delete();
        }
        Uri downloadURI = Uri.parse(downloadURL);
        DownloadManager.Request request = new DownloadManager.Request(downloadURI);
        request.setTitle(title);
        request.addRequestHeader(ApiConstant.AUTHORIZATION,ApiConstant.BEARER+" "+ SettingManager.getInstance(mContext).getAccessToken());
        downloadURI = Uri.fromFile(new File(storagePath));
        request.setDestinationUri(downloadURI);
       // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        long downloadID = downloadManager.enqueue(request);
        return downloadID;
    }

}
