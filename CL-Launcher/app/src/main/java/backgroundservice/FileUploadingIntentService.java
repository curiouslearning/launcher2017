package backgroundservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import apihandler.NetworkStatus;
import excelsoft.com.cl_launcher.R;
import preference_manger.SettingManager;
import util.Constants;
import util.LogUtil;
import util.UploadDBFile;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FileUploadingIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FILE_UPLOAD = "backgroundservice.action.Fileupload";
    public static final String ACTION_FILE_COPY = "backgroundservice.action.Filecopy";
    SettingManager settingManager;

    public FileUploadingIntentService() {
        super("FileUploadingIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFileUpload(Context context) {
        Intent intent = new Intent(context, FileUploadingIntentService.class);
        intent.setAction(ACTION_FILE_UPLOAD);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFileCopy(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FileUploadingIntentService.class);
        intent.setAction(ACTION_FILE_COPY);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            settingManager = SettingManager.getInstance(this);
            if (ACTION_FILE_UPLOAD.equals(action)) {
              //  handleActionFileUpload();
            } else if (ACTION_FILE_COPY.equals(action)) {
                handleActionCopyFile();
            }
            FileUploadAlarmService.completeWakefulIntent(intent);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFileUpload(File file) {
        LogUtil.createLog("Files to upload", "FileName:" + file.getAbsolutePath());
        if(NetworkStatus.getInstance().isConnected(this)){

            String accessToken = settingManager.getAccessToken();
            String serialNo = settingManager.getCL_SerialNo();
            Uri fileUri = Uri.parse(file.getAbsolutePath());
          //  UploadDBFile.uploadFile(accessToken,serialNo,fileUri);
        }

    }

    /**
     * handleActionCopyFile
     */
    private void handleActionCopyFile() {

        UploadDBFile uploadDBFile =  new UploadDBFile(this);
        File destFile = FileUtils.getFile(Constants.COPY_DB_FILE_PATH +File.separator +getResources().getString(R.string.cl_db_name));

        //if file not exist and background has content data.

        if(!destFile.exists()&&uploadDBFile.getContentSize()>0){
            File srcFile = FileUtils.getFile(Constants.DATABASE_FILE_PATH+
                    File.separator+getResources().getString(R.string.cl_db)+
                    File.separator+getResources().getString(R.string.cl_db_name));
            if(srcFile.exists()) {
                File destFileDir = FileUtils.getFile(Constants.COPY_DB_FILE_PATH);
                if(!destFileDir.exists()){
                    destFileDir.mkdirs();
                }
                try {
                    if(UploadDBFile.copyFileUsingStream(srcFile,destFile)){
                        handleActionFileUpload(destFile);
                        uploadDBFile.doFlushBackgroundData();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(destFile.exists()){
            handleActionFileUpload(destFile);
        }

    }




}
