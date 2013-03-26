package com.santwick.adwarekill.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.santwick.adwarekill.R;

import android.content.Context;

public class AdwareRuleObject {
	private long version = 1001;
	private String updateDate ="2012-11-13";
	private HashMap<String, String> adwareList = new HashMap<String, String>(){/**
		 * 
		 */
		private static final long serialVersionUID = -1188653852209344380L;
		{
//			put("com.airpush.","Airpush");
//			put("com.Leadbolt.","Leadbolt");
//			put("com.appenda.","Appenda");
//			put("com.iac.notification.","IAC");
//			put("com.g3app.","g3app");
//			put("com.joe.activity.TestReceiver","Joe");
//			put("com.aichess.","Aichess");
//			put("com.uapush.","Uapush");
//			put("com.aseiei.apxxxx.","暗推");
//			put("com.waps.","万普广告");
//			put("com.kuguo.","酷果广告");
//			put("com.dianru.","点入广告");
//			put("net.miidi.","米迪广告");
//			put("cn.aduu.","优友广告");
//			put("com.doumob.","多盟广告");
//			put("com.doumob.","多盟广告");
//			put("android.common.appoffer.","安软园广告");
//			put("com.santwick.autoairplane","测试广告");
		};
	};
	
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
		if(adwareList.isEmpty()){
			fromRawJSONFile(context);
		}
	}
	
	public void fromRawJSONFile(Context context) { 
		try { 
			BufferedReader os =   new BufferedReader(new InputStreamReader(
					context.getResources().openRawResource(R.raw.rules)));
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
			json.put("version", version);
			json.put("updateDate", updateDate);
			JSONObject jsonAdwareList = new JSONObject();
			for(Entry<String,String> entry:adwareList.entrySet()){
				jsonAdwareList.put(entry.getKey(), entry.getValue());
			}
			json.put("adwareList", jsonAdwareList);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			version = json.getLong("version");
			updateDate = json.getString("updateDate");
			JSONObject jsonAdwareList = json.getJSONObject("adwareList");
			for(int i=0;i<jsonAdwareList.length();i++){
				String name = (String) jsonAdwareList.names().get(i);
				adwareList.put(name, jsonAdwareList.getString(name));
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public HashMap<String, String> getAdwareList() {
		return adwareList;
	}
	public void setAdwareList(HashMap<String, String> adwareList) {
		this.adwareList = adwareList;
	}
}
