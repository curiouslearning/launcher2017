package excelsoft.com.cl_launcher;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import apihandler.ApiClient;
import apihandler.ApiInterface;
import apihandler.NetworkStatus;
import database.APPInfoDB;
import database.AppInfoTable;
import device_admin_utill.CLDeviceManger;
import device_admin_utill.DevicePolicyAdmin;
import model.AppInfoModel;
import preference_manger.SettingManager;
import qrcodescanner.QRCodeScanning;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.Constants;
import util.LogUtil;
import util.UStats;
import util.Utils;

import static excelsoft.com.cl_launcher.Home.clPckgName;
import static excelsoft.com.cl_launcher.Home.clPckgName1;
import static excelsoft.com.cl_launcher.Home.packageMap;

//import static excelsoft.com.cl_launcher.Home.clPckgName2;

public class SettingScreen extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener,View.OnClickListener,Switch.OnCheckedChangeListener {

    private DevicePolicyManager deviceManger;
    private ComponentName compName;

    private View llPwdView;
    private RadioButton dfltPwd,clPwd,devicePwd;
    private RadioGroup pwdRadioGrp;
    private Button savePwdBtn,cancelPwdBtn,connectWifi,updateLauncherButton,cleanUpButton,setting_app_qrCodeScanner;
    private EditText edPwd,edCpwd;
    private SettingManager settingManager;
    private TextView txtMsg;
    private Switch mSwitch;
    private TextView tabletIdTxt,manifestVersionTxt,launcherVersionTxt,setting_launcher_endpointTxt;
    public static final String CLEAN_UP_ACTION = "app_clean_up";
    private ApiInterface apiInterface;
    private ProgressDialog loadingDialog;
    private String clPrevVersion = "";
    private String clCurrntVersion = "";
    int status =0;
    APPInfoDB maDbAdapter;
    AppInfoTable mAppInfoTable;
    String foregroundPkg ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen);

        llPwdView = (View)findViewById(R.id.ll_pwd);
        dfltPwd = (RadioButton) findViewById(R.id.setting_password_defaultTxt);
        clPwd = (RadioButton) findViewById(R.id.setting_password_clPwdTxt);
        devicePwd = (RadioButton) findViewById(R.id.setting_password_devicePwdTxt);
        pwdRadioGrp= (RadioGroup) findViewById(R.id.menu_setting_passwordGroup);
        savePwdBtn = (Button) findViewById(R.id.btn_savePwd);
        edPwd = (EditText) findViewById(R.id.ed_pwd);
        edCpwd = (EditText) findViewById(R.id.ed_cpwd);
        txtMsg = (TextView) findViewById(R.id.textViewMsg);
        updateLauncherButton = (Button) findViewById(R.id.setting_Cl_update);
        connectWifi = (Button) findViewById(R.id.connect_wifi);
        cleanUpButton = (Button) findViewById(R.id.setting_app_clean_up);
        tabletIdTxt= (TextView) findViewById(R.id.setting_tablet_id);
        manifestVersionTxt= (TextView) findViewById(R.id.setting_manifest_version);
        launcherVersionTxt= (TextView) findViewById(R.id.setting_launcher_version);
        setting_launcher_endpointTxt= (TextView) findViewById(R.id.setting_launcher_endpoint);
        cancelPwdBtn = (Button) findViewById(R.id.btn_CancelPwd);
        setting_app_qrCodeScanner = (Button) findViewById(R.id.setting_app_qrCodeScanner);
        mSwitch = (Switch) findViewById(R.id.setting_restrictHome_screenTxtSwitch);

        settingManager = SettingManager.getInstance(this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        loadingDialog = new ProgressDialog(this);

        compName = new ComponentName(this, DevicePolicyAdmin.class);
        deviceManger = CLDeviceManger.getCLDevicePolicyManager(this);

        updateLauncherText();

        if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_DEFAULT)){
            dfltPwd.setChecked(true);
            invisiblePwdView();
        }else  if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_CL)){
            clPwd.setChecked(true);
            visiblePwdView();

        }else  if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_ANDROID)){
            devicePwd.setChecked(true);
            invisiblePwdView();
            mSwitch.setEnabled(true);

        }
        mSwitch.setChecked(settingManager.getRestrictSettingScreen());

        tabletIdTxt.setText(getResources().getString(R.string.tablet_id)+settingManager.getCL_SerialNo());

        setting_launcher_endpointTxt.setText(getResources().getString(R.string.end_point)+BuildConfig.SERVICE_BASE_PATH);

        manifestVersionTxt.setText(getResources().getString(R.string.manifest_version)+settingManager.getManifestVersion());

        pwdRadioGrp.setOnCheckedChangeListener(this);
        mSwitch.setOnCheckedChangeListener(this);
        savePwdBtn.setOnClickListener(this);
        cancelPwdBtn.setOnClickListener(this);
        connectWifi.setOnClickListener(this);
        updateLauncherButton.setOnClickListener(this);
        cleanUpButton.setOnClickListener(this);
        setting_app_qrCodeScanner.setOnClickListener(this);
    }



    private void updateLauncherText(){
        AppInfoModel appInfoModel = packageMap.get(clPckgName);
        if(appInfoModel==null){
            return;
        }
        clPrevVersion = settingManager.getCLPreviousVersion();
        clCurrntVersion = appInfoModel.getAppVersion();
        status = Utils.versionCompare(clCurrntVersion,clPrevVersion);
        if(appInfoModel!=null) {

            if(status==0) {
                launcherVersionTxt.setText(getResources().getString(R.string.launcher_version) + clCurrntVersion);
            }else if(status>0){
                if(!clPrevVersion.equals("0"))
                    launcherVersionTxt.setText(getResources().getString(R.string.launcher_version) + clPrevVersion);
                else
                    launcherVersionTxt.setText(getResources().getString(R.string.launcher_version) + clCurrntVersion);
            }
            if (appInfoModel.getDownloadStatus() == Constants.ACTION_DOWNLOAD_COMPLETED &&
                    appInfoModel.getIsUpdateVersionExist() == Constants.UPDATE_AVAILABLE) {
                updateLauncherButton.setTextColor(getResources().getColor(R.color.black));
                updateLauncherButton.setEnabled(true);
                updateLauncherButton.setBackgroundResource(R.drawable.btn_enable_background);
            } else {
                updateLauncherButton.setTextColor(getResources().getColor(R.color.devider_color));
                updateLauncherButton.setEnabled(false);
                updateLauncherButton.setBackgroundResource(R.drawable.btn_disable_background);


            }

           /* if(clPrevVersion.equals("0")){
                settingManager.setCLPreviousVersion(clCurrntVersion);
                launcherVersionTxt.setText(getResources().getString(R.string.launcher_version) + settingManager.getCLPreviousVersion());
                updateLauncherButton.setTextColor(getResources().getColor(R.color.devider_color));
                updateLauncherButton.setEnabled(false);
            }else{
                if (appInfoModel.getDownloadStatus() == Constants.ACTION_DOWNLOAD_COMPLETED &&
                        appInfoModel.getIsUpdateVersionExist() == Constants.UPDATE_AVAILABLE && status>0) {
                    updateLauncherButton.setTextColor(getResources().getColor(R.color.black));
                    updateLauncherButton.setEnabled(true);

                } else if (appInfoModel.getIsUpdateVersionExist() == Constants.UPDATE_AVAILABLE && status>0) {
                    updateLauncherButton.setTextColor(getResources().getColor(R.color.devider_color));
                    updateLauncherButton.setEnabled(false);
                    doUpdateClVersion(clPckgName);

                }
                launcherVersionTxt.setText(getResources().getString(R.string.launcher_version) + settingManager.getCLPreviousVersion());
            }
*/
        }
    }

    private void doUpdateClVersion(String pckg) {
        try {
            maDbAdapter = new APPInfoDB(this);
            maDbAdapter.open();
            mAppInfoTable = new AppInfoTable(this);
            mAppInfoTable.open();
            mAppInfoTable.updateAppUpdateAvailableInfo(pckg, Constants.UPDATE_NOT_AVAILABLE);
            mAppInfoTable.updateAppUpdateInfo(packageMap.get(pckg).getAppId(), true);
            settingManager.setCLPreviousVersion(clCurrntVersion);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mAppInfoTable.close();
            maDbAdapter.close();
        }

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){
            case R.id.setting_password_defaultTxt:
                invisiblePwdView();
                settingManager.setEnablePasswordFormat(Constants.PWD_DEFAULT);
                break;
            case R.id.setting_password_clPwdTxt:
                visiblePwdView();
                settingManager.setEnablePasswordFormat(Constants.PWD_CL);
                mSwitch.setChecked(settingManager.getRestrictSettingScreen());
                break;
            case R.id.setting_password_devicePwdTxt:
                invisiblePwdView();
                mSwitch.setEnabled(true);
                mSwitch.setChecked(settingManager.getRestrictSettingScreen());
                doForAndroidPinLock();
                break;
        }
    }


    private void visiblePwdView(){
        if(llPwdView.getVisibility()==View.GONE){
            llPwdView.setVisibility(View.VISIBLE);
            llPwdView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.from_middle));
            pwdViewVisibility();
        }
        if(!mSwitch.isEnabled()){
            mSwitch.setEnabled(true);
        }
    }

    private void invisiblePwdView(){
        if(llPwdView.getVisibility()==View.VISIBLE){
            llPwdView.setVisibility(View.GONE);
        }
        if(mSwitch.isEnabled()){
            mSwitch.setEnabled(false);
        }

    }


    @Override
    public void onBackPressed() {

        if(settingManager.getCLPassword().equalsIgnoreCase("")||settingManager.getCLPassword()==null){
            dfltPwd.setChecked(true);
        }else {
            handleVisibilityInSaveMode();
        }
        overridePendingTransition(R.anim.fade_in,R.anim.shrink_from_bottomleft_to_topright);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_savePwd){
            handleClickSaveBtn();
        }else  if(v.getId()==R.id.btn_CancelPwd){
            if(settingManager.getCLPassword().equalsIgnoreCase("")||settingManager.getCLPassword()==null){
                dfltPwd.setChecked(true);
            }else {
                handleVisibilityInSaveMode();
            }
        }else if(v.getId()==R.id.setting_Cl_update){
            if(packageMap.get(clPckgName).getIsUpdateVersionExist()
                    ==Constants.UPDATE_AVAILABLE){
                doUpdateClVersion(clPckgName);
                Utils.installAPK(SettingScreen.this,clPckgName);
            }
        }else if(v.getId()==R.id.setting_app_clean_up){

            sentEventHomeToAppCleanup();
        }else if(v.getId()==R.id.setting_app_qrCodeScanner){
            openQrcodeScanner();
        }else if(v.getId()==R.id.connect_wifi){
            connectWifi();
        }
    }

    private void openQrcodeScanner() {
        startActivityForResult(new Intent(this, QRCodeScanning.class), 121);
    }

    private void sentEventHomeToAppCleanup() {
        Intent cleanUpaction = new Intent(CLEAN_UP_ACTION);
        sendBroadcast(cleanUpaction);
    }


    private boolean validateField(){
        if(TextUtils.isEmpty(edPwd.getText().toString())){
            edPwd.setError("Enter password.");
            return false;
        }else if(TextUtils.isEmpty(edCpwd.getText().toString())){
            edCpwd.setError("Enter confirm password.");
            return false;
        }else if(!edPwd.getText().toString().equals(edCpwd.getText().toString())){
            Utils.showToast(this,"Password & confirm password should be equal.");
            return false;
        }
        return true;
    }


    private void handleClickSaveBtn(){
        if(savePwdBtn.getTag().equals("save")){
            if(validateField()) {
                settingManager.setClPassword(edPwd.getText().toString());
                Utils.showToast(this, "Password saved successfully.");
                handleVisibilityInSaveMode();
            }
        }else if(savePwdBtn.getTag().equals("edit")){
            handleVisibilityInEditMode();
        }
    }

    private void pwdViewVisibility(){
        if(settingManager.getCLPassword().equals("")){
            handleVisibilityInEditMode();
        }else{
            handleVisibilityInSaveMode();
        }
    }


    private void handleVisibilityInSaveMode(){
        savePwdBtn.setText("Edit");
        savePwdBtn.setTag("edit");
        txtMsg.setText("Password set.");
        txtMsg.setVisibility(View.VISIBLE);
        edCpwd.setVisibility(View.GONE);
        edPwd.setVisibility(View.GONE);
        cancelPwdBtn.setVisibility(View.GONE);
    }

    private void handleVisibilityInEditMode(){
        savePwdBtn.setText("Save");
        savePwdBtn.setTag("save");
        edPwd.setText(settingManager.getCLPassword());
        edCpwd.setText(settingManager.getCLPassword());
        edCpwd.setVisibility(View.VISIBLE);
        edPwd.setVisibility(View.VISIBLE);
        txtMsg.setText("Password set in progress...");
        cancelPwdBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        settingManager.setRestrictSettingScreen(isChecked);
    }

    /**
     * For calling device lock function.
     */
    private void doForAndroidPinLock(){
        boolean active = deviceManger.isAdminActive(compName);
        if (active) {
            settingManager.setEnablePasswordFormat(Constants.PWD_ANDROID);

        }else{
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,getResources().getString(R.string.device_lock_string));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String dataResult = "";
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean active = deviceManger.isAdminActive(compName);
            if (active) {
                // if available then lock
                // deviceManger.lockNow();
                settingManager.setEnablePasswordFormat(Constants.PWD_ANDROID);

            }
        }
        if (requestCode == 121 && resultCode == RESULT_OK) {
            if(data!=null) {
                dataResult = data.getStringExtra("DATA");
                startDeploymentRegistration(dataResult);
            }

        }
    }

    private void startDeploymentRegistration(String dataResult) {
        if(!NetworkStatus.getInstance().isConnected(this)){
            Utils.showToast(this,getResources().getString(R.string.network_error_text));
            return;
        }else{
            try {
                JSONObject jsonObject = new JSONObject(dataResult);
                String deploymentId = jsonObject.has(Constants.KEY_DEPLOYMENT_ID)?jsonObject.optString(Constants.KEY_DEPLOYMENT_ID):"";
                String groupId = jsonObject.has(Constants.KEY_GROUP_ID)?jsonObject.optString(Constants.KEY_GROUP_ID):"";
                String tabletLebel = jsonObject.has(Constants.KEY_TABLET_LEBEl)?jsonObject.optString(Constants.KEY_TABLET_LEBEl):"";

                if(groupId.equals("")){
                    doCallForDeploymentReg(deploymentId);
                }else{
                    doCallForDeploymentGroupReg(deploymentId,groupId);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showToast(SettingScreen.this,getResources().getString(R.string.invalid_json));
            }


        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
            Log.d("Focus debug", "Focus changed !");
        super.onWindowFocusChanged(hasFocus);
            if (!hasFocus) {
                Log.d("Focus debug", "Lost focus !");
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);
                ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                foregroundPkg = UStats.getProcessName(this);
                Log.d("Focus packageName", foregroundPkg );
                if (foregroundPkg != null && (foregroundPkg.equals("com.android.systemui")
                        ||foregroundPkg.equals("com.google.process.gapps")
                        ||foregroundPkg.equals(clPckgName)
                        ||foregroundPkg.equals(clPckgName1)
//                        ||foregroundPkg.equals(clPckgName2)
                        ||foregroundPkg.equals("com.vlingo.midas")
                        ||foregroundPkg.equals("com.google.android.gms"))) {
                    activityManager.moveTaskToFront(getTaskId(), 0);
                    // startActivity(new Intent(this,Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    Log.d("Focus moveTaskToFront ", getTaskId()+"");
                }

           /* if (foregroundPkg != null && !foregroundPkg.equals(clPckgName)&& !isAppClick) {
               // activityManager.moveTaskToFront(getTaskId(), 0);
                startActivity(new Intent(this,Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                Log.d("Focus moveTaskToFront ", getTaskId()+"");
            }*/


            }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                if(settingManager.getCLPassword().equalsIgnoreCase("")||settingManager.getCLPassword()==null){
                    dfltPwd.setChecked(true);
                }else {
                    handleVisibilityInSaveMode();
                }
            }
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void connectWifi() {



        int savedlevel = 0;
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//        wifiManager.addNetwork(conf);

        String networkSSID = null;
        List<ScanResult> scanList = wifiManager.getScanResults();
        for (ScanResult config : scanList) {
//            ssidList.add(config.SSID);
            String capabilities = config.capabilities;
            if (capabilities.toUpperCase().contains("WEP")) {
                // WEP Network
            } else if (capabilities.toUpperCase().contains("WPA")
                    || capabilities.toUpperCase().contains("WPA2")) {
                // WPA or WPA2 Network
            } else {
                // Open Network
                int level = wifiManager.calculateSignalLevel(config.level, 5);
                if (level > savedlevel) {
                    savedlevel = level;
                    networkSSID = config.SSID;
                }
            }
        }

        if (networkSSID == null) {
            Toast.makeText(this, "No Open WIFI found.", Toast.LENGTH_SHORT).show();
            return;
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                if (wifiManager.reconnect()) {
                    Toast.makeText(this, "Connecting to open WIFI (" + networkSSID + ").", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unable to connect. Please try again!!!", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }


    }


    public void wifiClick(View view){
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }


    private void showDialog(){
        if(loadingDialog!=null) {
            loadingDialog.setMessage(getResources().getString(R.string.registeringTxt));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }
    }


    private void hideDialog(){
        if(loadingDialog!=null&&loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Method for group regd.
     * @param deploymentId
     * @param groupId
     */
    private void doCallForDeploymentGroupReg(String deploymentId, String groupId) {
        showDialog();
        String authorizationHeader = "Bearer "+settingManager.getAccessToken();
        String cl_serial_no = settingManager.getCL_SerialNo();

        Call<JsonObject> call = apiInterface.getGroupDeploymentRegCall(authorizationHeader,deploymentId,cl_serial_no,groupId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()&&response.code()==200){
                    JsonObject jsonObject = response.body();
                    if(jsonObject!=null){
                        Utils.showToast(SettingScreen.this,"Deployed successfully");
                        LogUtil.createLog("doCallForDeploymentReg ::",jsonObject.toString());
                    }
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        JSONObject jsonObjec = jObjError.optJSONObject("error");
                        Utils.showToast(SettingScreen.this,response.message()+" : "+jsonObjec.optString("code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtil.createLog("doCallForDeploymentReg onFailure ::",t.toString());
                Utils.showToast(SettingScreen.this,"Something went wrong. Please try again.");
                hideDialog();
            }
        });

    }


    /**
     * Method for deployment regd.
     * @param deploymentId
     */

    private void doCallForDeploymentReg(String deploymentId){
        showDialog();
        String authorizationHeader = "Bearer "+settingManager.getAccessToken();
        String cl_serial_no = settingManager.getCL_SerialNo();

        Call<JsonObject> call = apiInterface.getDeploymentRegCall(authorizationHeader,deploymentId,cl_serial_no);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()&&response.code()==200){
                    JsonObject jsonObject = response.body();
                    if(jsonObject!=null){
                        Utils.showToast(SettingScreen.this,"Deployed successfully");
                        LogUtil.createLog("doCallForDeploymentReg ::",jsonObject.toString());
                    }
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        JSONObject jsonObjec = jObjError.optJSONObject("error");
                        Utils.showToast(SettingScreen.this,response.message()+" : "+jsonObjec.optString("code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtil.createLog("doCallForDeploymentReg onFailure ::",t.toString());
                Utils.showToast(SettingScreen.this,"Something went wrong. Please try again.");
                hideDialog();
            }
        });
    }
}
