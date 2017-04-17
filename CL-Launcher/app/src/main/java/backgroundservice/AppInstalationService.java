package backgroundservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import util.Constants;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppInstalationService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_APP_INSTALL = "backgroundservice.action.ACTION_APP_INSTALL";
    private static final String ACTION_APP_UNINSTALL = "backgroundservice.action.ACTION_APP_UNINSTALL";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM_APP_INSTALL = "backgroundservice.extra.PARAM_APP_INSTALL";
    private static final String EXTRA_PARAM_APP_UNINSTALL = "backgroundservice.extra.PARAM_APP_UNINSTALL";

    private Process proc = null;

    public AppInstalationService() {
        super("AppInstalationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionInstall(Context context, String param) {
        Intent intent = new Intent(context, AppInstalationService.class);
        intent.setAction(ACTION_APP_INSTALL);
        intent.putExtra(EXTRA_PARAM_APP_INSTALL, param);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUninstall(Context context, String param) {
        Intent intent = new Intent(context, AppInstalationService.class);
        intent.setAction(ACTION_APP_UNINSTALL);
        intent.putExtra(EXTRA_PARAM_APP_UNINSTALL, param);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_APP_INSTALL.equals(action)) {
                final String param = intent.getStringExtra(EXTRA_PARAM_APP_INSTALL);
                handleActionAppInstallation(param);
            } else if (ACTION_APP_UNINSTALL.equals(action)) {
                final String param = intent.getStringExtra(EXTRA_PARAM_APP_UNINSTALL);
                handleActionAppUnInstall(param);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAppInstallation(String path) {
        try {
            if (proc != null) {
                proc.getOutputStream().close();
                proc.getInputStream().close();
                proc.getErrorStream().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        path = Constants.APK_PATH+"/"+path.replaceAll("\\s+","") +".apk";
        File file = new File(path);
        if (file.exists()) {
            try {
                Log.i("APP", "App installation is started");
                final String command = "pm install -r " + file.getAbsolutePath();
                proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
                // Runtime.getRuntime().exec("pm install "+ file.getAbsolutePath()).waitFor();


                        /*String command = "adb install " + file.getAbsolutePath();;A
                        Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                        proc.waitFor();*/
                ;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

            }
        }else
           // Toast.makeText(AppInstalationService.this, "No file exist", Toast.LENGTH_SHORT).show();
        Log.i("APP", "No file exis for App installation");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAppUnInstall(String path) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
