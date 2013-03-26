package com.santwick.adwarekill.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ConfigObject {
	private boolean isCheckNewApp = true;
	private boolean isUpdateRules = true;
	private boolean isUpdateRulesOnlyWifi = true;

	
	public void fromJSONFile(Context context,String path) { 

		try { 
			BufferedReader os =   new BufferedReader(new InputStreamReader(context.openFileInput(path),"UTF-8"));
			String line = os.readLine();
			if(line != null){
				fromJSONString(line);
			}
			os.close();
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	
	public void toJSONFile(Context context,String path) { 
		try { 
			FileOutputStream out = context.openFileOutput(path, Context.MODE_PRIVATE) ; 
			out.write(toJSONString().getBytes("UTF-8"));
			out.close(); 
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	} 
	
	public String toJSONString(){
		JSONObject json = new JSONObject();
		try {
			json.put("isCheckNewApp", isCheckNewApp);
			json.put("isUpdateRules", isUpdateRules);
			json.put("isUpdateRulesOnlyWifi", isUpdateRulesOnlyWifi);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			isCheckNewApp = json.getBoolean("isCheckNewApp");
			isUpdateRules = json.getBoolean("isUpdateRules");
			isUpdateRulesOnlyWifi = json.getBoolean("isUpdateRulesOnlyWifi");


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isCheckNewApp() {
		return isCheckNewApp;
	}

	public void setCheckNewApp(boolean isCheckNewApp) {
		this.isCheckNewApp = isCheckNewApp;
	}

	public boolean isUpdateRules() {
		return isUpdateRules;
	}

	public void setUpdateRules(boolean isUpdateRules) {
		this.isUpdateRules = isUpdateRules;
	}

	public boolean isUpdateRulesOnlyWifi() {
		return isUpdateRulesOnlyWifi;
	}

	public void setUpdateRulesOnlyWifi(boolean isUpdateRulesOnlyWifi) {
		this.isUpdateRulesOnlyWifi = isUpdateRulesOnlyWifi;
	}

}
