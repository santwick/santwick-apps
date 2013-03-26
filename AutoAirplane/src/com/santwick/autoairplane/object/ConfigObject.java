package com.santwick.autoairplane.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ConfigObject {
	
	public static final int AUTOMODE_DISABLE = 0x00;
	public static final int AUTOMODE_ENTER = 0x01;
	public static final int AUTOMODE_LEAVE = 0x02;
	public static final int AUTOMODE_BOTH = 0x03;
	
	private int autoMode = AUTOMODE_BOTH;
	private int startHour = 22;
	private int startMin = 30;
	private int stopHour = 7;
	private int stopMin = 30;
	private boolean isKeepWifi = false;
	
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
			json.put("autoMode", autoMode);
			json.put("startHour", startHour);
			json.put("startMin", startMin);
			json.put("stopHour", stopHour);
			json.put("stopMin", stopMin);
			json.put("isKeepWifi", isKeepWifi);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			startHour = json.getInt("startHour");
			startMin = json.getInt("startMin");
			stopHour = json.getInt("stopHour");
			stopMin = json.getInt("stopMin");
			autoMode = json.getInt("autoMode");
			isKeepWifi = json.getBoolean("isKeepWifi");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getStopHour() {
		return stopHour;
	}

	public void setStopHour(int stopHour) {
		this.stopHour = stopHour;
	}

	public int getStopMin() {
		return stopMin;
	}

	public void setStopMin(int stopMin) {
		this.stopMin = stopMin;
	}


	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMin() {
		return startMin;
	}

	public void setStartMin(int startMin) {
		this.startMin = startMin;
	}

	public int getAutoMode() {
		return autoMode;
	}

	public void setAutoMode(int autoMode) {
		this.autoMode = autoMode;
	}

	public boolean isKeepWifi() {
		return isKeepWifi;
	}

	public void setKeepWifi(boolean isKeepWifi) {
		this.isKeepWifi = isKeepWifi;
	}
	
	
	
	
	
}
