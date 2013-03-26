package com.santwick.flowwidget;

import com.santwick.flowwidget.object.ConfigObject;

import android.content.Context;

public class DataCenter {
	private static ConfigObject config = null;
	
	public static ConfigObject getConfigObject(Context context){
		if(config==null){
			config = new ConfigObject();
			config.fromJSONFile(context, "config");
		}
		return config;
	}
}
