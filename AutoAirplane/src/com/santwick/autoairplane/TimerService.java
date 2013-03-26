package com.santwick.autoairplane;

import java.util.Calendar;

import com.santwick.autoairplane.object.ConfigObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service; 
import android.content.Context;
import android.content.Intent; 
import android.net.wifi.WifiManager;
import android.os.IBinder; 
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.Toast;



public class TimerService extends Service { 
	
	private ConfigObject config = new ConfigObject();
	private AlarmManager am = null;

	@Override 
	public void onStart(Intent intent, int startId) { 
		log("onStart");
		if(intent != null && intent.getExtras() != null){
			if(intent.getExtras().getBoolean("isStart")){
				setAirplaneMode(this,true);
			}else{
				setAirplaneMode(this,false);
			}
		}

		super.onStart(intent, startId); 
	} 
	
	@Override 
	public void onCreate() { 
		log("onCreate");
		
		config.fromJSONFile(this, "config");
		
		Calendar calendar = Calendar.getInstance(); 
		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
		int nowMin = calendar.get(Calendar.MINUTE);
		int nowSec = calendar.get(Calendar.SECOND);
		
		am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		if((config.getAutoMode()& ConfigObject.AUTOMODE_ENTER) != 0){
			int startDelay = (((config.getStartHour() - nowHour)*60 + config.getStartMin() - nowMin)*60 + 0 - nowSec) * 1000;
			Intent intentStart = new Intent(this, TimerService.class);
			intentStart.putExtra("isStart", true);
			PendingIntent piStart = PendingIntent.getService(this, 0, intentStart, 0);

			am.setRepeating(AlarmManager.RTC_WAKEUP, 
					System.currentTimeMillis()+(startDelay>0?startDelay:(startDelay+AlarmManager.INTERVAL_DAY)), 
					AlarmManager.INTERVAL_DAY, piStart);
		}
		if((config.getAutoMode()& ConfigObject.AUTOMODE_LEAVE) != 0){
			int stopDelay = (((config.getStopHour() - nowHour)*60 + config.getStopMin() - nowMin)*60 + 0 - nowSec) * 1000;

			Intent intentStop = new Intent(this, TimerService.class);
			intentStop.putExtra("isStart", false);
			PendingIntent piStop = PendingIntent.getService(this, 1, intentStop, 0);

			am.setRepeating(AlarmManager.RTC_WAKEUP, 
					System.currentTimeMillis()+(stopDelay>0?stopDelay:(stopDelay+AlarmManager.INTERVAL_DAY)), 
					AlarmManager.INTERVAL_DAY, piStop);

		}
		super.onCreate(); 
	} 
	
	@Override 
	public IBinder onBind(Intent intent) { 
		return null; 
	} 
	
	@Override 
	public void onDestroy() { 
	/* */ 
		log("onDestroy");
		
		Intent intentStart = new Intent(this, TimerService.class);
		intentStart.putExtra("isStart", true);
		PendingIntent piStart = PendingIntent.getService(this, 0, intentStart, 0);

		am.cancel(piStart);
		
		Intent intentStop = new Intent(this, TimerService.class);
		intentStop.putExtra("isStart", false);
		PendingIntent piStop = PendingIntent.getService(this, 1, intentStop, 0);

		am.cancel(piStop);
		
		super.onDestroy(); 
	} 
	
	
	private void log(String str) { 
		Log.i("TimerService", str);
	} 
	
	public static boolean getAirplaneMode(Context context) {
		try {
			return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON)==1 ;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void setAirplaneMode(Context context,boolean setAirPlane) {
		try {
			WifiManager mWifiManager = null;
			boolean bKeepWifi = false;
			if(setAirPlane){
				ConfigObject config = new ConfigObject();
				config.fromJSONFile(context, "config");
				if(config.isKeepWifi()){
					mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
					bKeepWifi = mWifiManager.isWifiEnabled();
				}
			}
			
			if(Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON)  != (setAirPlane ? 1 : 0) ){
				
//				PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);//获取电源管理器对象
//				PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
//				//获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是LogCat里用的Tag
//				wl.acquire();//点亮屏幕
//				wl.release();//释放
				
				Toast.makeText(context, setAirPlane?R.string.toast_start:R.string.toast_stop, Toast.LENGTH_LONG).show();
						
				Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
				// 广播飞行模式信号的改变，让相应的程序可以处理。
				// 不发送广播时，在非飞行模式下，Android 2.2.1上测试关闭了Wifi,不关闭正常的通话网络(如GMS/GPRS等)。
				// 不发送广播时，在飞行模式下，Android 2.2.1上测试无法关闭飞行模式。
				Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				intent.putExtra("state", setAirPlane);
				context.sendBroadcast(intent);
				
				if(bKeepWifi){
					mWifiManager.setWifiEnabled(true);
				}
			}
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}