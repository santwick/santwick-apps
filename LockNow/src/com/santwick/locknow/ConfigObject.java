package com.santwick.locknow;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ConfigObject {
	private boolean isCompatibleMode = false;
	
	public ConfigObject(){
		if(android.os.Build.VERSION.RELEASE.equals("4.0.4")
				|| android.os.Build.VERSION.RELEASE.equals("4.1.1")){
			isCompatibleMode = true;
		}
	}

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
			json.put("isCompatibleMode", isCompatibleMode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			isCompatibleMode = json.getBoolean("isCompatibleMode");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean isCompatibleMode() {
		return isCompatibleMode;
	}

	public void setCompatibleMode(boolean isCompatibleMode) {
		this.isCompatibleMode = isCompatibleMode;
	}

}
