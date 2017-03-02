package adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import apihandler.ApiConstant;
import excelsoft.com.cl_launcher.Home;
import excelsoft.com.cl_launcher.R;
import model.AppInfoModel;
import preference_manger.SettingManager;
import util.Constants;
import util.Utils;

/**
 * Created by IMFCORP\alok.acharya on 24/2/17.
 */

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.CustomViewHolder>{

    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    MyDownloadDownloadStatusListenerV1
            myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1();
    RetryPolicy retryPolicy;
    Home home;
    HashMap<Integer,AppInfoModel> downLoadMap = new HashMap<>();
    HashMap<Integer,Integer> downLoadMapPosition = new HashMap<>();

    private ArrayList<AppInfoModel> dataList;
    private Context context;
    private SettingManager settingManager;

    public AppInfoAdapter(Context context,ArrayList<AppInfoModel> dataList) {
        this.dataList=dataList;
        this.context=context;
        this.settingManager = SettingManager.getInstance(context);
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        retryPolicy = new DefaultRetryPolicy();
        home = (Home) context;
    }


    public void setDataList(ArrayList<AppInfoModel> dataList){
        this.dataList=dataList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_icon_text, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final AppInfoModel model = dataList.get(position);
        holder.title.setText(model.getTitle());
        if(model.isInstalled()) {
            holder.loader.setVisibility(View.GONE);
            holder.icon.setImageDrawable(model.getIcon());
        }else{

            if(model.isDownloaded()) {
                holder.loader.setVisibility(View.GONE);
                holder.icon.setImageDrawable( context.getResources().getDrawable(R.drawable.ic_launcher_app_install));
            }else{

                holder.loader.setVisibility(View.VISIBLE);
                holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_app_not_install));
                String apkDownloadUrl = ApiConstant.APK_ENDPOINT_URL+settingManager.getCL_SerialNo()+ApiConstant.APK+
                        model.getApkName();
                //  DownloadAPKFileManger.getDownloadManager(context);
                //  long downLoadID = DownloadAPKFileManger.startDownloadManager(context, apkDownloadUrl,model.getTitle(), Constants.APK_PATH);
                int downloadId = startDownLoad(model.getId(),apkDownloadUrl,model.getTitle(),settingManager.getAccessToken());
               // myDownloadStatusListener.setModel(model,position);
                downLoadMap.put(downloadId,model);
                downLoadMapPosition.put(downloadId,position);
            }
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.isInstalled()){
                    context.startActivity(model.getIntent());
                }else {
                    if(model.isDownloaded()){
                        Utils.installAPK(context,model.getTitle());
                    }else {
                        Utils.showToast(context, context.getResources().getString(R.string.downloading_in_progress));
                    }
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ContentLoadingProgressBar loader;
        public TextView title;
        public ImageView icon;
        public CustomViewHolder(View itemView) {
            super(itemView);
            loader = (ContentLoadingProgressBar) itemView.findViewById(R.id.progressBarContentLoading);
            title = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);

        }
    }



    private int startDownLoad(int downloadId,String downloadUrl,String title,String accessToken){
        File filesDir = new File(Constants.APK_PATH);
        if(!filesDir.exists()){
            filesDir.mkdir();
        }

        Uri downloadUri = Uri.parse(downloadUrl);
        Uri destinationUri = Uri.parse(filesDir+"/"+title+".apk");
        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
                .setRetryPolicy(retryPolicy)
                .setDownloadContext(title)
                .addCustomHeader(ApiConstant.AUTHORIZATION, ApiConstant.BEARER+" "+accessToken)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(myDownloadStatusListener);

        if (downloadManager.query(downloadId) == DownloadManager.STATUS_NOT_FOUND) {
            downloadId = downloadManager.add(downloadRequest);
        }
        return downloadId;
    }


    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

        AppInfoModel model;
        int position;
        private void setModel(AppInfoModel model,int position ){
            this.model= model;
            this.position=position;
        }

        @Override
        public void onDownloadComplete(DownloadRequest request) {
            final int id = request.getDownloadId();
            model = downLoadMap.get(id);
            position = downLoadMapPosition.get(id);
            if(home!=null){
                if(home.updateDownloadInfo(model.getId(),true)) {
                    model.setDownloaded(true);
                    notifyItemChanged(position);
                }}
            if(downLoadMapPosition.size()>0)
                downLoadMapPosition.remove(id);
            if(downLoadMap.size()>0)
                downLoadMap.remove(id);
        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            final int id = request.getDownloadId();
            model = downLoadMap.get(id);
            position = downLoadMapPosition.get(id);
            if(home!=null){
                if(home.updateDownloadInfo(model.getId(),false)) {
                    model.setDownloaded(false);
                    notifyItemChanged(position);
                }
            }
            if(downLoadMapPosition.size()>0)
            downLoadMapPosition.remove(id);
            if(downLoadMap.size()>0)
            downLoadMap.remove(id);
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();

            System.out.println("######## onProgress ###### "+id+" : "+totalBytes+" : "+downloadedBytes+" : "+progress);

        }
    }

}
