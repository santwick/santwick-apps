package com.santwick.utils;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


import com.santwick.net.HttpClientUtil;
import com.santwick.net.NetClientCallback;
import com.santwick.ui.R;
import com.santwick.ui.DownloadActivity;

public class UpdateUtils {
	
	public final static int VERSION_CHECK_START = 0;
	public final static int VERSION_CHECK_SUCCESS = 1;
	public final static int VERSION_CHECK_FAILED = 2;
	public final static int VERSION_CHECK_ALREADY_LATEST = 3;
	
	public final static int GET_OURAPPS_SUCCESS = 1;
	public final static int GET_OURAPPS_FAILED = 2;
	
	public static void getOurApps(final Context context,final Handler handler) {
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
									Message msg = new Message();
									msg.what = GET_OURAPPS_SUCCESS;
									msg.getData().putString("jsonData", json.getString("data"));
									handler.sendMessage(msg);
									return;
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
						case NetClientCallback.NET_FAILED:
						case NetClientCallback.NET_TIMEOUT:
							break;
						}
						handler.sendEmptyMessage(GET_OURAPPS_FAILED);
						return;
					}
				})
				.addParam("cid", ClientUtils.getClientId(context))
				.addParam("aid", PackageUtils.getAppId(context))
				.addParam("req", "getApps")
				.syncConnect();
			}
		}).start();
	}
	
	
	public static void checkUpdateSchedule(final Context context) {
		if(System.currentTimeMillis() - getLastUpdateTimeFromFile(context) > 7*24*60*60*1000){
			checkUpdate(context,new Handler(){
				public void handleMessage(Message msg) {
					switch(msg.what){
					case VERSION_CHECK_SUCCESS:
						saveLastUpdateTimeToFile(context, System.currentTimeMillis());
						showUpdateConfirmDialog(context,msg.getData().getString("updateUrl"));
						break;
					case VERSION_CHECK_START:
					case VERSION_CHECK_FAILED:
						break;
					case VERSION_CHECK_ALREADY_LATEST:
						saveLastUpdateTimeToFile(context, System.currentTimeMillis());
						break;
					}
				}
			});
		}
	}

	public static void checkUpdateNow(final Context context) {
		checkUpdate(context,new Handler(){
			public void handleMessage(Message msg) {

				switch(msg.what){
				case VERSION_CHECK_START:
					Toast.makeText(context, R.string.version_check_start, Toast.LENGTH_SHORT).show();
					break;
				case VERSION_CHECK_SUCCESS:
					saveLastUpdateTimeToFile(context, System.currentTimeMillis());
					showUpdateConfirmDialog(context,msg.getData().getString("updateUrl"));
					break;
				case VERSION_CHECK_FAILED:
					Toast.makeText(context, R.string.version_check_failed, Toast.LENGTH_SHORT).show();
					break;
				case VERSION_CHECK_ALREADY_LATEST:
					saveLastUpdateTimeToFile(context, System.currentTimeMillis());
					Toast.makeText(context, R.string.version_check_alreadylatest, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}
	
	public static void showUpdateConfirmDialog(final Context context,final String updateUrl){
		new AlertDialog.Builder(context)
		.setTitle(R.string.app_name)
		.setMessage(R.string.version_update_confirm)
		.setPositiveButton(R.string.button_ok, 
				new DialogInterface.OnClickListener(){
			        public void onClick(DialogInterface paramDialogInterface, int paramInt){
			        	Intent intent = new Intent(context, DownloadActivity.class);
			        	intent.putExtra("url", updateUrl);
			        	context.startActivity(intent);
		}}).	setNegativeButton(R.string.button_cancel, null).show();
	}
	

	public static void checkUpdate(final Context context,final Handler handler) {
		handler.sendEmptyMessage(VERSION_CHECK_START);
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
									long versionCode = jsonData.getLong("versionCode");
									String version = jsonData.getString("version");
									String updateUrl = jsonData.getString("updateUrl");
									long nowVersionCode = PackageUtils.getVersionCode(context);
									
									
									if(versionCode>nowVersionCode){
										Message msg = new Message();
										msg.what = VERSION_CHECK_SUCCESS;
										msg.getData().putString("updateUrl", updateUrl);
										msg.getData().putString("version", version);
										handler.sendMessage(msg);
										return;
									}else{
										handler.sendEmptyMessage(VERSION_CHECK_ALREADY_LATEST);
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
						handler.sendEmptyMessage(VERSION_CHECK_FAILED);
						return;
					}
				})
				.addParam("cid", ClientUtils.getClientId(context))
				.addParam("aid", PackageUtils.getAppId(context))
				.addParam("req", "getUpdate")
				.syncConnect();
			}
		}).start();
	}
	
	private static long getLastUpdateTimeFromFile(Context context){
		try { 
			BufferedReader os =   new BufferedReader(new InputStreamReader(context.openFileInput("lastupdatetime"),"UTF-8"));
			String line = os.readLine();
			if(line != null){
				return Long.parseLong(line);
			}
			os.close();
		} catch (NumberFormatException e){
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} 
		return 0;
	}
	
	private static void saveLastUpdateTimeToFile(Context context,long lastUpdateTime){
		try { 
			FileOutputStream out = context.openFileOutput("lastupdatetime", Context.MODE_PRIVATE) ; 
			out.write(Long.toString(lastUpdateTime).getBytes("UTF-8"));
			out.close(); 
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	}
}
