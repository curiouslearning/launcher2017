package device_admin_utill;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by IMFCORP\alok.acharya on 23/1/17.
 */

public class CLDeviceManger {
    private ComponentName compName;
    private static DevicePolicyManager deviceManger;
    private static CLDeviceManger instance;
    private CLDeviceManger(){

    }
    public static CLDeviceManger getInstance(){
        if(instance==null){
            return new CLDeviceManger();
        }else{
            return instance;
        }
    }

    public static DevicePolicyManager getCLDevicePolicyManager(Context context){
        if(deviceManger==null){
            deviceManger = (DevicePolicyManager)context.getSystemService(
                    Context.DEVICE_POLICY_SERVICE);
            return deviceManger;
        }else{
            return deviceManger;
        }
    }

    public void checkForActiveAdminAndLock(Context context){
        compName = new ComponentName(context, DevicePolicyAdmin.class);
        if(deviceManger.isAdminActive(compName)){
            deviceManger.lockNow();
        }
    }
}
