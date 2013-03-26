package com.santwick.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;


import com.santwick.net.HttpClientUtil;
import com.santwick.net.NetClientCallback;
import com.santwick.ui.R;

import android.content.Context;
import android.telephony.TelephonyManager;

public class ClientUtils {

	public static String getClientId(Context context){

		int cid = 0;
		try{
			cid = Integer.parseInt(getClientIdFromFile(context));
		}catch(NumberFormatException e){
		}
		
		if(cid==0){
			
			try{
				cid = Integer.parseInt(getClientIdFromNet(context));
			}catch(NumberFormatException e){
				
			}
	
			if(cid != 0){
				saveClientIdToFile(context,Integer.toString(cid));
			}
		}
		return Integer.toString(cid);
	}
	
	private static String getClientIdFromNet(Context context){
		
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		final StringBuffer clientId = new StringBuffer();
		
		new HttpClientUtil(context.getResources().getString(R.string.update_url),HttpClientUtil.METHOD_GET,new NetClientCallback(){
			@Override
			public void execute(int status, String response) {
				switch(status){
				case NetClientCallback.NET_SUCCESS:
					try {
						JSONObject json = new JSONObject(response);
						if(json.getBoolean("success")){
							JSONObject jsonData = 	json.getJSONObject("data");
							clientId.append(jsonData.getString("clientId"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case NetClientCallback.NET_FAILED:
				case NetClientCallback.NET_TIMEOUT:
					break;
				}

				return;
			}
		})
		.addParam("did", tm.getDeviceId())
		.addParam("req", "getClientId")
		.syncConnect();
		
		return clientId.toString();
	}
	
	private static String getClientIdFromFile(Context context){
		try { 
			BufferedReader os =   new BufferedReader(new InputStreamReader(context.openFileInput("clientid"),"UTF-8"));
			String line = os.readLine();
			if(line != null){
				return line;
			}
			os.close();
		} catch (IOException e) { 
			e.printStackTrace();
		} 
		return "";
	}
	
	private static void saveClientIdToFile(Context context,String clientId){
		try { 
			FileOutputStream out = context.openFileOutput("clientid", Context.MODE_PRIVATE) ; 
			out.write(clientId.getBytes("UTF-8"));
			out.close(); 
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	}
}
