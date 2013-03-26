package com.santwick.adwarekill;


import com.santwick.adwarekill.object.ConfigObject;
import com.santwick.adwarekill.ui.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class PackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConfigObject config = DataCenter.getConfigObject(context);
	
		if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction()) ||
				Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){
			if(config.isCheckNewApp()){
				String packName = intent.getDataString().substring(8);
				String adString = ScanEngine.scanPack(context, packName);
				if(!DataCenter.getIgnoreList(context).getAdwareSet().contains(packName) && !adString.equals("")){
					Notification mNotification = new Notification(R.drawable.ic_launcher,context.getResources().getString(R.string.app_name),System.currentTimeMillis());
					mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				    NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
				    
				    Intent intentMain = new Intent(Intent.ACTION_MAIN);
				    intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
				    intentMain.setClass(context, MainActivity.class);
				    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	
				    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentMain, 0);
				    mNotification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), 
				    		String.format(context.getResources().getString(R.string.notify_content), ScanEngine.getName(context, packName),adString), contentIntent);  
		            mNotificationManager.notify(0, mNotification);  
				}
			}
		}else if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){   
			Handler onPackageRemovedHandler = DataCenter.getOnPackageRemovedHandler();
			if(onPackageRemovedHandler != null){
				Message msg = new Message();
				msg.getData().putString("packName", intent.getDataString().substring(8));
				onPackageRemovedHandler.sendMessage(msg);
			}
		}
	}
}
