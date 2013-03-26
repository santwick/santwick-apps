package com.santwick.adwarekill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.santwick.adwarekill.object.AdwareObject;
import com.santwick.graphics.FormatConvert;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;


public class ScanEngine {
	
	public static final int SCAN_START = 1;
	public static final int SCAN_RUN = 2;
	public static final int SCAN_END = 3;
	public static final int SCAN_FIND = 4;
	
	
	public static String scanPack(Context context,String packName){
		
		HashMap<String, String> AD_INFO_MAP = DataCenter.getAdwareRuleObject(context).getAdwareList();
		
		PackageManager packManager = context.getPackageManager();
		String adString = "";
		try {
			PackageInfo packInfo = packManager.getPackageInfo(packName,
					PackageManager.GET_ACTIVITIES | PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES);
			HashSet<String> adSet = new HashSet<String>();
			
	    	if(packInfo.activities != null){
	    		for(ActivityInfo actInfo: packInfo.activities){
	    			if(actInfo!=null){
		    			for(Entry<String,String> entry:AD_INFO_MAP.entrySet()){
		    				if(actInfo.name.startsWith(entry.getKey())){
		    					adSet.add(entry.getValue());
		    				}
		    			}
	    			}
	    		}
	    	}
	    	if(packInfo.receivers != null){
	    		for(ActivityInfo actInfo: packInfo.receivers){
	    			if(actInfo!=null){
		    			for(Entry<String,String> entry:AD_INFO_MAP.entrySet()){
		    				if(actInfo.name!=null && actInfo.name.startsWith(entry.getKey())){
		    					adSet.add(entry.getValue());
		    				}
		    			}
	    			}
	    		}
	    	}
	    	if(packInfo.services != null){
	    		for(ServiceInfo svrInfo: packInfo.services){
	    			if(svrInfo!=null){
		    			for(Entry<String,String> entry:AD_INFO_MAP.entrySet()){
		    				if(svrInfo.name.startsWith(entry.getKey())){
		    					adSet.add(entry.getValue());
		    				}
		    			}
	    			}
	    		}
	    	}
	    	if(!adSet.isEmpty()){

	    		for(String adstr:adSet){
	    			adString += adstr +" ";
	    		}


	    	}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return adString;
	}
	
	private static void _scanAll(Context context,Handler handler){
		
		
		PackageManager packManager = context.getPackageManager();
	    
	    List<ApplicationInfo> appList = packManager.getInstalledApplications(0);
	    int total = appList.size();
	    int scaned = 0;
	    for(ApplicationInfo appInfo: appList){
	    	String adString;
	    	if(		!DataCenter.getIgnoreList(context).getAdwareSet().contains(appInfo.packageName) &&
	    			!(adString = scanPack(context,appInfo.packageName)).equals("")){
//	    		HashMap<String,Object> map = new HashMap<String,Object>();
//	    		map.put("image", appInfo.loadIcon(packManager));
//	    		map.put("name", appInfo.loadLabel(packManager).toString());
//	    		map.put("packname", packInfo.packageName);
//	    		map.put("adname", adString);
//	    		list.add(map);
	    		
				Message msg = new Message();
				msg.what = SCAN_FIND;

				AdwareObject adwareObject = new AdwareObject();
				adwareObject.setIcon(FormatConvert.drawableToString(appInfo.loadIcon(packManager)));
				adwareObject.setName(appInfo.loadLabel(packManager).toString());
				adwareObject.setPackName(appInfo.packageName);
				adwareObject.setAdString(adString);
				DataCenter.getAdwareMap().put(appInfo.packageName, adwareObject);

				//msg.getData().putParcelable("image",appInfo.loadIcon(packManager));
				msg.getData().putString("name",appInfo.loadLabel(packManager).toString());
				msg.getData().putString("packName",appInfo.packageName);
				msg.getData().putString("adString",adString);

				handler.sendMessage(msg);
	    		
	    	}

			Message msg = new Message();
			msg.what = SCAN_RUN;
			msg.getData().putInt("total",total);
			msg.getData().putInt("scaned",++scaned);
			msg.getData().putString("name",appInfo.loadLabel(packManager).toString());
			handler.sendMessage(msg);
	    }
	    handler.sendEmptyMessage(SCAN_END);
	}

	public static void scanAll(final Context context,final Handler handler) {
		handler.sendEmptyMessage(SCAN_START);
    	new Thread(new Runnable() {
			@Override
			public void run() {
				_scanAll(context,handler);
			}
		}).start();
	}
	
	public static String getName(Context context,String packName){
		PackageManager packManager = context.getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = packManager.getApplicationInfo(packName, 0);
			return appInfo.loadLabel(packManager).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Drawable getIcon(Context context,String packName){
		PackageManager packManager = context.getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = packManager.getApplicationInfo(packName, 0);
			return appInfo.loadIcon(packManager);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
