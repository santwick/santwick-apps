package com.santwick.locknow;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends  DeviceAdminReceiver{
	@Override 
    public void onReceive(Context context, Intent intent) { 
        super.onReceive(context, intent); 
    } 
   
    @Override 
    public void onEnabled(Context context, Intent intent) { 
        super.onEnabled(context, intent); 
    } 
   
    @Override 
    public void onDisabled(Context context, Intent intent) { 
        super.onDisabled(context, intent); 
    } 
}
