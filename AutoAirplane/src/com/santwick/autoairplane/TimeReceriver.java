package com.santwick.autoairplane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeReceriver  extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Intent.ACTION_TIME_CHANGED.equals(intent.getAction())
				|| Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())){
			Log.i("TimeReceriver",intent.getAction());
			context.stopService(new Intent(context,TimerService.class));
			context.startService(new Intent(context,TimerService.class));
		}
	}
}
