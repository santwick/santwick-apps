package com.santwick.adwarekill.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;

public class IgnoreListObject {
	private HashSet<String> adwareSet = new HashSet<String>();

	public HashSet<String> getAdwareSet() {
		return adwareSet;
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
		JSONArray jsonArray = new JSONArray();
	
		for(String packName:adwareSet){
			jsonArray.put(packName);
		}
		return jsonArray.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONArray jsonArray = new JSONArray(string);
			for(int i=0; i<jsonArray.length(); i++){
				adwareSet.add(jsonArray.getString(i));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
