package adapter;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import excelsoft.com.cl_launcher.Home;
import excelsoft.com.cl_launcher.R;
import model.AppInfoModel;
import preference_manger.SettingManager;
import util.Constants;
import util.LogUtil;

/**
 * Created by IMFCORP\alok.acharya on 24/2/17.
 */

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.CustomViewHolder>{

    // private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    // MyDownloadDownloadStatusListenerV1
    //         myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1();
    // RetryPolicy retryPolicy;
    Home home;
    private ArrayList<AppInfoModel> dataList;
    private Context context;
    private SettingManager settingManager;
    public static final long appUpdateTime =60000L;
    OnItemDownLoadStartListener onItemDownLoadStartListener;
    OnItemClickListener onItemClickListener;




    public AppInfoAdapter(Context context,ArrayList<AppInfoModel> dataList) {
        this.dataList=dataList;
        this.context=context;
        this.settingManager = SettingManager.getInstance(context);
        ////downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        //retryPolicy = new DefaultRetryPolicy();
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
        LogUtil.createLog("onBindViewHolder called",position+"");
        final AppInfoModel model = dataList.get(position);
        holder.title.setText(model.getAppTitle());

        if(model.isInstalationStatus()) {
            holder.loader.setVisibility(View.GONE);
            holder.icon.setImageDrawable(model.getIcon());

            /*if(model.getIsUpdateVersionExist()==Constants.UPDATE_AVAILABLE
                    && model.getDownloadStatus()==Constants.ACTION_NOT_DOWNLOAD_YET){
                if(onItemDownLoadStartListener!=null)
                    onItemDownLoadStartListener.onStartDownLoad(position);
            }*/

        }else{

            if(model.getDownloadStatus()== Constants.ACTION_DOWNLOAD_COMPLETED) {
                holder.loader.setVisibility(View.GONE);
                holder.icon.setImageDrawable( context.getResources().getDrawable(R.drawable.ic_launcher_app_install));
            }else if(model.getDownloadStatus()== Constants.ACTION_DOWNLOAD_FAILED
                    || model.getDownloadStatus()==Constants.ACTION_NOT_DOWNLOAD_YET) {
                holder.loader.setVisibility(View.GONE);
                holder.icon.setImageDrawable( context.getResources().getDrawable(R.drawable.ic_launcher_app_not_install));

            }else if(model.getDownloadStatus()== Constants.ACTION_DOWNLOAD_STARTED
                    || model.getDownloadStatus()==Constants.ACTION_DOWNLOAD_RUNNING){

                holder.loader.setVisibility(View.VISIBLE);
                holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_app_not_install));
               /* if(onItemDownLoadStartListener!=null)
                    onItemDownLoadStartListener.onStartDownLoad(position);*/
            }
        }

        if(model.getType()!=null){
            holder.frame.setVisibility(View.VISIBLE);
        }else{
            holder.frame.setVisibility(View.GONE);
        }


       /* holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.isInstalationStatus()){
                    if(model.getIsUpdateVersionExist()==Constants.UPDATE_AVAILABLE&&model.isDownloadStatus()){
                        Utils.installAPK(context,model.getAppTitle());
                    }else{
                        context.startActivity(model.getIntent());
                    }
                }else {
                    if(model.isDownloadStatus()){
                        Utils.installAPK(context,model.getAppTitle());
                    }else {
                        Utils.showToast(context, context.getResources().getString(R.string.downloading_in_progress));
                    }
                }
            }
        });
*/


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ContentLoadingProgressBar loader;
        public TextView title;
        public ImageView icon;
        public LinearLayout frame;
        public CustomViewHolder(View itemView) {
            super(itemView);
            loader = (ContentLoadingProgressBar) itemView.findViewById(R.id.progressBarContentLoading);
            title = (TextView) itemView.findViewById(R.id.text);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            frame = (LinearLayout) itemView.findViewById(R.id.appFrame);
            frame.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener!=null){
                onItemClickListener.onClick(getPosition());
            }
        }
    }



   /* private int startDownLoad(int downloadId,String downloadUrl,String title,String accessToken){

        LogUtil.createLog("File start download",downloadUrl);

        File filesDir = new File(Constants.APK_PATH);
        if(!filesDir.exists()){
            filesDir.mkdir();
        }

        Uri downloadUri = Uri.parse(downloadUrl);
        Uri destinationUri = Uri.parse(filesDir+"/"+title+".apk");
        File destinationUriFile = new File(destinationUri.toString());
        if(destinationUriFile.exists()){
            destinationUriFile.delete();
            LogUtil.createLog("File delete before download",destinationUri.toString());
        }


        final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
                .setRetryPolicy(retryPolicy)
                .setDownloadContext(title)
                .addCustomHeader(ApiConstant.AUTHORIZATION, ApiConstant.BEARER+" "+accessToken)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(myDownloadStatusListener);


        int status = downloadManager.query(downloadId);

        if (downloadManager.query(downloadId) == DownloadManager.STATUS_NOT_FOUND) {
            downloadId = downloadManager.add(downloadRequest);
            LogUtil.createLog("download starting",destinationUri.toString());
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
            LogUtil.createLog("onDownloadComplete ::",model.getAppTitle());
            position = downLoadMapPosition.get(id);
            if(home!=null){
                if(home.updateDownloadInfo(model.getAppId(),true)) {
                    model.setDownloadStatus(true);
                    notifyItemChanged(position);
                }}

            if(model.getType()==null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        home.checkAndStartUpdatCLAPP();
                    }
                },appUpdateTime) ;

            }
            if(downLoadMapPosition.size()>0)
                downLoadMapPosition.remove(id);
            if(downLoadMap.size()>0)
                downLoadMap.remove(id);

        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            final int id = request.getDownloadId();
            model = downLoadMap.get(id);
            LogUtil.createLog("onDownloadFailed ::",model.getAppTitle());
            position = downLoadMapPosition.get(id);
            downLoadIdMap.put(model.getAppPckageName(),id);
            if(home!=null){
                if(home.updateDownloadInfo(model.getAppId(),false)) {
                    model.setDownloadStatus(false);
                    notifyItemChanged(position);
                    model.setDownloadedFailed(true);
                    Utils.showToast(context,"There is problem for downloading some file.");
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
    }*/






    public interface OnItemDownLoadStartListener {
        void onStartDownLoad(int position);
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }



    public void setOnItemDownLoadStartListener(OnItemDownLoadStartListener onItemDownLoadStartListener){
        this.onItemDownLoadStartListener=onItemDownLoadStartListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }




    public void receiveNotification(int itemPosition){

        notifyItemChanged(itemPosition);
    }



}
