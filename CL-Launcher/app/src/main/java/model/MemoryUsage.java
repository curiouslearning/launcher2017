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
        private String spaceAvailable;
        @SerializedName("space_in_use")
        @Expose
        private String spaceInUse;
        @SerializedName("cl_software_space")
        @Expose
        private String clSoftwareSpace;
        @SerializedName("cl_data_space")
        @Expose
        private String clDataSpace;

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

        public String getSpaceAvailable() {
            return spaceAvailable;
        }

        public void setSpaceAvailable(String spaceAvailable) {
            this.spaceAvailable = spaceAvailable;
        }

        public String getSpaceInUse() {
            return spaceInUse;
        }

        public void setSpaceInUse(String spaceInUse) {
            this.spaceInUse = spaceInUse;
        }

        public String getClSoftwareSpace() {
            return clSoftwareSpace;
        }

        public void setClSoftwareSpace(String clSoftwareSpace) {
            this.clSoftwareSpace = clSoftwareSpace;
        }

        public String getClDataSpace() {
            return clDataSpace;
        }

        public void setClDataSpace(String clDataSpace) {
            this.clDataSpace = clDataSpace;
        }

    }
}
