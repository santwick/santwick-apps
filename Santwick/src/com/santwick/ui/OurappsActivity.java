package com.santwick.ui;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.santwick.graphics.FormatConvert;
import com.santwick.net.HttpClientUtil;
import com.santwick.net.NetClientCallback;
import com.santwick.object.AppObject;
import com.santwick.ui.SettingItem.OnItemClickListener;
import com.santwick.utils.PackageUtils;
import com.santwick.utils.UpdateUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

@SuppressLint("HandlerLeak")
public class OurappsActivity extends SettingActivity {
	
	private ArrayList<AppObject> appList = new ArrayList<AppObject>();
	private ArrayList<SettingItem> itemList = new ArrayList<SettingItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setBackTips(R.string.header_back_back);
		this.addView(R.layout.activity_ourapps);
		
		UpdateUtils.getOurApps(this, new Handler(){
			public void handleMessage(Message msg) {
				switch(msg.what){
				case UpdateUtils.GET_OURAPPS_SUCCESS:
					try {
						JSONArray jsonData = new JSONArray(msg.getData().getString("jsonData"));
						for(int i=0;i<jsonData.length();i++){
							JSONObject jsonObject = jsonData.getJSONObject(i);
							AppObject appObject = new AppObject();
							appObject.setAppId(jsonObject.getString("appid"));
							appObject.setAppName(jsonObject.getString("name"));
							appObject.setAppDesc(jsonObject.getString("desc"));
							appObject.setPackName(jsonObject.getString("pack"));
							appObject.setVersion(jsonObject.getString("ver"));
							appObject.setVersionCode(jsonObject.getInt("vercode"));
							appObject.setIconUrl(jsonObject.getString("iconurl"));
							appObject.setDownUrl(jsonObject.getString("downurl"));
							
							appList.add(appObject);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case UpdateUtils.GET_OURAPPS_FAILED:
					break;
				}
				if(appList.isEmpty()){
					OurappsActivity.this.findViewById(R.id.textViewNoItem).setVisibility(View.VISIBLE);
				}else{
					SettingGroup appListGroup = new SettingGroup(OurappsActivity.this);
					for(int i=0;i<appList.size();i++){
						AppObject appObject = appList.get(i);
						SettingItem item = new SettingItem(OurappsActivity.this,appObject.getAppName()+"(v"+appObject.getVersion()+")");
						itemList.add(item);
						appListGroup.addSettingItem(item);
						
						new HttpClientUtil(appObject.getIconUrl(),HttpClientUtil.METHOD_GET,new NetClientCallback(){
							@Override
							public void execute(int status, String response) {
								handlerRefresh.sendEmptyMessage(1);
							}
						}).asyncDownloadTo(appObject.getIconPath(), false);
					}
					OurappsActivity.this.addSettingGroup(appListGroup);
					handlerRefresh.sendEmptyMessage(1);
				}
				OurappsActivity.this.findViewById(R.id.progressBar).setVisibility(View.GONE);
			}
		});
		
	}

	@Override
	protected void onDestroy() {
		appList.clear();
		itemList.clear();
		super.onDestroy();
	}
	
	Handler handlerRefresh = new Handler(){
		public void handleMessage(Message msg) {
			for(int i=0;i<appList.size();i++){
				AppObject appObject = appList.get(i);
				SettingItem item = itemList.get(i);
				item.setSummary(appObject.getAppDesc());
				
				item.setIcon(FormatConvert.drawableFromFile (OurappsActivity.this, appObject.getIconPath() , 32 , 32 ));
				
				int versionCode = PackageUtils.getVersionCode(OurappsActivity.this,appObject.getPackName());
				if(versionCode >= appObject.getVersionCode()){
					item.setHint("已安装");
					//item.setState(SettingItem.STATE_NONE);
					item.setOnItemClickListener(null);
				}else {
					item.setOnItemClickListener(new OnItemClickListener(){
						@Override
						public void onClick(SettingItem item, View v) {
							for(int i=0;i<itemList.size();i++){
								if(item==itemList.get(i)){
									final String url = appList.get(i).getDownUrl();
									new AlertDialog.Builder(OurappsActivity.this)
									.setTitle(appList.get(i).getAppName())
									.setMessage(R.string.app_download_confirm)
									.setPositiveButton(R.string.button_ok, 
											new DialogInterface.OnClickListener(){
										        public void onClick(DialogInterface paramDialogInterface, int paramInt){
										        	Intent intent = new Intent(OurappsActivity.this, DownloadActivity.class);
										        	intent.putExtra("url",  url);
										        	OurappsActivity.this.startActivity(intent);
									}}).	setNegativeButton(R.string.button_cancel, null).show();
									
						        	break;
								}
							}
						}
					});
					if(versionCode==-1){
						item.setHint("未安装");
					}else{
						item.setHint("有更新");
					}
					//item.setState(SettingItem.STATE_MORE);
				}
			}
		}
	};


	@Override
	protected void onResume() {
		handlerRefresh.sendEmptyMessage(1);
		super.onResume();
	}
}
