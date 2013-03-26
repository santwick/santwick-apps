package com.santwick.adwarekill.object;

import org.json.JSONException;
import org.json.JSONObject;


public class AdwareObject  {

	private String icon;
	private String name;
	private String packName;
	private String adString;
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public String getAdString() {
		return adString;
	}
	public void setAdString(String adString) {
		this.adString = adString;
	}
	
	public String toJSONString(){
		JSONObject json = new JSONObject();
		try {
			json.put("icon", icon);
			json.put("name", name);
			json.put("packName", packName);
			json.put("adString", adString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			
			icon = json.getString("icon");
			name = json.getString("name");
			packName = json.getString("packName");
			adString = json.getString("adString");
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
