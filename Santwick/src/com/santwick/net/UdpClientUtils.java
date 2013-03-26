package com.santwick.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;




public class UdpClientUtils {
	
	private static UdpClientUtils udpClientUtils  = new UdpClientUtils();
	public static UdpClientUtils getUdpClientUtils(){
		return udpClientUtils;
	}
	private DatagramSocket socket;
	private InetAddress server;
	private int serverport;
	private boolean bInited = false;
	private static Long maxRequestID = (long) 0; 
	
	public static class RequestObject{
		private long requestID = 0;
		private long timeout = 2000;
		private long starttime;
		private long retryLimit = 3;
		private long retry = 0;
		private NetClientCallback callback = null;
		private String strModule = null;
		private String strCommand = null;
		private JSONObject jsonParam = null;
		
		public RequestObject(){
			starttime =System.currentTimeMillis();
		}
		public RequestObject setCallback(NetClientCallback callback){
			this.callback = callback;
			if(callback!=null){
				synchronized(maxRequestID){
					requestID = ++maxRequestID;
				}
			}else{
				requestID = 0;
			}
			return this;
		}
		public RequestObject setTimeout(long timeout){
			this.timeout = timeout;
			return this;
		}
		public RequestObject setRetry(long retry){
			this.retryLimit = retry;
			return this;
		}
		public RequestObject setParam(String strKey,Object objValue){
			if(jsonParam==null){
				jsonParam = new JSONObject();
			}
			try {
				jsonParam.put(strKey, objValue);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return this;
		}
		public RequestObject setModule(String strModule){
			this.strModule = strModule;
			return this;
		}
		public RequestObject setCommand(String strCommand){
			this.strCommand = strCommand;
			return this;
		}
		public boolean isNeedRetry(){
			if(System.currentTimeMillis() - starttime > timeout*(retry+1)){
				retry++;
				return true;
			}else{
				return false;
			}
		}
		public boolean isTimeout(){
			return System.currentTimeMillis() - starttime > timeout*retryLimit;
		}
		public NetClientCallback getCallback() {
			return callback;
		}
		public long getRequestID(){
			return requestID;
		}
		public String toString(){
			JSONObject json = new JSONObject();
			try {
				if(requestID!=0){
					json.put("id", requestID);
				}
				if(strModule!=null){
					json.put("mod", strModule);
				}
				if(strCommand!=null){
					json.put("cmd", strCommand);
				}
				if(jsonParam!=null){
					json.put("param", jsonParam);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json.toString();
		}
	}
	private Map<Long,RequestObject> requestList = new HashMap<Long,RequestObject>();
	private Map<String,NetClientCallback> pushList = new HashMap<String,NetClientCallback>();

	public boolean Init(String servername,int serverport,int clientport){
		if(bInited){
			uninit();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			socket = new DatagramSocket(clientport);
			socket.setSoTimeout(1000);
			
			server = InetAddress.getByName(servername);
			this.serverport = serverport;
			bInited = true;
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					byte[] packdata = new byte[4096];
					DatagramPacket pack = new DatagramPacket(packdata, packdata.length);
					try {
						while(bInited){
							pack.setLength(packdata.length);
							try{
								socket.receive(pack);
								try{
									String strReplay = new String(pack.getData(),0,pack.getLength(),"UTF-8");
									JSONObject json = new JSONObject(strReplay);
									if(json.has("id") && json.has("result")){
										long requestId = json.getLong("id");
										synchronized(requestList){
											if(requestList.containsKey(requestId)){
												requestList.get(requestId).getCallback().execute(NetClientCallback.NET_SUCCESS,  json.getJSONObject("result").toString());
												requestList.remove(requestId);
											}
										}
									}else if(json.has("push") && json.has("msg")){
										String pushMark = json.getString("push");
										synchronized(pushList){
											if(pushList.containsKey(pushMark)){
												pushList.get(pushMark).execute(NetClientCallback.NET_SUCCESS,  json.getJSONObject("msg").toString());
											}
										}
									}
								}catch (JSONException e) {
									e.printStackTrace();
								}
							} catch (SocketTimeoutException  e) {
							}
							synchronized(requestList){
								List<Long> timeoutRequestIDList = new ArrayList<Long>();
								for(RequestObject request:requestList.values()){
									if(request.isTimeout()){
										timeoutRequestIDList.add(request.getRequestID());
									}
								}
								for(Long requestID:timeoutRequestIDList){
									requestList.get(requestID).getCallback().execute(NetClientCallback.NET_TIMEOUT,null);
									requestList.remove(requestID);
								}
								timeoutRequestIDList.clear();
								
								for(RequestObject requestObject:requestList.values()){
									if(requestObject.isNeedRetry()){
										sendRequest(requestObject);
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}).start();
		} catch (SocketException e) {
			bInited = false;
			e.printStackTrace();
		} catch (UnknownHostException e) {
			bInited = false;
			e.printStackTrace();
		}
		return bInited;
	}
	public void uninit(){
		if(bInited){
			bInited = false;
			socket.close();
		}
	}

	public void request(RequestObject requestObject){
		if(!bInited){
			if(requestObject.getCallback()!=null){
				requestObject.getCallback().execute(NetClientCallback.NET_FAILED, null);
			}
			return;
		}
		if(requestObject.getRequestID()!=0){
			synchronized(requestList){
				requestList.put(requestObject.getRequestID(), requestObject);
			}
		}
		sendRequest(requestObject);
	}
	
	private void sendRequest(RequestObject requestObject){
		try {
			byte[] requestData = requestObject.toString().getBytes("UTF-8");
			socket.send(new DatagramPacket(requestData, requestData.length,  server, serverport));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void registerPushHandler(String pushMark,NetClientCallback callback){
		synchronized(pushList){
			pushList.put(pushMark, callback);
		}
	}
	
	public void unregisterPushHandler(String pushMark){
		synchronized(pushList){
			pushList.remove(pushMark);
		}
	}
}
