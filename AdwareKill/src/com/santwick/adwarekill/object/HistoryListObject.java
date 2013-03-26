package com.santwick.adwarekill.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;


import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;

public class HistoryListObject {

	private HashMap<String,AdwareObject> adwareMap = new HashMap<String,AdwareObject>();

	public HashMap<String,AdwareObject> getAdwareMap() {
		return adwareMap;
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
		for(Entry<String, AdwareObject> entry:adwareMap.entrySet()){
			try {
				json.put(entry.getKey(), entry.getValue().toJSONString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			
			for(int i=0;i<json.length();i++){
				String name = (String) json.names().get(i);
				AdwareObject adwareObject = new AdwareObject(); 
				adwareObject.fromJSONString(json.getString(name));
				adwareMap.put(name, adwareObject);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
