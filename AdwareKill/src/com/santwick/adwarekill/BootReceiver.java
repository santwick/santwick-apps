package com.santwick.adwarekill;


import com.santwick.adwarekill.object.ConfigObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			ConfigObject config = DataCenter.getConfigObject(context);
			if(config.isUpdateRules()){
				context.startService(new Intent(context,UpdateService.class));
			}
		}
	}
}
