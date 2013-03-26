package com.santwick.adwarekill;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import com.santwick.adwarekill.object.ConfigObject;
import com.santwick.net.HttpClientUtil;
import com.santwick.net.NetClientCallback;
import com.santwick.ui.R;
import com.santwick.utils.ClientUtils;
import com.santwick.utils.PackageUtils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service; 
import android.content.Context;
import android.content.Intent; 
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder; 
import android.os.Message;
import android.util.Log;



@SuppressLint("HandlerLeak")
public class UpdateService extends Service { 
	
	public final static int UPDATE_START = 0;
	public final static int UPDATE_SUCCESS = 1;
	public final static int UPDATE_FAILED = 2;
	public final static int UPDATE_ALREADY_LATEST = 3;

	
	private ConfigObject config ;
	private static String lastUpdateSuccessDate = "";

	@Override 
	public void onStart(Intent intent, int startId) { 
		log("onStart");

		if(intent != null && intent.getExtras() != null){
			if(intent.getExtras().getBoolean("startupdate") && config.isUpdateRules()){
				//如果只允许WiFi更新而当前不是WiFi就不更新
				if(config.isUpdateRulesOnlyWifi() && !checkWifi()){
					return;
				}
				//今天已经更新过，就不自动更新了
				if(new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date()).equals(lastUpdateSuccessDate)){
					return;
				}
				CheckUpdate(this,new Handler(){
					public void handleMessage(Message msg) {
						switch(msg.what){
						case UPDATE_START:
							break;
						case UPDATE_FAILED:
//							break;
						case UPDATE_SUCCESS:
						case UPDATE_ALREADY_LATEST:
							lastUpdateSuccessDate = new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
							break;
						}
					}
				});
			}
		}

		super.onStart(intent, startId); 
	} 
	
	@Override 
	public void onCreate() { 
		log("onCreate");
		config = DataCenter.getConfigObject(this);
		lastUpdateSuccessDate = DataCenter.getAdwareRuleObject(this).getUpdateDate();

		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		Intent intent = new Intent(this, UpdateService.class);
		intent.putExtra("startupdate", true);
		PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
		am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10*60*1000, 10*60*1000, pi);
		
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
		
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		Intent intent = new Intent(this, UpdateService.class);
		intent.putExtra("startupdate", true);
		PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);

		am.cancel(pi);

		super.onDestroy(); 
	} 
	
	private void log(String str) { 
		Log.i("UpdateService", str);
	} 
	
	private boolean checkWifi() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo!= null && networkInfo.getState() == NetworkInfo.State.CONNECTED && 
        		networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        	return true;
        }
        return false;
    }

	public static void CheckUpdate(final Context context,final Handler handler) {

		handler.sendEmptyMessage(UPDATE_START);
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				new HttpClientUtil(context.getResources().getString(R.string.update_url),HttpClientUtil.METHOD_GET,new NetClientCallback(){
					@Override
					public void execute(int status, String response) {
						switch(status){
						case NetClientCallback.NET_SUCCESS:
							try {
								JSONObject json = new JSONObject(response);
								if(json.getBoolean("success")){
									JSONObject jsonData = 	json.getJSONObject("data");
									long version = jsonData.getLong("version");
									String updateDate = jsonData.getString("updateDate");
									
									if(version>DataCenter.getAdwareRuleObject(context).getVersion()){
										HashMap<String, String> adwareList = new HashMap<String, String>();
										
										JSONObject jsonAdwareList = jsonData.getJSONObject("adwareList");
										for(int i=0;i<jsonAdwareList.length();i++){
											String name = (String) jsonAdwareList.names().get(i);
											adwareList.put(name, jsonAdwareList.getString(name));
										}
										DataCenter.getAdwareRuleObject(context).setVersion(version);
										DataCenter.getAdwareRuleObject(context).setUpdateDate(updateDate);
										DataCenter.getAdwareRuleObject(context).setAdwareList(adwareList);
										
										DataCenter.saveAdwareRuleObject(context);
		
										handler.sendEmptyMessage(UPDATE_SUCCESS);
										return;
									}else{
										handler.sendEmptyMessage(UPDATE_ALREADY_LATEST);
										return;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
						case NetClientCallback.NET_FAILED:
						case NetClientCallback.NET_TIMEOUT:
							break;
						}
						handler.sendEmptyMessage(UPDATE_FAILED);
						return;
					}
				})
				.addParam("cid", ClientUtils.getClientId(context))
				.addParam("aid", PackageUtils.getAppId(context))
				.addParam("req", "getRule")
				.syncConnect();
			}
		}).start();
	}
	
}