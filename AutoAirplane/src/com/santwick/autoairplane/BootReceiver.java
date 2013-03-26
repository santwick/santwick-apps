package com.santwick.autoairplane;

import com.santwick.autoairplane.object.ConfigObject;
import com.santwick.utils.TimeUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			ConfigObject config = new ConfigObject();
			config.fromJSONFile(context, "config");
			
			if(config.getAutoMode()==ConfigObject.AUTOMODE_BOTH){
				TimerService.setAirplaneMode(context, 
						TimeUtils.timeBetween(config.getStartHour()*60 + config.getStartMin(), 
						config.getStopHour()*60 + config.getStopMin()));
			}
			if(config.getAutoMode() != ConfigObject.AUTOMODE_DISABLE ){
				context.startService(new Intent(context,TimerService.class));
			}
		}
	}
}
