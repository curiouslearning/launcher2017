package excelsoft.com.cl_launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import preference_manger.SettingManager;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
    }

    public void onClick(View v){
        SettingManager.getInstance(SplashActivity.this).setGotIt(true);
        finish();
    }
}
