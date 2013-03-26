package com.santwick.flowwidget.object;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.santwick.utils.TimeUtils;

import android.content.Context;


public class FlowObject {
	private long gprsRecv = 0 ;
	private long gprsSend = 0 ;
	private long wifiRecv = 0 ;
	private long wifiSend = 0 ;
	private long ethRecv = 0 ;
	private long ethSend = 0 ;
	
	private boolean isDelta = false;
	
	
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
			json.put("gprsRecv", gprsRecv);
			json.put("gprsSend", gprsSend);
			json.put("wifiRecv", wifiRecv);
			json.put("wifiSend", wifiSend);
			json.put("ethRecv", ethRecv);
			json.put("ethSend", ethSend);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public void fromJSONString(String string){
		try {
			JSONObject json = new JSONObject(string);
			gprsRecv = json.getLong("gprsRecv");
			gprsSend = json.getLong("gprsSend");
			wifiRecv = json.getLong("wifiRecv");
			wifiSend = json.getLong("wifiSend");
			ethRecv = json.getLong("ethRecv");
			ethSend = json.getLong("ethSend");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public long getEth(){
		return ethRecv+ethSend;
	}
	/*
	 * 用户流量校正，无法确定是上行还是下行的，就算成下行流量
	 */
	public void correctGprs(double gprs){
		long flow = (long) (gprs*1024*1024);
		gprsRecv = flow - gprsSend;
	}
	
	public long getGprs(){
		return gprsRecv+gprsSend;
	}
	
	public long getWifi(){
		return wifiRecv+wifiSend;
	}
	
	public boolean isEmpty(){
		return (ethRecv==0) && (ethSend==0) && (gprsRecv==0) && (gprsSend==0) && (wifiRecv==0) && (wifiSend==0);
	}
	 
	public void addDelta(FlowObject deltaFlowData){
		this.ethRecv += deltaFlowData.ethRecv;
		this.ethSend += deltaFlowData.ethSend;
		this.gprsRecv += deltaFlowData.gprsRecv;
		this.gprsSend += deltaFlowData.gprsSend;
		this.wifiRecv += deltaFlowData.wifiRecv;
		this.wifiSend += deltaFlowData.wifiSend;
	}
	
	public FlowObject delta(FlowObject flowData){
		FlowObject deltaFlowData = new FlowObject();

		deltaFlowData.ethRecv = this.ethRecv>=flowData.ethRecv?this.ethRecv-flowData.ethRecv:this.ethRecv;
		deltaFlowData.ethSend = this.ethSend>=flowData.ethSend?this.ethSend-flowData.ethSend:this.ethSend;
		deltaFlowData.gprsRecv = this.gprsRecv>=flowData.gprsRecv?this.gprsRecv-flowData.gprsRecv:this.gprsRecv;
		deltaFlowData.gprsSend = this.gprsSend>=flowData.gprsSend?this.gprsSend-flowData.gprsSend:this.gprsSend;
		deltaFlowData.wifiRecv = this.wifiRecv>=flowData.wifiRecv?this.wifiRecv-flowData.wifiRecv:this.wifiRecv;
		deltaFlowData.wifiSend = this.wifiSend>=flowData.wifiSend?this.wifiSend-flowData.wifiSend:this.wifiSend;
		
		deltaFlowData.isDelta = true;
		return deltaFlowData;
	}
	
	//修正彩信流量 只有差值对象才能修正
	public FlowObject correctMMS(boolean isMMSOpen){
		if(isDelta && isMMSOpen ){
			gprsRecv = 0;
			gprsSend = 0;
		}
		return this;
	}
	
	
	//流量优惠 只有差值对象才能修正
	public FlowObject discount(ConfigObject config){
		if(isDelta && config.getDiscountType() != ConfigObject.DISCOUNT_NONE ){
			if(TimeUtils.timeBetween(config.getDiscountStartHour()*60 + config.getDiscountStartMin(),
					config.getDiscountStopHour()*60 + config.getDiscountStopMin())){
				double discount = 1;
				switch(config.getDiscountType()){
				case ConfigObject.DISCOUNT_NONE:
					discount = 1;
					break;
				case ConfigObject.DISCOUNT_HALF:
					discount = 0.5;
					break;
				case ConfigObject.DISCOUNT_FREE:
					discount = 0;
					break;
				default:
					discount = 1;
					break;	
				}
				gprsRecv *= discount;
				gprsSend *= discount;
			}
		}
		return this;
	}

	public long getGprsRecv() {
		return gprsRecv;
	}

	public void setGprsRecv(long gprsRecv) {
		this.gprsRecv = gprsRecv;
	}

	public long getGprsSend() {
		return gprsSend;
	}

	public void setGprsSend(long gprsSend) {
		this.gprsSend = gprsSend;
	}

	public long getWifiRecv() {
		return wifiRecv;
	}

	public void setWifiRecv(long wifiRecv) {
		this.wifiRecv = wifiRecv;
	}

	public long getWifiSend() {
		return wifiSend;
	}

	public void setWifiSend(long wifiSend) {
		this.wifiSend = wifiSend;
	}

	public long getEthRecv() {
		return ethRecv;
	}

	public void setEthRecv(long ethRecv) {
		this.ethRecv = ethRecv;
	}

	public long getEthSend() {
		return ethSend;
	}

	public void setEthSend(long ethSend) {
		this.ethSend = ethSend;
	}
}
