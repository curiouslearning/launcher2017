package model;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IMFCORP\alok.acharya on 24/2/17.
 */

public class AppInfoModel {
    @SerializedName("appId")
    @Expose
    private Integer appId;

    @SerializedName("apkDownloadPath")
    @Expose
    private String apkDownloadPath;

   @SerializedName("file")
    @Expose
    private String file;

    @SerializedName("appTitle")
    @Expose
    private String appTitle;

    @SerializedName("content_type")
    @Expose
    private String contentType;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("version")
    @Expose
    private String appVersion;


    @SerializedName("visible")
    @Expose
    private Integer visible;

    @SerializedName("downloadStatus")
    @Expose
    private int downloadStatus;

    @SerializedName("instalationStatus")
    @Expose
    private boolean instalationStatus;

    @SerializedName("isUpdateVersionExist")
    @Expose
    private Integer isUpdateVersionExist;


    @SerializedName("localPath")
    @Expose
    private String localPath;

    @SerializedName("updateVersion")
    @Expose
    private String updateVersion;

    @SerializedName("isUpdated")
    @Expose
    private boolean isUpdated;


    @SerializedName("isInstalationProcessInitiate")
    @Expose
    private boolean isInstalationProcessInitiate;

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public boolean isInstalationProcessInitiate() {
        return isInstalationProcessInitiate;
    }

    public void setInstalationProcessInitiate(boolean instalationProcessInitiate) {
        isInstalationProcessInitiate = instalationProcessInitiate;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public String getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(String updateVersion) {
        this.updateVersion = updateVersion;
    }

    @SerializedName("downloadId")
    @Expose
    private long downloadId;






    @SerializedName("appPckageName")
    @Expose
    private String appPckageName;







    public Integer getIsUpdateVersionExist() {
        return isUpdateVersionExist;
    }

    public void setIsUpdateVersionExist(Integer isUpdateVersionExist) {
        this.isUpdateVersionExist = isUpdateVersionExist;
    }




    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }








    public String getAppPckageName() {
        return appPckageName;
    }

    public void setAppPckageName(String appPckageName) {
        this.appPckageName = appPckageName;
    }



    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public boolean isInstalationStatus() {
        return instalationStatus;
    }

    public void setInstalationStatus(boolean instalationStatus) {
        this.instalationStatus = instalationStatus;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getApkDownloadPath() {
        return apkDownloadPath;
    }

    public void setApkDownloadPath(String apkDownloadPath) {
        this.apkDownloadPath = apkDownloadPath;
    }

   public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }


    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     * The intent used to start the application.
     */
    Intent intent;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * The application icon.
     */
    Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    boolean filtered;




    /**
     * Creates the application intent based on a component name and various launch flags.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    public final Intent setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        return  intent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppInfoModel)) {
            return false;
        }

        AppInfoModel that = (AppInfoModel) o;
        return appTitle.equals(that.appTitle) &&
                intent.getComponent().getClassName().equals(
                        that.intent.getComponent().getClassName());
    }

    @Override
    public int hashCode() {
        int result;
        result = (appTitle != null ? appTitle.hashCode() : 0);
        final String name = intent.getComponent().getClassName();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
