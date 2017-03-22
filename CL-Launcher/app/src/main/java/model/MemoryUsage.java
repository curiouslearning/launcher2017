package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by IMFCORP\alok.acharya on 13/2/17.
 */

public class MemoryUsage {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("value")
    @Expose
    private Value value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


    public class Value {

        @SerializedName("tabletID")
        @Expose
        private String tabletID;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getManifest_version() {
            return manifest_version;
        }

        public void setManifest_version(String manifest_version) {
            this.manifest_version = manifest_version;
        }

        @SerializedName("manifest_version")
        @Expose
        private String manifest_version;

        @SerializedName("android_version")
        @Expose
        private String androidVersion;
        @SerializedName("space_available")
        @Expose
        private String spaceAvailable_kb;

        @SerializedName("space_in_use")
        @Expose
        private String spaceInUse_kb;
        @SerializedName("cl_software_space")
        @Expose
        private String clSoftwareSpace_kb;
        @SerializedName("cl_data_space")
        @Expose
        private String clDataSpace_kb;

        public String getTabletID() {
            return tabletID;
        }

        public void setTabletID(String tabletID) {
            this.tabletID = tabletID;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getAndroidVersion() {
            return androidVersion;
        }

        public void setAndroidVersion(String androidVersion) {
            this.androidVersion = androidVersion;
        }

        public String getSpaceAvailable_kb() {
            return spaceAvailable_kb;
        }

        public void setSpaceAvailable_kb(String spaceAvailable_kb) {
            this.spaceAvailable_kb = spaceAvailable_kb;
        }

        public String getSpaceInUse_kb() {
            return spaceInUse_kb;
        }

        public void setSpaceInUse_kb(String spaceInUse_kb) {
            this.spaceInUse_kb = spaceInUse_kb;
        }

        public String getClSoftwareSpace_kb() {
            return clSoftwareSpace_kb;
        }

        public void setClSoftwareSpace_kb(String clSoftwareSpace_kb) {
            this.clSoftwareSpace_kb = clSoftwareSpace_kb;
        }

        public String getClDataSpace_kb() {
            return clDataSpace_kb;
        }

        public void setClDataSpace_kb(String clDataSpace_kb) {
            this.clDataSpace_kb = clDataSpace_kb;
        }

    }
}
