package com.santwick.flowwidget.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ConfigObject {
	
	public static final int ALERM_25  =  0x00000001;
	public static final int ALERM_50  =  0x00000002;
	public static final int ALERM_75  =  0x00000004;
	public static final int ALERM_100 =  0x00000008;
	public static final int ALERM_5M  =  0x00000010;
	public static final int ALERM_1M  =  0x00000020;
	
	public static final int DISCOUNT_NONE = 0;
	public static final int DISCOUNT_HALF = 1;
	public static final int DISCOUNT_FREE = 2;
	
	private int monthLimit = 0;
	private boolean enableAlerm = false;
	private boolean enableDisconnect = false;
	private int alerm = 0;
	private boolean widgetProgress = true;
	
	private int discountType = DISCOUNT_NONE;
	private int discountStartHour = 23;
	private int discountStartMin = 0;
	private int discountStopHour = 7;
	private int discountStopMin = 0;
	
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
			json.put("monthLimit", monthLimit);
			json.put("alerm", alerm);
			json.put("enableDisconnect", enableDisconnect);
			json.put("widgetProgress", widgetProgress);
			json.put("discountType", discountType);
			json.put("discountStartHour", discountStartHour);
			json.put("discountStartMin", discountStartMin);
			json.put("discountStopHour", discountStopHour);
			json.put("discountStopMin", discountStopMin);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			monthLimit = json.getInt("monthLimit");
			enableDisconnect = json.getBoolean("enableDisconnect");
			alerm = json.getInt("alerm");
			widgetProgress = json.getBoolean("widgetProgress");
			discountType = json.getInt("discountType");
			discountStartHour = json.getInt("discountStartHour");
			discountStartMin = json.getInt("discountStartMin");
			discountStopHour = json.getInt("discountStopHour");
			discountStopMin = json.getInt("discountStopMin");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public int getMonthLimit() {
		return monthLimit;
	}
	public void setMonthLimit(int monthLimit) {
		this.monthLimit = monthLimit;
	}
	public boolean isEnableAlerm() {
		return enableAlerm;
	}
	public void setEnableAlerm(boolean enableAlerm) {
		this.enableAlerm = enableAlerm;
	}
	public boolean isEnableDisconnect() {
		return enableDisconnect;
	}
	public void setEnableDisconnect(boolean enableDisconnect) {
		this.enableDisconnect = enableDisconnect;
	}
	public boolean isAlermOn(int alermType){
		return (alerm&alermType)!=0;
	}
	public void setAlerm(int alermType,boolean isOn){
		if(isOn){
			alerm |= alermType;
		}else{
			alerm &=~alermType;
		}
	}

	public boolean isWidgetProgress() {
		return widgetProgress;
	}

	public void setWidgetProgress(boolean widgetProgress) {
		this.widgetProgress = widgetProgress;
	}

	public int getDiscountType() {
		return discountType;
	}

	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}

	public int getDiscountStartHour() {
		return discountStartHour;
	}

	public void setDiscountStartHour(int discountStartHour) {
		this.discountStartHour = discountStartHour;
	}

	public int getDiscountStartMin() {
		return discountStartMin;
	}

	public void setDiscountStartMin(int discountStartMin) {
		this.discountStartMin = discountStartMin;
	}

	public int getDiscountStopHour() {
		return discountStopHour;
	}

	public void setDiscountStopHour(int discountStopHour) {
		this.discountStopHour = discountStopHour;
	}

	public int getDiscountStopMin() {
		return discountStopMin;
	}

	public void setDiscountStopMin(int discountStopMin) {
		this.discountStopMin = discountStopMin;
	}
	
}
