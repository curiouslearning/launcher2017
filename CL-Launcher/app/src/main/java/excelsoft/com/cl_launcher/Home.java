/*
 * This file has been modified from original licensed file by Hans Adrian
 */

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package excelsoft.com.cl_launcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import adapter.AppInfoAdapter;
import apihandler.ApiClient;
import apihandler.ApiConstant;
import apihandler.ApiInterface;
import apihandler.NetworkStatus;
import backgroundservice.AppInstalationService;
import backgroundservice.AppUsageAlarmReceiver;
import backgroundservice.AppUsageSchedulingService;
import backgroundservice.InAppDataCollectionReceiver;
import backgroundservice.LocationFetchingService;
import database.APPInfoDB;
import database.AppInfoTable;
import device_admin_utill.CLDeviceManger;
import model.AppInfoModel;
import preference_manger.SettingManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.Constants;
import util.GridSpacingItemDecoration;
import util.HomeKeyLocker;
import util.LogUtil;
import util.UStats;
import util.Utils;

import static adapter.AppInfoAdapter.appUpdateTime;
import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;
import static permission_manager.PermissionHandler.checkIfAlreadyhavePermission;
import static permission_manager.PermissionHandler.requestForSpecificPermission;
import static util.Utils.isRooted;
import static util.Utils.showToast;

@SuppressLint("NewApi") // To remove error about pressing home and back
public class Home extends BaseActivity implements View.OnClickListener{
    /**
     * Tag used for logging errors.
     */
    private static final String LOG_TAG = "Home";

    /**
     * Keys during freeze/thaw.
     */
    private static final String KEY_SAVE_GRID_OPENED = "grid.opened";

    private static final String DEFAULT_FAVORITES_PATH = "etc/favorites.xml";

    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_CLASS = "class";


    // Identifiers for option menu items
    private static final int MENU_WALLPAPER_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_SEARCH = MENU_WALLPAPER_SETTINGS + 1;
    private static final int MENU_SETTINGS = MENU_SEARCH + 1;

    /**
     * Maximum number of recent tasks to query.
     */
    private static final int MAX_RECENT_TASKS = 20;

    private static boolean mWallpaperChecked;
    private static ArrayList<ApplicationInfo> mApplicationsList = new ArrayList<>();
    private static LinkedList<ApplicationInfo> mFavorites;
    private static boolean isSettingClick = false;
    private static boolean isExitClick;

    private final BroadcastReceiver mWallpaperReceiver = new WallpaperIntentReceiver();
    private final BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();
    private final BroadcastReceiver mInAppDataCollectionReceiver = new InAppDataCollectionReceiver();
    private final BroadcastReceiver mAppReloadReceiver = new ApplicationsLoadReceiver();



   /* private GridView mGrid;
    private ApplicationsAdapter applicationsAdapter;*/

    private LayoutAnimationController mShowLayoutAnimation;
    private LayoutAnimationController mHideLayoutAnimation;

    private boolean mBlockAnimation;

    private boolean mHomeDown;
    private boolean mBackDown;

    //private View mShowApplications;
    //private CheckBox mShowApplicationsCheck;

    //private ApplicationsStackLayout mApplicationsStack;

    private Animation mGridEntry;
    private Animation mGridExit;

    private Location mLocation;
    //private AddressResultReceiver mResultReceiver;
    private PopupWindow rightHandPopUp;
    private ImageView refreshImg,exitImg,menuImg;
    private TextView settingMenu,shareMenu,helpMenu;

    WindowManager manager;
    customViewGroup view;
    AppUsageAlarmReceiver mAppUsageAlarmReceiver;
    private boolean grantedAllPermission = false;
    private SettingManager settingManager;
    private ApiInterface apiService;
    private ProgressDialog loadingDialog;
    private AppInfoAdapter appInfoAdapter;
    private ArrayList<AppInfoModel> appInfoList = new ArrayList<>();
    private RecyclerView mRcRecyclerView;
    private GridLayoutManager lLayout;
    // private DBAdapter mDbAdapter;
    private APPInfoDB appInfoDB;
    private AppInfoTable mAppInfoTable;
    private int initialAppInfoCountFromDb = 0;
    public static HashMap<String,AppInfoModel > packageMap = new HashMap<>();
    private HomeKeyLocker mHomeKeyLocker;
    private Timer timerTask = null;
    public static final String clPckgName1 = "excelsoft.com.cl_launcher";
    public static final String clPckgName = "com.excelsoft.cl-launcher";
//    public static final String clPckgName2 ="com.android.launcher";

    // HashMap<Long,AppInfoModel> downLoadMap = new HashMap<>();
    // HashMap<Long,Integer> downLoadMapPosition = new HashMap<>();
    // HashMap<String,Long> downLoadIdMap = new HashMap<>();
    private ArrayList<AppInfoModel> cleanupMap;
    private DownloadManager downloadManager;
    private static final long NOTIFY_DELAY_TIME = 1000L;
    private IntentFilter cleanupFilter;
    public static HashMap<String,AppInfoModel > packageFilterMap = new HashMap<>();
    public static boolean isAppClick = false;
    String foregroundPkg ;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        allFunctionalInitiationAction();

    }



    private void allWidgetInit(){
        setContentView(R.layout.home);
       // clPckgName = getPackageName();
        isExitClick = false;
        screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
        menuImg = (ImageView) findViewById(R.id.img_context_menu);
        exitImg = (ImageView) findViewById(R.id.img_exit_menu);
        refreshImg = (ImageView) findViewById(R.id.img_refresh_menu);
        loadingDialog = new ProgressDialog(this);

        menuImg.setOnClickListener(this);
        exitImg.setOnClickListener(this);
        refreshImg.setOnClickListener(this);
        settingManager = SettingManager.getInstance(this);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

        cleanupFilter = new IntentFilter(SettingScreen.CLEAN_UP_ACTION);

    }


    private void allFunctionalInitiationAction(){
        allWidgetInit();
        registerIntentReceivers();
        //  setDefaultWallpaper();
        bindApplications();
        requestAllPermission();
        // bindFavorites(true);
        // bindRecents();
        //  bindButtons();
        popupSetup();
        mGridEntry = AnimationUtils.loadAnimation(this, R.anim.grid_entry);
        mGridExit = AnimationUtils.loadAnimation(this, R.anim.grid_exit);
    }


    private void popupSetup(){

        rightHandPopUp = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightHandPopUp
                .setAnimationStyle(R.style.Animations_PopUpMenu_Right);
        rightHandPopUp.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_bag));
        rightHandPopUp.setOutsideTouchable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.option_menu,null);
        settingMenu = (TextView) v. findViewById(R.id.menu_setting);
        shareMenu = (TextView)v. findViewById(R.id.menu_share);
        helpMenu = (TextView)v. findViewById(R.id.menu_help);

        settingMenu.setOnClickListener(this);
        shareMenu.setOnClickListener(this);
        helpMenu.setOnClickListener(this);

        rightHandPopUp.setContentView(v);
    }


    private void showPopup(View views){

        rightHandPopUp.showAtLocation(
                views,
                Gravity.TOP | Gravity.RIGHT,
                30,
                (int)getResources().getDimension(R.dimen.righthand_popup_y_pos));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 101){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                LogUtil.createLog("Home","onRequestPermissionsResult granted");
                Utils.createCLAPPDirectory();
                preventStatusBarWithCheckingPermission();
                openDBandLoadApp();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                LogUtil.createLog("Home","onRequestPermissionsResult denied");
            }
            return;
        }
    }

    private void _initUsageStateByCheckingPermission(){
        //mResultReceiver = new AddressResultReceiver(new Handler());
        // startLocationService();
        callUsageFunction();
    }


    private void startOverlay(){
        if(!settingManager.getGotIt()){
            startActivity(new Intent(this,SplashActivity.class));
        }
    }

    private void requestAllPermission(){

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkIfAlreadyhavePermission(this)) {
                requestForSpecificPermission(this);
            } else{
                Utils.createCLAPPDirectory();
                preventStatusBarWithCheckingPermission();
                openDBandLoadApp();
            }
//        }else{
//            Utils.createCLAPPDirectory();
//            preventStatusBarWithCheckingPermission();
//            openDBandLoadApp();
//        }


    }

    private void openDBandLoadApp(){
        appInfoDB = new APPInfoDB(this);

        appInfoDB.open();
        mAppInfoTable = new AppInfoTable(this);
        mAppInfoTable.open();
        loadApplications();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            getWindow().closeAllPanels();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the callback for the cached drawables or we leak
        // the previous Home screen on orientation change
        final int count = mApplicationsList.size();
        for (int i = 0; i < count; i++) {
            mApplicationsList.get(i).icon.setCallback(null);
        }

        unregisterReceiver(mWallpaperReceiver);
        unregisterReceiver(mApplicationsReceiver);
        unregisterReceiver(cleanUpReciever);
        unregisterReceiver(downLoadreceiver);
        unregisterReceiver(mAppReloadReceiver);
        unregisterReceiver(listenRootedMsg);

        stopLocationService();

        //enable dragging of status bar
        if(manager!=null&&view!=null)
            manager.removeView(view);

        unregisterReceiver(mScreenStateReceiver);
        unregisterReceiver(mInAppDataCollectionReceiver);


        if(mAppInfoTable!=null)
            mAppInfoTable.close();
        if(appInfoDB!=null)
            appInfoDB.close();

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        if(grantedAllPermission){
            checkLocationSettings();
            //  startOverlay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  bindRecents();
        isAppClick=false;
        registerReceiver(mScreenStateReceiver, screenStateFilter);
        registerReceiver(downLoadreceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Log.i(TAG,"onResume called");
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        final boolean opened = state.getBoolean(KEY_SAVE_GRID_OPENED, false);
        if (opened) {
            showApplications(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SAVE_GRID_OPENED, mRcRecyclerView.getVisibility() == View.VISIBLE);
    }

    /**
     * Registers various intent receivers. The current implementation registers
     * only a wallpaper intent receiver to let other applications change the
     * wallpaper.
     */
    private void registerIntentReceivers() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
        registerReceiver(mWallpaperReceiver, filter);

        filter = new IntentFilter(ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(mApplicationsReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Constants.ACTION_IN_APP_RECORD_ONE);
        filter.addAction(Constants.ACTION_IN_APP_RECORD_TWO);
        registerReceiver(mInAppDataCollectionReceiver,filter);
        registerReceiver(cleanUpReciever,cleanupFilter);

        filter = new IntentFilter(Constants.ACTION_APP_RELOAD);
        registerReceiver(mAppReloadReceiver,filter);


        filter = new IntentFilter(Constants.ACTION_ROOTED);
        registerReceiver(listenRootedMsg,filter);
    }

    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    private void bindApplications() {
        if (mRcRecyclerView == null) {
            mRcRecyclerView = (RecyclerView) findViewById(R.id.all_apps);
        }
        int spanCount = Utils.calculateNoOfColumns(this);
        lLayout = new GridLayoutManager(this,spanCount );
        mRcRecyclerView.setLayoutManager(lLayout);
        mRcRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, Utils.dpToPx(this,10), true));
        mRcRecyclerView.setItemAnimator(new DefaultItemAnimator());
        appInfoAdapter = new AppInfoAdapter(this, appInfoList);
        appInfoAdapter.setOnItemClickListener(onItemClickListener);
        mRcRecyclerView.setAdapter(appInfoAdapter);
        showApplications(true);
       /* if (mApplicationsStack == null) {
            mApplicationsStack = (ApplicationsStackLayout) findViewById(R.id.faves_and_recents);
        }*/
    }

    /**
     * Binds actions to the various buttons.
     */
    private void bindButtons() {
        /*mShowApplications = findViewById(R.id.show_all_apps);
        mShowApplications.setOnClickListener(new ShowApplications());
        mShowApplicationsCheck = (CheckBox) findViewById(R.id.show_all_apps_check);*/

        //  mGrid.setOnItemClickListener(new ApplicationLauncher());
    }

    /**
     * When no wallpaper was manually set, a default wallpaper is used instead.
     */
    private void setDefaultWallpaper() {
      /*  if (!mWallpaperChecked) {
            Drawable wallpaper = peekWallpaper();
            if (wallpaper == null) {
                try {
                    clearWallpaper();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to clear wallpaper " + e);
                }
            } else {
                getWindow().setBackgroundDrawable(new ClippedDrawable(wallpaper));
            }
           mWallpaperChecked = true;
        }*/
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(Home.this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        //getWindow().setBackgroundDrawable(new ClippedDrawable(getWallpaper()));
        getWindow().setBackgroundDrawable(wallpaperDrawable);
    }

    /**
     * Refreshes the favorite applications stacked over the all apps button.
     * The number of favorites depends on the user.
     */
   /* private void bindFavorites(boolean isLaunching) {
        if (!isLaunching || mFavorites == null) {

            if (mFavorites == null) {
                mFavorites = new LinkedList<ApplicationInfo>();
            } else {
                mFavorites.clear();
            }
            //mApplicationsStack.setFavorites(mFavorites);

            FileReader favReader;

            // Environment.getRootDirectory() is a fancy way of saying ANDROID_ROOT or "/system".
            final File favFile = new File(Environment.getRootDirectory(), DEFAULT_FAVORITES_PATH);
            try {
                favReader = new FileReader(favFile);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Couldn't find or open favorites file " + favFile);
                return;
            }

            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager packageManager = getPackageManager();

            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(favReader);

                beginDocument(parser, TAG_FAVORITES);

                ApplicationInfo info;

                while (true) {
                    nextElement(parser);

                    String name = parser.getName();
                    if (!TAG_FAVORITE.equals(name)) {
                        break;
                    }

                    final String favoritePackage = parser.getAttributeValue(null, TAG_PACKAGE);
                    final String favoriteClass = parser.getAttributeValue(null, TAG_CLASS);

                    final ComponentName cn = new ComponentName(favoritePackage, favoriteClass);
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    info = getApplicationInfo(packageManager, intent);
                    if (info != null) {
                        info.intent = intent;
                        mFavorites.addFirst(info);
                    }
                }
            } catch (XmlPullParserException e) {
                Log.w(LOG_TAG, "Got exception parsing favorites.", e);
            } catch (IOException e) {
                Log.w(LOG_TAG, "Got exception parsing favorites.", e)
            }
        }

        //mApplicationsStack.setFavorites(mFavorites);
    }*/

    private static void beginDocument(XmlPullParser parser, String firstElementName)
            throws XmlPullParserException, IOException {

        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                    ", expected " + firstElementName);
        }
    }

    private static void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
            // Empty
        }
    }

    /**
     * Refreshes the recently launched applications stacked over the favorites. The number
     * of recents depends on how many favorites are present.
     */
   /* private void bindRecents() {
        final PackageManager manager = getPackageManager();
        final ActivityManager tasksManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTasks = tasksManager.getRecentTasks(
                MAX_RECENT_TASKS, 0);

        final int count = recentTasks.size();
        final ArrayList<ApplicationInfo> recents = new ArrayList<ApplicationInfo>();

        for (int i = count - 1; i >= 0; i--) {
            final Intent intent = recentTasks.get(i).baseIntent;

            if (Intent.ACTION_MAIN.equals(intent.getAction()) &&
                    !intent.hasCategory(Intent.CATEGORY_HOME)) {

                ApplicationInfo info = getApplicationInfo(manager, intent);
                if (info != null) {
                    info.intent = intent;
                    if (!mFavorites.contains(info)) {
                        recents.add(info);
                    }
                }
            }
        }

        // mApplicationsStack.setRecents(recents);
    }*/

    private static ApplicationInfo getApplicationInfo(PackageManager manager, Intent intent) {
        final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);

        if (resolveInfo == null) {
            return null;
        }

        final ApplicationInfo info = new ApplicationInfo();
        final ActivityInfo activityInfo = resolveInfo.activityInfo;
        info.icon = activityInfo.loadIcon(manager);
        if (info.title == null || info.title.length() == 0) {
            info.title = activityInfo.loadLabel(manager);
        }
        if (info.title == null) {
            info.title = "";
        }
        return info;
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//    }




    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("Focus debug", "Focus changed !");
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Log.d("Focus debug", "Lost focus !");
            mBackDown = mHomeDown = false;
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            foregroundPkg = UStats.getProcessName(this);
            Log.d("Focus packageName", foregroundPkg);
            if (foregroundPkg != null && (foregroundPkg.equals("com.android.systemui")
                    ||foregroundPkg.equals("com.google.process.gapps")
                    ||foregroundPkg.equals(clPckgName)
                    ||foregroundPkg.equals(clPckgName1)
//                    ||foregroundPkg.equals(clPckgName2)
                    ||foregroundPkg.equals("com.vlingo.midas")
                    ||foregroundPkg.equals("com.google.android.gms"))
                    && !isAppClick) {
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    mBackDown = true;
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    mHomeDown = true;
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (!event.isCanceled()) {
                        // Do BACK behavior.
                        if (mRcRecyclerView.getVisibility() == View.VISIBLE) {
                            hideApplications();
                        }
                    }
                    mBackDown = true;
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    if (!event.isCanceled()) {
                        // Do HOME behavior.
                    }
                    mHomeDown = true;
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
                .setIcon(android.R.drawable.ic_menu_gallery)
                .setAlphabeticShortcut('W');
        menu.add(0, MENU_SEARCH, 0, R.string.menu_search)
                .setIcon(android.R.drawable.ic_search_category_default)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);
        menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings)
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setIntent(new Intent(Settings.ACTION_SETTINGS));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_WALLPAPER_SETTINGS:
                changeWallpaper();
                return true;
            case MENU_SEARCH:
                onSearchRequested();
                return true;
            case android.R.id.home:
                return false;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void changeWallpaper() {
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        startActivity(Intent.createChooser(pickWallpaper, getString(R.string.menu_wallpaper)));
    }

    /**
     * Loads the list of installed applications in mApplicationsList.
     */
    private void loadApplications() {
      /* if (!isLaunching && mApplicationsList != null) {
            return;
        }
*/
        // if(UStats.getProcessName(this).equals(clPckgName))

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideDialog();
                if(!(appInfoList.size()>0)) {
                    initDeviceRegistrationProcess();
                }else{
                    //  appInfoAdapter.notifyDataSetChanged();
                    appInfoAdapter.setDataList(appInfoList);
                    appInfoAdapter.notifyDataSetChanged();
                    addDataIntoMap(appInfoList);
                    checkDownLoadStatusAndProcess(appInfoList);
                }
                isAppClick=false;
            }

            @Override
            protected Void doInBackground(Void... params) {

                PackageManager manager = getPackageManager();

                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
                Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

                if (apps != null) {
                    try {
                        appInfoList = mAppInfoTable.getAppInfo(apps,manager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();

    }

    private void addDataIntoMap(ArrayList<AppInfoModel> appInfoList) {
        for (AppInfoModel model:appInfoList) {
            packageMap.put(model.getAppPckageName(),model);
            packageFilterMap.put(model.getAppPckageName(),model);
        }
    }



    /**
     * Shows all of the applications by playing an animation on the grid.
     */
    private void showApplications(boolean animate) {
        if (mBlockAnimation) {
            return;
        }
        mBlockAnimation = true;

        //mShowApplicationsCheck.toggle();

        if (mShowLayoutAnimation == null) {
            mShowLayoutAnimation = AnimationUtils.loadLayoutAnimation(
                    this, R.anim.show_applications);
        }

        // This enables a layout animation; if you uncomment this code, you need to
        // comment the line mGrid.startAnimation() below
//        mGrid.setLayoutAnimationListener(new ShowGrid());
//        mGrid.setLayoutAnimation(mShowLayoutAnimation);
//        mGrid.startLayoutAnimation();

        if (animate) {
            //  mGridEntry.setAnimationListener(new ShowGrid());
            //  mGrid.startAnimation(mGridEntry);
        }

        mRcRecyclerView.setVisibility(View.VISIBLE);

        if (!animate) {
            mBlockAnimation = false;
        }

        // ViewDebug.startHierarchyTracing("Home", mGrid);
    }

    /**
     * Hides all of the applications by playing an animation on the grid.
     */
    private void hideApplications() {
        if (mBlockAnimation) {
            return;
        }
        mBlockAnimation = true;

        //  mShowApplicationsCheck.toggle();

        if (mHideLayoutAnimation == null) {
            mHideLayoutAnimation = AnimationUtils.loadLayoutAnimation(
                    this, R.anim.hide_applications);
        }

        mGridExit.setAnimationListener(new HideGrid());
        mRcRecyclerView.startAnimation(mGridExit);
        mRcRecyclerView.setVisibility(View.INVISIBLE);
        // mShowApplications.requestFocus();

        // This enables a layout animation; if you uncomment this code, you need to
        // comment the line mGrid.startAnimation() above
//        mGrid.setLayoutAnimationListener(new HideGrid());
//        mGrid.setLayoutAnimation(mHideLayoutAnimation);
//        mGrid.startLayoutAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_context_menu:
                showPopup(v);
                break;
            case R.id.img_exit_menu:
                isExitClick = true;
                handleLauncherExit();
                break;
            case R.id.img_refresh_menu:
                if(NetworkStatus.getInstance().isConnected(this)){
                    Utils.showToast(Home.this,"Sync in progress...");
                    doCallForManifest(settingManager.getAccessToken());
                }else{
                    Utils.showToast(this,getResources().getString(R.string.network_error_text));
                }
                doCallForCollectInAppData();
                break;

            case R.id.menu_setting:
                isSettingClick = true;
                dismisPopup();
                callingSettingScreen();
                break;
            case R.id.menu_share:
                dismisPopup();
                Utils.openBluetooth(Home.this);
                break;
            case R.id.menu_help:
                dismisPopup();
                callHelpScreen();
                break;
        }
    }

    private void doCallForCollectInAppData() {
        Intent service = new Intent(this, AppUsageSchedulingService.class);
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(this, service);
    }


    private void callHelpScreen(){
        startActivity(new Intent(this,HelpActivity.class));
    }

    @Override
    public void onBackPressed() {
        dismisPopup();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return true;
    }

    private  void dismisPopup(){
        if(rightHandPopUp!=null && rightHandPopUp.isShowing()){
            rightHandPopUp.dismiss();
        }
    }

    private void callingSettingScreen(){
        if(settingManager.getRestrictSettingScreen() && settingManager.getEnablePasswordFormat().equals(Constants.PWD_CL)) {

            showPasswordAlert(Constants.OPEN_SETTING,false);
        }else if(settingManager.getRestrictSettingScreen() && settingManager.getEnablePasswordFormat().equals(Constants.PWD_ANDROID)) {
            doAndroidPassCodeLock();
        }else{
            handleOpenSettingScreen();
        }
    }

    private void handleOpenSettingScreen(){
        Intent intent = new Intent(this, SettingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.grow_from_topright_to_bottomleft, R.anim.fade_out);
    }

    public void removeItemAndUninstallSilently(final int position) {
        AppInfoModel model = appInfoList.get(position);
        LogUtil.createLog("Start Remove",model.getAppPckageName());
        if(isRooted()) {
            AppInstalationService.startActionUninstall(Home.this,model.getAppPckageName());
        }

       /* new Handler().post(new Runnable() {
            @Override
            public void run() {
                appInfoList.remove(position);
                appInfoAdapter.notifyItemRemoved(position);
            }
        });*/

    }


    /**
     * Receives intents from other applications to change the wallpaper.
     */
    private class WallpaperIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(Home.this);
            final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            //getWindow().setBackgroundDrawable(new ClippedDrawable(getWallpaper()));
            getWindow().setBackgroundDrawable(wallpaperDrawable);
        }
    }

    /**
     * Receives notifications when applications are added/removed.
     */
    private class ApplicationsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                LogUtil.createLog("onReceive  : ",intent.getAction());
                if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED)){
                    String added_package = intent.getData().toString().split(":")[1];
                    LogUtil.createLog("added_package",added_package);
                    LogUtil.createLog("alok","added_package ::"+added_package);

                    if(packageMap.get(added_package)!=null && packageMap.get(added_package).getIsUpdateVersionExist()==Constants.UPDATE_AVAILABLE) {
                        mAppInfoTable.updateAppUpdateAvailableInfo(added_package, Constants.UPDATE_NOT_AVAILABLE);
                        mAppInfoTable.updateAppUpdateInfo(packageMap.get(added_package).getAppId(), true);
                    }

                    if(!added_package.equals(clPckgName) /*&& !added_package.equals(clPckgName1)*/){
                        //  stopTimer();
                        loadApplications();
                    }
                }
            }

            //  bindApplications();
            //  bindRecents();
            //  bindFavorites(false);
        }
    }



    /**
     * Receives notifications when applications are added/removed.
     */
    private class ApplicationsLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                LogUtil.createLog("onReceive  : ",intent.getAction());
                if(intent.getAction().equalsIgnoreCase(Constants.ACTION_APP_RELOAD)){
                    loadApplications();
                }
            }
        }
    }



    /**
     * GridView adapter to show the list of all installed applications.
     */
    private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
        private Rect mOldBounds = new Rect();

        public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
            super(context, 0, apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ApplicationInfo info = mApplicationsList.get(position);

            if (convertView == null) {
                final LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item_icon_text, parent, false);
            }

           /* Drawable icon = info.icon;

            if (!info.filtered) {
                final Resources resources = getContext().getResources(); //
                //int width = (int) resources.getDimension(android.R.dimen.app_icon_size); // 42;
               // int height = (int) resources.getDimension(android.R.dimen.app_icon_size); // 42;
                int width = (int) resources.getDimension(android.R.dimen.app_icon_size); // 42;
                int height = (int) resources.getDimension(android.R.dimen.app_icon_size); // 42;
                final int iconWidth = icon.getIntrinsicWidth();
                final int iconHeight = icon.getIntrinsicHeight();

                if (icon instanceof PaintDrawable) {
                    PaintDrawable painter = (PaintDrawable) icon;
                    painter.setIntrinsicWidth(width);
                    painter.setIntrinsicHeight(height);
                }

                if (width > 0 && height > 0 && (width < iconWidth || height < iconHeight)) {
                    final float ratio = (float) iconWidth / iconHeight;

                    if (iconWidth > iconHeight) {
                        height = (int) (width / ratio);
                    } else if (iconHeight > iconWidth) {
                        width = (int) (height * ratio);
                    }

                    final Bitmap.Config c =
                            icon.getOpacity() != PixelFormat.OPAQUE ?
                                    Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    final Bitmap thumb = Bitmap.createBitmap(width, height, c);
                    final Canvas canvas = new Canvas(thumb);
                    canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
                    // Copy the old bounds to restore them later
                    // If we were to do oldBounds = icon.getBounds(),
                    // the call to setBounds() that follows would
                    // change the same instance and we would lose the
                    // old bounds
                    mOldBounds.set(icon.getBounds());
                    icon.setBounds(0, 0, width, height);
                    icon.draw(canvas);
                    icon.setBounds(mOldBounds);
                    icon = info.icon = new BitmapDrawable(thumb);
                    info.filtered = true;
                }
            }

            final TextView textView = (TextView) convertView.findViewById(R.id.label);
            textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
            textView.setText(info.title);
            textView.setTextColor(getResources().getColor(R.color.black));*/

            final TextView textView = (TextView) convertView.findViewById(R.id.text);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            textView.setText(info.title);
            imageView.setImageDrawable(info.icon);



            return convertView;
        }
    }

    /**
     * Shows and hides the applications grid view.
     */
    private class ShowApplications implements View.OnClickListener {
        public void onClick(View v) {
            if (mRcRecyclerView.getVisibility() != View.VISIBLE) {
                showApplications(true);
            } else {
                hideApplications();
            }
        }
    }

    /**
     * Hides the applications grid when the layout animation is over.
     */
    private class HideGrid implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mBlockAnimation = false;
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Shows the applications grid when the layout animation is over.
     */
    private class ShowGrid implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mBlockAnimation = false;
            // ViewDebug.stopHierarchyTracing();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Starts the selected activity/application in the grid view.
     */
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
            startActivity(app.intent);
        }
    }

    /**
     * When a drawable is attached to a View, the View gives the Drawable its dimensions
     * by calling Drawable.setBounds(). In this application, the View that draws the
     * wallpaper has the same size as the screen. However, the wallpaper might be larger
     * that the screen which means it will be automatically stretched. Because stretching
     * a bitmap while drawing it is very expensive, we use a ClippedDrawable instead.
     * This drawable simply draws another wallpaper but makes sure it is not stretched
     * by always giving it its intrinsic dimensions. If the wallpaper is larger than the
     * screen, it will simply get clipped but it won't impact performance.
     */
    private class ClippedDrawable extends Drawable {
        private final Drawable mWallpaper;

        public ClippedDrawable(Drawable wallpaper) {
            mWallpaper = wallpaper;
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);

            int wallpaperWidth = mWallpaper.getIntrinsicWidth();
            int wallpaperHeight = mWallpaper.getIntrinsicHeight();
            int boundWidth = right - left;
            int boundHeight = bottom - top;
            int centerLeft;
            int centerTop;

            if (wallpaperWidth > boundWidth) {
                centerLeft = left - ((wallpaperWidth - boundWidth)/2);
            }
            else {
                centerLeft = left;
            }

            if (wallpaperHeight > boundHeight) {
                centerTop = top - ((wallpaperHeight - boundHeight)/2);
            }
            else {
                centerTop = top;
            }

            mWallpaper.setBounds(centerLeft, centerTop, centerLeft + wallpaperWidth,
                    centerTop + wallpaperHeight);
        }

        public void draw(Canvas canvas) {
            mWallpaper.draw(canvas);
        }

        public void setAlpha(int alpha) {
            mWallpaper.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            mWallpaper.setColorFilter(cf);
        }

        public int getOpacity() {
            return mWallpaper.getOpacity();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Home","onActivityResult requestCode :"+requestCode);
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 0x1:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        LogUtil.createLog("RESULT_OK", "User agreed to make required location settings changes.");
                        startLocationService();
                        break;
                    case Activity.RESULT_CANCELED:
                        LogUtil.createLog("RESULT_CANCELED", "User chose not to make required location settings changes.");
                        break;

                }
                break;
            case Constants.PREVENT_STATUS_BAR_PERMISSION:
                preventStatusBarExpansion(Home.this);
                _initUsageStateByCheckingPermission();
                break;
            case Constants.USAGE_STATE_PERMISSION:
                mAppUsageAlarmReceiver = new AppUsageAlarmReceiver();
                mAppUsageAlarmReceiver.setAlarm(this);
                grantedAllPermission=true;
                break;

        }
    }


    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationService();

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(Home.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }




    private void startLocationService(){
        LogUtil.createLog("Launcher","call start service");
        startService(new Intent(Home.this, LocationFetchingService.class)
        );
    }


    private void stopLocationService(){
        stopService(new Intent(Home.this, LocationFetchingService.class));
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Show a toast message if an address was found.
            if (resultCode == Constants.LOCATION_RESULT_KEY) {
                mLocation = resultData.getParcelable(Constants.LOCATION_KEY);

            }
        }
    }





    /**
     *Calling usage setting class if app is not get permitted.
     */
    private void callUsageFunction(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (UStats.getInstance(this).getUsageStatsList(this).isEmpty()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent,Constants.USAGE_STATE_PERMISSION);
                Toast.makeText(this,
                        getString(R.string.explanation_access_to_appusage_is_not_enabled),
                        Toast.LENGTH_LONG).show();

            }else{
                mAppUsageAlarmReceiver = new AppUsageAlarmReceiver();
                mAppUsageAlarmReceiver.setAlarm(this);
                grantedAllPermission =true;
                // startOverlay();
            }
        }else {
            mAppUsageAlarmReceiver = new AppUsageAlarmReceiver();
            mAppUsageAlarmReceiver.setAlarm(this);
            grantedAllPermission =true;
            checkLocationSettings();
            // startOverlay();
        }
    }


    public void preventStatusBarExpansion(Context context) {
        manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        Activity activity = (Activity)context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        view = new customViewGroup(context);
        manager.addView(view, localLayoutParams);
    }

    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }

    /**
     *
     */

    private void preventStatusBarWithCheckingPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(Home.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, Constants.PREVENT_STATUS_BAR_PERMISSION);
            }else{
                preventStatusBarExpansion(this);
                _initUsageStateByCheckingPermission();
            }
        }else{
            preventStatusBarExpansion(this);
            _initUsageStateByCheckingPermission();
        }
    }




    public  void showPasswordAlert(final int requestCode, final boolean error){

        final EditText pwdEd = new EditText(this);
        if(error)
            pwdEd.setError("Enter valid password.");

        AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        bldr.setMessage("Please enter CL-Password.");
        bldr.setView(pwdEd);
        bldr.setPositiveButton("validate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(TextUtils.isEmpty(pwdEd.getText().toString())){
                    showToast(Home.this,"Enter valid password.");
                    showPasswordAlert(requestCode,true);
                }else{
                    if(!settingManager.getCLPassword().equals(
                            pwdEd.getText().toString())){
                        showToast(Home.this,"Enter valid password.");
                        showPasswordAlert(requestCode,true);
                    }else{
                        dialog.dismiss();
                        handleCLPasswordVerifiedAction(requestCode);
                    }
                }
            }
        });
        bldr.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        bldr.create().show();

    }


    private void handleCLPasswordVerifiedAction(int requestCode){
        switch (requestCode){
            case Constants.EXIT_LAUNCHER_REQUEST:
                exitLauncher();
                break;
            case Constants.OPEN_SETTING:
                handleOpenSettingScreen();
                break;
        }
    }


    private void exitLauncher(){

        this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void handleLauncherExit(){
        String option = settingManager.getEnablePasswordFormat();
        if(option.equals(Constants.PWD_DEFAULT)){
            exitLauncher();
        }else if(option.equals(Constants.PWD_CL)){
            showPasswordAlert(Constants.EXIT_LAUNCHER_REQUEST,false);
        }else if(option.equals(Constants.PWD_ANDROID)){
            doAndroidPassCodeLock();
        }
    }

    IntentFilter screenStateFilter;
    BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.createLog("onReceive  : ","ACTION_USER_PRESENT");
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                // Toast.makeText(context, "ACTION_SCREEN_ON", Toast.LENGTH_SHORT).show();
                if(settingManager.getEnablePasswordFormat().equals(Constants.PWD_ANDROID)){

                    if(isExitClick) {
                        exitLauncher();
                    }else if(isSettingClick){
                        handleOpenSettingScreen();
                    }
                }

            }
        }
    };



    private void doAndroidPassCodeLock(){
        DevicePolicyManager manager = CLDeviceManger.getCLDevicePolicyManager(this);
        if(manager!=null){
            manager.lockNow();
        }
    }


    private void showDialog(){
        try {
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.setMessage(getResources().getString(R.string.initializingTxt));
                loadingDialog.setCancelable(false);
                loadingDialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void hideDialog(){
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void doCallForRegistration()  {
        showDialog();
        String tabletSerialNo = Utils.getDeviceId(this);
        String launcherVersion = Utils.getAppVersion(this);
        String androidVersion = Utils.getAndroidVersion();
        String cl_serial_no = "";

        Call<JsonObject> call = apiService.registerTabletCall(tabletSerialNo,
                launcherVersion,androidVersion,cl_serial_no);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()&& response.code()==200){
                    JsonObject jsonObject = response.body();
                    if(jsonObject!=null){
                        String cl_serial_number = jsonObject.get("cl_serial_number").getAsString();
                        String credentials = jsonObject.get("credentials").getAsString();
                        settingManager.setCL_SerialNo(cl_serial_number);
                        settingManager.setCL_Credential(credentials);
                        LogUtil.createLog(TAG,"cl_serial_number ::"+cl_serial_number+" credentials::"+credentials);
                        doCallForAccessToken(credentials);
                    }
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtil.createLog("onFailure","jsonObject ::"+call);
                hideDialog();
            }
        });

    }



    private void doCallForAccessToken(String credential)  {
        showDialog();
        String authorizationHeader = "Basic "+credential;
        Call<JsonObject> call = apiService.getAccessTokenCall(authorizationHeader,"client_credentials");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()&& response.code()==200){
                    JsonObject jsonObject = response.body();
                    if(jsonObject!=null){
                        String access_token = jsonObject.get("access_token").getAsString();
                        String expireTime = jsonObject.get("expires_in").getAsString();
                        String token_type = jsonObject.get("token_type").getAsString();
                        settingManager.setAccessToken(access_token);
                        settingManager.setAccessTokenExpireTime(expireTime);
                        LogUtil.createLog(TAG,"access_token ::"+access_token+" expireTime::"+expireTime);

                        doCallForManifest(access_token);
                    }
                }else{
                    doCallForRegistration();
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtil.createLog("onFailure","jsonObject ::"+call);
                hideDialog();
            }
        });

    }




    private void doCallForManifest(String accessToken)  {
        showDialog();
        initialAppInfoCountFromDb = mAppInfoTable.getCount();
        String authorizationHeader = "Bearer "+accessToken;
        String cl_serial_number = settingManager.getCL_SerialNo();
        Call<JsonObject> call = apiService.getManifestCall(authorizationHeader,cl_serial_number);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hideDialog();
                if(response.isSuccessful()&& response.code()==200){
                    JsonObject jsonObject = response.body();
                  /*  JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(mock).getAsJsonObject();*/
                    if(jsonObject!=null){
                        parseData(jsonObject);
                    }

                }else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String errorString = jObjError.optString("error");
                        if(errorString.equalsIgnoreCase(Constants.KEY_INVALID_TOKEN))
                            doCallForAccessToken(settingManager.getCL_Credential());
                        else
                            Utils.showToast(Home.this,response.message()+" : "+errorString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                LogUtil.createLog("onFailure","jsonObject ::"+call);
                hideDialog();
                //If access token expire
                //  doCallForAccessToken(settingManager.getCL_Credential());
            }
        });

    }



    private void initDeviceRegistrationProcess(){
        if(NetworkStatus.getInstance().isConnected(this)){
            doCallForRegistration();
        }else{
            Utils.showToast(this,getResources().getString(R.string.network_error_text));
        }
    }


    private void
    parseData(final JsonObject jsonObject){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    AppInfoModel model = null;
                    String clManifestVersion = jsonObject.get(Constants.KEY_VERSION).getAsString();
                    settingManager.setManifestVersion(clManifestVersion);
                    JsonArray apps = jsonObject.getAsJsonArray(Constants.KEY_APPS);
                    LogUtil.createLog("manifest size :",apps.size()+" :: json"+jsonObject.toString());

                    for (int i = 0; i < apps.size(); i++) {
                        JsonObject jsonAppObject = apps.get(i).getAsJsonObject();
                        model = new AppInfoModel();
                        //start data parsing actual data coming from server.
                        model.setAppId(jsonAppObject.get(Constants.KEY_ID).getAsInt());
                        model.setApkDownloadPath(jsonAppObject.get(Constants.KEY_APK_NAME).getAsString());
                        model.setAppPckageName(jsonAppObject.get(Constants.KEY_FILE).getAsString());
                        model.setAppTitle(jsonAppObject.get(Constants.KEY_TITTLE).getAsString());
                        model.setContentType(jsonAppObject.get(Constants.KEY_CONTENT_TYPE).getAsString());
                        model.setAppVersion(getValidVersion(jsonAppObject.get(Constants.KEY_VERSION).getAsString()));
                        if(jsonAppObject.has(Constants.KEY_TYPE))
                            model.setType(jsonAppObject.get(Constants.KEY_TYPE).getAsString());
                        model.setVisible(jsonAppObject.get(Constants.KEY_VISIBLE).getAsInt());
                        //end of parsing

                        model.setDownloadStatus(Constants.ACTION_NOT_DOWNLOAD_YET);
                        model.setInstalationStatus(false);
                        model.setUpdateVersion("0");
                        model.setIsUpdateVersionExist(Constants.UPDATE_NOT_AVAILABLE);
                        model.setIcon(getResources().getDrawable(R.drawable.ic_launcher_app_not_install));
                        model.setUpdated(false);
                        model.setInstalationProcessInitiate(false);
                        model.setDownloadId(-1);
                        if(packageFilterMap.containsKey(model.getAppPckageName()))
                        {
                            packageFilterMap.remove(model.getAppPckageName());
                        }
                        doInsertUpdateProcess(model);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showToast(Home.this,getResources().getString(R.string.error_with_manifest));
                        }
                    });
                    clearFilterMap();

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideDialog();
                if(packageFilterMap.size()>0){
                    LogUtil.createLog("doFilterForMissingEntries","started for - "+packageFilterMap.size());
                    doFilterForMissingEntries(packageFilterMap);
                }else{
                    LogUtil.createLog("doFilterForMissingEntries","not started for - "+packageFilterMap.size());
                    loadApplications();
                }


            }
        }.execute();

    }

    private void doFilterForMissingEntries(final HashMap<String, AppInfoModel> packageFilterMap) {
        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                hideDialog();
                loadApplications();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Iterator it = packageFilterMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    System.out.println(pair.getKey().toString() + " = " + pair.getValue());
                    AppInfoModel model = (AppInfoModel) pair.getValue();
                    if(model!=null) {
                        if(model.isInstalationStatus()) {
                            mAppInfoTable.updateAppVisibilityInfo(pair.getKey().toString(), Constants.APP_NEED_TO_UNINSTALL);
                        }else{
                            mAppInfoTable.deleteAppDetails(pair.getKey().toString());
                        }
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
                return null;
            }
        }.execute();
    }


    private void doInsertUpdateProcess(AppInfoModel model){


        try {
            if(initialAppInfoCountFromDb > 0 && (mAppInfoTable.isExist(model.getAppId(),model.getAppPckageName()))){

                String oldVersionString = getValidVersion(mAppInfoTable.getVersionNo(model.getAppId(),model.getAppPckageName()));
                String newVersionString = getValidVersion(model.getAppVersion());

                int compareResult = 0;

                if(oldVersionString!=null&&!oldVersionString.equalsIgnoreCase("")
                        && newVersionString!=null&&!newVersionString.equalsIgnoreCase("")){
                    compareResult = Utils.versionCompare(newVersionString,oldVersionString);
                    LogUtil.createLog("Version compareResult :: oldVersionString ",oldVersionString+" newVersionString "+newVersionString+" ::"+compareResult+"");
                }



                if(compareResult>0){
                    model.setIsUpdateVersionExist(Constants.UPDATE_AVAILABLE);
                    model.setDownloadStatus(Constants.ACTION_NOT_DOWNLOAD_YET);
                    model.setUpdated(false);
                    model.setInstalationProcessInitiate(false);
                    model.setDownloadId(-1);
                    mAppInfoTable.updateAppInfo(model);


                }else if(compareResult<0){
                    model.setVisible(Constants.APP_NEED_TO_UNINSTALL);
                    mAppInfoTable.updateAppInfo(model);
                }

            }else{
                mAppInfoTable.insertAppInfo(model);
            }
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showToast(Home.this,getResources().getString(R.string.manifest_version_error));
                }
            });
            clearFilterMap();

        }

    }





    private void clearFilterMap(){
        if(packageFilterMap.size()>0) {
            packageFilterMap.clear();
        }
    }

    public boolean updateDownloadInfo(int id,int status){
        return  mAppInfoTable.updateAppDownLoadInfo(id,status);
    }

    public boolean updateDownloadId(int id,int downLoadId){
        return  mAppInfoTable.updateAppDownLoadID(id,downLoadId);
    }

    public boolean updateInstallationProcessInitiate(int id,boolean status){
        return  mAppInfoTable.updateAppInstallationProcessInfo(id,status);
    }



    //Hiding recent task.
    @Override
    protected void onPause() {
        super.onPause();
       /* if (!isAppClick) {
            startActivity(new Intent(this,Home.class).setFlags(FLAG_ACTIVITY_CLEAR_TASK|FLAG_ACTIVITY_NEW_TASK));
        }*/



    }





    private void startTimer() {
        if (timerTask == null) {
            timerTask = new Timer();
            timerTask.schedule(new TimerTask() {
                @Override
                public void run() {
                    Utils.installAPK(Home.this,"CL-Launcher");
                }
            }, 0, appUpdateTime);
        }
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
            Log.i("Timer Cancel","Stop Timer Called.");
        }
    }



    public BroadcastReceiver downLoadreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.createLog("onReceive  : ",action);
            LogUtil.createLog("Download Complete : ","ACTION_DOWNLOAD_COMPLETE");
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                checkDownLoadStatusAndProcess(appInfoList);
            }
        }
    };



    public BroadcastReceiver cleanUpReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.createLog("onReceive  : ",action);
            if(NetworkStatus.getInstance().isConnected(Home.this)) {
                Utils.showToast(Home.this, getResources().getString(R.string.cleanup_app_start_string));
                cleanUpProcess(appInfoList);
            }else{
                Utils.showToast(Home.this, getResources().getString(R.string.network_error_text));
            }

        }
    };


    public BroadcastReceiver listenRootedMsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.createLog("onReceive  : ",action);
            if(action.equals(Constants.ACTION_ROOTED)) {
                String packg = intent.getStringExtra("PCKG");
                AppInfoModel model = packageMap.get(packg);
                appInfoAdapter.notifyDataSetChanged();

            }

        }
    };




    public  void cleanUpProcess(final ArrayList<AppInfoModel> modelList){
        cleanupMap = new ArrayList<>();
        for (final AppInfoModel model:modelList) {
            new AsyncTask<Void,Void,AppInfoModel>(){
                @Override
                protected AppInfoModel doInBackground(Void... params) {
                    checkDownLoadStatusFromDownloadManager(model);
                    return model;
                }

                @Override
                protected void onPostExecute(AppInfoModel model) {
                    super.onPostExecute(model);
                    if(model!=null){

                        if (model.getDownloadStatus() == Constants.ACTION_DOWNLOAD_COMPLETED
                                && model.isInstalationProcessInitiate()
                                && !model.isInstalationStatus()
                                && !model.isUpdated()) {

                            initDownLoad(model);
                            cleanupMap.add(model);

                        }else if (model.getDownloadStatus() == Constants.ACTION_DOWNLOAD_COMPLETED
                                &&  model.isInstalationProcessInitiate()
                                &&  model.getIsUpdateVersionExist()==Constants.UPDATE_AVAILABLE
                                &&  model.isInstalationStatus()
                                && !model.isUpdated()){

                            initDownLoad(model);
                            cleanupMap.add(model);
                        }

                        appInfoAdapter.notifyItemChanged(modelList.indexOf(model));
                    }
                    if(cleanupMap.size()==0&&modelList.indexOf(model)==modelList.size()-1){
                        Utils.showToast(Home.this, getResources().getString(R.string.no_files_cleanup_txt));
                    }
                }
            }.execute();
        }


    }


//main

    private void checkDownLoadStatusAndProcess(final ArrayList<AppInfoModel> modelList){

        for (final AppInfoModel model:modelList) {
            if (model.getDownloadStatus() != Constants.ACTION_DOWNLOAD_COMPLETED&&model.getVisible()!= Constants.APP_NEED_TO_UNINSTALL) {
                new AsyncTask<Void, Void, AppInfoModel>() {
                    @Override
                    protected AppInfoModel doInBackground(Void... params) {
                        checkDownLoadStatusFromDownloadManager(model);

                        return model;
                    }

                    @Override
                    protected void onPostExecute(AppInfoModel model) {
                        if(model!=null){
                            if (model.getDownloadStatus() == Constants.ACTION_NOT_DOWNLOAD_YET||
                                    model.getDownloadStatus() == Constants.ACTION_DOWNLOAD_FAILED) {
                                initDownLoad(model);
                            }
                            appInfoAdapter.notifyDataSetChanged();
                        }
                    }
                }.execute();
            }
        }

    }


    private void checkDownLoadStatusFromDownloadManager(AppInfoModel model){
        long downloadId = model.getDownloadId();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if(cursor!=null&&cursor.getCount()>0) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    model.setDownloadStatus(Constants.ACTION_DOWNLOAD_COMPLETED);
                    if(isRooted()){
                        // model.setDownloadStatus(Constants.ACTION_ACTION_DOWNLOAD_COMPLETED_INSTALLING_FOR_ROOTED);
                        if((!model.isInstalationStatus())||(model.isInstalationStatus()&&model.getIsUpdateVersionExist()==Constants.UPDATE_AVAILABLE))
                            AppInstalationService.startActionInstall(this,model.getAppPckageName());
                    }

                    if(cleanupMap!=null&&cleanupMap.size()>0){
                        cleanupMap.remove(model);
                        if(cleanupMap.size()==0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.showToast(Home.this, getResources().getString(R.string.cleanup_app_stop_string));
                                }
                            });

                        }
                    }



                } else if (status == DownloadManager.STATUS_FAILED) {
                    // 1. process for download fail.
                    model.setDownloadStatus(Constants.ACTION_DOWNLOAD_FAILED);

                } else if ((status == DownloadManager.STATUS_PAUSED) ||
                        (status == DownloadManager.STATUS_RUNNING)) {
                    model.setDownloadStatus(Constants.ACTION_DOWNLOAD_RUNNING);

                } else if (status == DownloadManager.STATUS_PENDING) {
                    //Not handling now
                }

            }
        }else{
            model.setDownloadStatus(Constants.ACTION_NOT_DOWNLOAD_YET);
        }

        updateDownloadInfo(model.getAppId(),model.getDownloadStatus());


    }



    private void initDownLoad(AppInfoModel model){

        String apkDownloadUrl = ApiConstant.APK_ENDPOINT_URL+settingManager.getCL_SerialNo()+ApiConstant.APK+
                model.getApkDownloadPath();
        long downloadId = startDownloadManager(apkDownloadUrl,model.getAppPckageName(),settingManager.getAccessToken());
        updateDownloadId(model.getAppId(), (int) downloadId);
        updateDownloadInfo(model.getAppId(), Constants.ACTION_DOWNLOAD_RUNNING);
        updateInstallationProcessInitiate(model.getAppId(),false);
        model.setDownloadStatus(Constants.ACTION_DOWNLOAD_RUNNING);
        model.setDownloadId((int) downloadId);
        model.setInstalationProcessInitiate(false);

    }






    private long startDownloadManager(String downloadURL, String title, String accessToken) {

        LogUtil.createLog("startDownloadManager ::",downloadURL+" access token ::"+accessToken);
        File filesDir = new File(Constants.APK_PATH);
        if(!filesDir.exists()){
            filesDir.mkdir();
        }

        Uri downloadUri = Uri.parse(downloadURL);
        Uri destinationUri = Uri.parse(filesDir+"/"+title.replaceAll("\\s+","")+".apk");


        File destinationUriFile = new File(destinationUri.toString());
        if(destinationUriFile.exists()){
            destinationUriFile.delete();
            LogUtil.createLog("File delete before download",destinationUri.toString());
        }


        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setTitle(title);
        request.addRequestHeader(ApiConstant.AUTHORIZATION, ApiConstant.BEARER+" "+accessToken);
        request.setDestinationUri(Uri.fromFile(destinationUriFile));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        //   if(!checkDownloadSuceesStatus(downloadID)) {
        long downloadID = downloadManager.enqueue(request);
        //   }
        return downloadID;
    }

    /**
     *
     * @param downloadId
     * @return
     */
    private boolean checkDownloadSuceesStatus(long downloadId) {
        Cursor cursor = null;
        try {
            DownloadManager.Query downloadQuery = new DownloadManager.Query();
            downloadQuery.setFilterById(downloadId);
            cursor = downloadManager.query(downloadQuery);
            int cursorCount = cursor.getCount();
            if (cursorCount > 0) {
                cursor.moveToFirst();
                int downloadedStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (downloadedStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            cursor.close();
        }

        return false;
    }


    /**
     *
     */


    AppInfoAdapter.OnItemClickListener onItemClickListener = new AppInfoAdapter.OnItemClickListener() {
        @Override
        public void onClick(int position) {
            AppInfoModel model = appInfoList.get(position);
            if (model != null) {
                isAppClick = true;
                if (model.isInstalationStatus()) {
                    if (model.getVisible() == Constants.APP_NEED_TO_UNINSTALL) {
                        if(isRooted()) {
                            AppInstalationService.startActionUninstall(Home.this,model.getAppPckageName());
                        }else{
                            Utils.unInstallApk(Home.this, model.getAppPckageName());
                        }

                    } else {
                        if (model.getIsUpdateVersionExist() == Constants.UPDATE_AVAILABLE
                                && model.getDownloadStatus() == Constants.ACTION_DOWNLOAD_COMPLETED) {
                            checkAndDoInstalingProcessForRooted(model);
                        } else {
                            startActivity(model.getIntent());
                        }
                    }
                } else {
                    if (model.getDownloadStatus() == Constants.ACTION_DOWNLOAD_COMPLETED) {
                        checkAndDoInstalingProcessForRooted(model);
                    } else if (model.getDownloadStatus() == Constants.ACTION_DOWNLOAD_FAILED) {
                        Utils.showToast(Home.this, getResources().getString(R.string.download_failed));
                    } else {
                        Utils.showToast(Home.this, getResources().getString(R.string.downloading_in_progress));
                    }
                }
            }
        }
    };


    /**
     *
     * @param model
     */
    private void checkAndDoInstalingProcessForRooted(AppInfoModel model){
        if(isRooted()){
            Utils.showToast(Home.this,getResources().getString(R.string.instaling));
        }else {
            Utils.installAPK(Home.this, model.getAppPckageName());
        }
        boolean status = updateInstallationProcessInitiate(model.getAppId(), true);
        if (status) {
            model.setInstalationProcessInitiate(true);
        }
    }


    /**
     *
     * @param version
     * @return
     */
    private String getValidVersion(String version){
        return  version.contains("-")? version.replace("-","."):version;
    }

}
