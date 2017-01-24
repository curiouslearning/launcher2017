package excelsoft.com.cl_launcher;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import device_admin_utill.CLDeviceManger;
import device_admin_utill.DevicePolicyAdmin;
import preference_manger.SettingManager;
import util.Constants;
import util.Utils;

public class SettingScreen extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener,View.OnClickListener,Switch.OnCheckedChangeListener {

    private DevicePolicyManager deviceManger;
    private ComponentName compName;

    private View llPwdView;
    private RadioButton dfltPwd,clPwd,devicePwd;
    private RadioGroup pwdRadioGrp;
    private Button savePwdBtn,cancelPwdBtn;
    private EditText edPwd,edCpwd;
    private SettingManager settingManager;
    private TextView txtMsg;
    private Switch mSwitch;
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
        cancelPwdBtn = (Button) findViewById(R.id.btn_CancelPwd);
        mSwitch = (Switch) findViewById(R.id.setting_restrictHome_screenTxtSwitch);

        settingManager = SettingManager.getInstance(this);

        compName = new ComponentName(this, DevicePolicyAdmin.class);
        deviceManger = CLDeviceManger.getCLDevicePolicyManager(this);


        if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_DEFAULT)){
            dfltPwd.setChecked(true);
        }else  if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_CL)){
            clPwd.setChecked(true);
            visiblePwdView();
            mSwitch.setChecked(settingManager.getRestrictSettingScreen());

        }else  if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_ANDROID)){
            devicePwd.setChecked(true);
        }




        pwdRadioGrp.setOnCheckedChangeListener(this);
        mSwitch.setOnCheckedChangeListener(this);
        savePwdBtn.setOnClickListener(this);
        cancelPwdBtn.setOnClickListener(this);


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
                settingManager.setEnablePasswordFormat(Constants.PWD_ANDROID);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.shrink_from_bottomleft_to_topright);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_savePwd){
            handleClickSaveBtn();
        }else  if(v.getId()==R.id.btn_CancelPwd){
            handleVisibilityInSaveMode();
        }
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
        txtMsg.setText("Password already set.");
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
            deviceManger.lockNow();

        }else{
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,getResources().getString(R.string.device_lock_string));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean active = deviceManger.isAdminActive(compName);
            if (active) {
                // if available then lock
                deviceManger.lockNow();
            }
        }
    }
}
