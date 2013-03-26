package com.santwick.utils;

import com.santwick.ui.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {
	
	public static void sharePackage(Context context){
		Intent intent=new Intent(Intent.ACTION_SEND); 
		intent.setType("text/plain"); 
		intent.putExtra(Intent.EXTRA_SUBJECT, String.format(
				context.getResources().getString(R.string.share_title), 
				context.getResources().getString(R.string.app_name))); 
		intent.putExtra(Intent.EXTRA_TEXT, String.format(
				context.getResources().getString(R.string.share_content),
				context.getResources().getString(R.string.app_name),
				context.getResources().getString(R.string.app_url))); 
		context.startActivity(Intent.createChooser(intent, 
				context.getResources().getString(R.string.config_about_share)));
	}

	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;

		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0.1";
	}
	
	public static String getAppId(Context context) {
		String packName = context.getPackageName();
		if(packName.startsWith("com.santwick.")){
			return packName.substring(13);
		}else{
			return packName;
		}
	}
	
	
	public static int getVersionCode(Context context,String packName){
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(packName,0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	
	public static int getVersionCode(Context context) {
		return getVersionCode(context,context.getPackageName());
	}
}
