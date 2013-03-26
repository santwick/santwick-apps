package com.santwick.object;

import java.io.File;

import android.os.Environment;

public class AppObject {
	private String appId;
	private String appName;
	private String appDesc;
	private String packName;
	private String version;
	private int versionCode;
	private String iconUrl;
	private String downUrl;
	private String iconPath;
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppDesc() {
		return appDesc;
	}
	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
		new File(Environment.getExternalStorageDirectory().getPath() + "/santwick/" + appId).mkdirs();
		this.iconPath = Environment.getExternalStorageDirectory().getPath() + "/santwick/" + appId + "/icon.png";
	}
	public String getIconPath() {
		return iconPath;
	}
}
