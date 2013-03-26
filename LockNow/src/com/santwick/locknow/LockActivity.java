package com.santwick.locknow;

import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManager;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


public class LockActivity extends Activity {
	private DevicePolicyManager policyManager; 
    private ComponentName componentName;
    private ConfigObject config = new ConfigObject();
	int defaultimeout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_lock);
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE); 
		componentName = new ComponentName(this, LockReceiver.class); 
		config.fromJSONFile(this, "config");
		
		if (policyManager.isAdminActive(componentName)) {
			if(config.isCompatibleMode()){
				try
				{
					defaultimeout = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
					Log.i("LockActivity", Integer.toString(defaultimeout));
					Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);
					screenOff();
				}catch (SettingNotFoundException e){
					e.printStackTrace();
					exitProcess();
				}
			}else{
				policyManager.lockNow();
				exitProcess();
			}
		}else{
			activeManager();
		}
    }
	
  //使用隐式意图调用系统方法来激活指定的设备管理器 
	private void activeManager() { 
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN); 
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName); 
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.app_name)); 
		startActivityForResult(intent,1); 
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("LockActivity", "onPause");

		if (policyManager.isAdminActive(componentName)) {
			if(config.isCompatibleMode()){
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defaultimeout);
			}
			policyManager.lockNow();
			exitProcess();
		}
	}
	
	//重写此方法用来在第一次激活设备管理器之后锁定屏幕 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (policyManager.isAdminActive(componentName)) {
			if(config.isCompatibleMode()){
				try
				{
					defaultimeout = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
					Log.i("LockActivity", Integer.toString(defaultimeout));
					Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);
					screenOff();
				}catch (SettingNotFoundException e){
					e.printStackTrace();
					exitProcess();
				}
			}else{
				policyManager.lockNow();
				exitProcess();
			}
		}else{
			exitProcess();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	} 
	
	private void screenOff(){
		WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
		localLayoutParams.screenBrightness = 0.0F;
		getWindow().setAttributes(localLayoutParams);
	}
	
	private void exitProcess(){
		finish();
		android.os.Process.killProcess(android.os.Process.myPid()); 
	}

}
