package com.santwick.adwarekill;

import java.util.HashMap;

import com.santwick.adwarekill.object.HistoryListObject;
import com.santwick.adwarekill.object.AdwareObject;
import com.santwick.adwarekill.object.AdwareRuleObject;
import com.santwick.adwarekill.object.ConfigObject;
import com.santwick.adwarekill.object.IgnoreListObject;

import android.content.Context;
import android.os.Handler;

public class DataCenter {
	private static ConfigObject config = null;
	private static AdwareRuleObject adwareRules = null;
	private static HistoryListObject historyList = null;
	private static IgnoreListObject ignoreList = null;
	private static HashMap<String,AdwareObject> adwareMap = new HashMap<String,AdwareObject>();
	
	private static Handler onPackageRemovedHandler = null;
	
	public static ConfigObject getConfigObject(Context context){
		if(config==null){
			config = new ConfigObject();
			config.fromJSONFile(context, "config");
		}
		return config;
	}
	
	public static AdwareRuleObject getAdwareRuleObject(Context context){
		if(adwareRules==null){
			adwareRules = new AdwareRuleObject();
			adwareRules.fromJSONFile(context, "adwareRules");
		}
		return adwareRules;
	}
	
	public static void saveAdwareRuleObject(Context context){
		if(adwareRules!=null){
			adwareRules.toJSONFile(context, "adwareRules");
		}
	}
	
	public static HistoryListObject getHistoryList(Context context){
		if(historyList==null){
			historyList = new HistoryListObject();
			historyList.fromJSONFile(context, "historyList");
		}
		return historyList;
	}
	
	public static void saveHistoryList(Context context){
		if(historyList!=null){
			historyList.toJSONFile(context, "historyList");
		}
	}
	
	public static IgnoreListObject getIgnoreList(Context context){
		if(ignoreList==null){
			ignoreList = new IgnoreListObject();
			ignoreList.fromJSONFile(context, "ignoreList");
		}
		return ignoreList;
	}
	
	public static void saveIgnoreList(Context context){
		if(ignoreList!=null){
			ignoreList.toJSONFile(context, "ignoreList");
		}
	}

	public static Handler getOnPackageRemovedHandler() {
		return onPackageRemovedHandler;
	}

	public static void setOnPackageRemovedHandler(Handler onPackageRemovedHandler) {
		DataCenter.onPackageRemovedHandler = onPackageRemovedHandler;
	}

	public static HashMap<String,AdwareObject> getAdwareMap() {
		return adwareMap;
	}
	

}
