package preference_manger;

import android.content.Context;
import android.content.SharedPreferences;

import util.Constants;

/**
 * Created by IMFCORP\alok.acharya on 12/12/16.
 */

public class SettingManager{

    private static final String PREFERENCE_KEY = "cl_launcher_preference";
    private final String LAST_SYNC_TIME_KEY = "last_sync_time";
    private final String CL_CREDENTIAL_KEY = "credential";
    private final String CL_SERIAL_NO_KEY = "serialNo";
    private final String CL_ACCESS_TOKEN_KEY = "access_token";
    private final String CL_ACCESS_TOKEN_EXPIRE_KEY = "access_token_expire";
    private final String ENABLE_PWD_FORMAT_KEY = "cl_pwd_format";
    private final String RESTRICT_SETTING_SCREEN = "restrict_setting_screen";
    private final String GOT_IT_KEY = "got_it";
    private final String CL_PWD_KEY = "cl_pwd";
    private final String CL_MANIFEST_VERSION_KEY = "manifest_version_key";
    private final String CL_PREVIOUS_VERSION = "prev_version_key";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private SettingManager(Context mContext){
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private static SettingManager _instance = null;

    public static SettingManager getInstance(Context _context){
        if(_instance==null){
            return  new SettingManager(_context);
        }
        return _instance;
    }


    public void setLastSyncTime(long lastSyncTime){
        mEditor.putLong(LAST_SYNC_TIME_KEY,lastSyncTime);
        mEditor.commit();
    }


    public long getLastSyncTime(){
        return mSharedPreferences.getLong(LAST_SYNC_TIME_KEY,0L);
    }


    public String getCLPassword(){
        return mSharedPreferences.getString(CL_PWD_KEY,"");
    }

    public void setClPassword(String pwd){
        mEditor.putString(CL_PWD_KEY,pwd);
        mEditor.commit();
    }


    public String getEnablePasswordFormat(){
        return mSharedPreferences.getString(ENABLE_PWD_FORMAT_KEY, Constants.PWD_DEFAULT);
    }

    public void setEnablePasswordFormat(String format){
        mEditor.putString(ENABLE_PWD_FORMAT_KEY,format);
        mEditor.commit();
    }

    public boolean getRestrictSettingScreen(){
        return mSharedPreferences.getBoolean(RESTRICT_SETTING_SCREEN, false);
    }

    public void setRestrictSettingScreen(boolean status){
        mEditor.putBoolean(RESTRICT_SETTING_SCREEN,status);
        mEditor.commit();
    }

    public boolean getGotIt(){
        return mSharedPreferences.getBoolean(GOT_IT_KEY, false);
    }

    public void setGotIt(boolean status){
        mEditor.putBoolean(GOT_IT_KEY,status);
        mEditor.commit();
    }


    public String getCL_Credential(){
        return mSharedPreferences.getString(CL_CREDENTIAL_KEY, "");
    }

    public void setCL_Credential(String credential){
        mEditor.putString(CL_CREDENTIAL_KEY,credential);
        mEditor.commit();
    }

    public String getCL_SerialNo(){
        return mSharedPreferences.getString(CL_SERIAL_NO_KEY, "");
    }

    public void setCL_SerialNo(String serialNo){
        mEditor.putString(CL_SERIAL_NO_KEY,serialNo);
        mEditor.commit();
    }

    public String getAccessToken(){
        return mSharedPreferences.getString(CL_ACCESS_TOKEN_KEY, "");
    }

    public void setAccessToken(String token){
        mEditor.putString(CL_ACCESS_TOKEN_KEY,token);
        mEditor.commit();
    }

    public String getAccessTokenExpireTime(){
        return mSharedPreferences.getString(CL_ACCESS_TOKEN_EXPIRE_KEY, "");
    }

    public void setAccessTokenExpireTime(String time){
        mEditor.putString(CL_ACCESS_TOKEN_EXPIRE_KEY,time);
        mEditor.commit();
    }


    public String getManifestVersion(){
        return mSharedPreferences.getString(CL_MANIFEST_VERSION_KEY, "");
    }

    public void setManifestVersion(String version){
        mEditor.putString(CL_MANIFEST_VERSION_KEY,version);
        mEditor.commit();
    }

    public String getCLPreviousVersion(){
        return mSharedPreferences.getString(CL_PREVIOUS_VERSION,"0");
    }

    public void setCLPreviousVersion(String version){
        mEditor.putString(CL_PREVIOUS_VERSION,version);
        mEditor.commit();
    }


}
