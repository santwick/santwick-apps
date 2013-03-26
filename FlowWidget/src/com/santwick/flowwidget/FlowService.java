package com.santwick.flowwidget;


import java.io.BufferedReader; 
import java.io.FileNotFoundException; 
import java.io.FileReader; 
import java.io.IOException; 
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar; 

import com.santwick.flowwidget.R;
import com.santwick.flowwidget.object.ConfigObject;
import com.santwick.flowwidget.object.FlowObject;
import com.santwick.flowwidget.ui.ConfigActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service; 
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent; 
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler; 
import android.os.IBinder; 
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;


public class FlowService extends Service { 
	
	
	private Handler objHandler = new Handler(); 


	private int mMonth; 
	private int mDay; 

	private FlowObject dayFlowData = new FlowObject();
	private FlowObject monFlowData = new FlowObject();
	private FlowObject lastFlowData = new FlowObject();
	
	private ConfigObject config;
	
	private AppWidgetManager manager;  
	private ComponentName widget;
	private RemoteViews updateViews ;
	
	private Boolean isGprsOpen = false;
	
	public static final String DEV_FILE = "/proc/self/net/dev";// 系统流量文件 


	private Runnable mTasks = new Runnable() { 
		public void run()// 运行该服务执行此函数 
		{ 
	       	synchronized(isGprsOpen){
        		refresh();
	        } 
			objHandler.postDelayed(mTasks, 10*1000);// 每10秒执行一次 
		} 
	}; 
	
	@Override 
	public void onStart(Intent intent, int startId) { 
		log("onStart");
		super.onStart(intent, startId); 
	} 
	
	private BroadcastReceiver connectChangedReceriver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isMobileConnect = false;
			ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        if(networkInfo!=null && networkInfo.getState() == NetworkInfo.State.CONNECTED ){
	        	isMobileConnect = true;
	        }
        	synchronized(isGprsOpen){
        		refresh();
	        	isGprsOpen = isMobileConnect;
	        }
		}
		
	};
	
	@Override 
	public void onCreate() { 
		log("onCreate");
				
		Calendar calendar = Calendar.getInstance(); 
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mMonth  = calendar.get(Calendar.MONTH);
		
		config = DataCenter.getConfigObject(this);
		
		monFlowData.fromJSONFile(this, calendar.get(Calendar.YEAR)+"-"+mMonth );
		dayFlowData.fromJSONFile(this, calendar.get(Calendar.YEAR)+"-"+mMonth+"-"+mDay);
		lastFlowData = getCurrentFlowData();// 读取本次开机之后直到当前系统的总流量 

		manager = AppWidgetManager.getInstance(this);  
		widget = new ComponentName(this, FlowWidget.class);
		updateViews =   new RemoteViews(this.getPackageName(), R.layout.widget_flow);
		Intent intentClick = new Intent(this,ConfigActivity.class);  
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);  
        updateViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);  
        updateViews.setViewVisibility(R.id.layout_progress, config.isWidgetProgress() ? View.VISIBLE :  View.GONE);
		
		refreshWidget();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(connectChangedReceriver, filter);
		
		objHandler.postDelayed(mTasks, 0); 
		
		super.onCreate(); 
	} 
	
	@Override 
	public IBinder onBind(Intent intent) { 
		return null; 
	} 
	
	@Override 
	public void onDestroy() { 
	/* */ 
		log("onDestroy");
		objHandler.removeCallbacks(mTasks); 
		
		unregisterReceiver(connectChangedReceriver);
		
		super.onDestroy(); 
	} 
	
	public void log(String str) { 
		Log.i("FlowService", str);
	} 
	
	public FlowObject getCurrentFlowData() { 
		FlowObject flowData = new FlowObject();
		FileReader fstream = null; 
		try { 
			fstream = new FileReader(DEV_FILE); 
		} catch (FileNotFoundException e) { 
			log("Could not read " + DEV_FILE); 
			return flowData;
		}
		
		BufferedReader in = new BufferedReader(fstream); 
		ArrayList<String> lineList = new ArrayList<String>();

	    try {
	    	String line; 
		    while ((line = in.readLine()) != null){
		    	lineList.add(line);
		    }
			fstream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    for(String line:lineList){
	    	
			String[] segs = line.trim().split(":"); 
			if(segs.length!=2){
				continue;
			}
			
			String[] netdata = segs[1].trim().split(" +"); 
			
			if(netdata.length!=16){
				continue;
			}

			 if ((line.indexOf("rmnet")!=-1) || (line.indexOf("pdp")!=-1)) { 
				flowData.setGprsRecv(flowData.getGprsRecv()+Long.parseLong(netdata[0]));
				flowData.setGprsSend(flowData.getGprsSend()+Long.parseLong(netdata[8]));
			}
//			 else if (line.indexOf("eth")!=-1) { 
//				flowData.setEthRecv(flowData.getEthRecv()+Long.parseLong(netdata[0]));
//				flowData.setEthSend(flowData.getEthSend()+Long.parseLong(netdata[8]));
//			} else if (line.indexOf("wlan")!=-1) { 
//				flowData.setWifiRecv(flowData.getWifiRecv()+Long.parseLong(netdata[0]));
//				flowData.setWifiSend(flowData.getWifiSend()+Long.parseLong(netdata[8]));
//			} 
	    }
		
		return flowData;
	} 
	

	
	public void refresh() { 
	
		// 读取本次开机之后直到当前系统的总流量 
		FlowObject curFlowData = getCurrentFlowData();
		
		if(!isGprsOpen){
			lastFlowData = curFlowData;
			refreshWidget();
			return;
		}

		//计算差值-->计算优惠
		FlowObject deltaFlowData = curFlowData.delta(lastFlowData).discount(config);
		lastFlowData = curFlowData;
		
		boolean bDayChanged = false;
		long lastMonthGprs = monFlowData.getGprs();
		Calendar calendar = Calendar.getInstance(); 
		if(mDay==calendar.get(Calendar.DAY_OF_MONTH)){
			dayFlowData.addDelta(deltaFlowData);
		}else{
			dayFlowData = new FlowObject();
			dayFlowData.addDelta(deltaFlowData);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
			bDayChanged = true;
		}
		if(mMonth==calendar.get(Calendar.MONTH)){
			monFlowData.addDelta(deltaFlowData);
		}else{
			monFlowData = new FlowObject();
			monFlowData.addDelta(deltaFlowData);
			mMonth  = calendar.get(Calendar.MONTH);
		}
		
		if(deltaFlowData.isEmpty() && !bDayChanged){
			return;
		}
		
		if(config.getMonthLimit() >0){
			
			int flag = 0;
			if(config.isAlermOn(ConfigObject.ALERM_25) &&  lastMonthGprs < config.getMonthLimit()*1024*1024/4 && monFlowData.getGprs() >= config.getMonthLimit()*1024*1024/4){
				flag =1;
			}else if(config.isAlermOn(ConfigObject.ALERM_50) &&  lastMonthGprs < config.getMonthLimit()*1024*1024/2 && monFlowData.getGprs() >= config.getMonthLimit()*1024*1024/2){
				flag =2;
			}else if(config.isAlermOn(ConfigObject.ALERM_75) &&  lastMonthGprs < config.getMonthLimit()*1024*1024*3/4 && monFlowData.getGprs() >= config.getMonthLimit()*1024*1024*3/4){
				flag =3;
			}else if(config.isAlermOn(ConfigObject.ALERM_100) &&  lastMonthGprs < config.getMonthLimit()*1024*1024 && monFlowData.getGprs() >= config.getMonthLimit()*1024*1024){
				flag =4;
			}
			if(flag > 0){
				Notification mNotification = new Notification(R.drawable.ic_launcher,getResources().getString(R.string.notify_flow_title),System.currentTimeMillis());
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			    NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
			    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
			    mNotification.setLatestEventInfo(this, getResources().getString(R.string.notify_flow_title), 
			    		String.format(getResources().getString(R.string.notify_flow_content_over), monFlowData.getGprs()/1024.0/1024.0,config.getMonthLimit(),25*flag), contentIntent);  
	            mNotificationManager.notify(0, mNotification);  
			}
			
			flag = 0;
			if(config.isAlermOn(ConfigObject.ALERM_5M) &&  lastMonthGprs < config.getMonthLimit()*1024*1024 - 5*1024*1024 && monFlowData.getGprs() >= config.getMonthLimit()*1024*1024 - 5*1024*1024){
				flag =5;
			}else if(config.isAlermOn(ConfigObject.ALERM_1M) &&  lastMonthGprs < config.getMonthLimit()*1024*1024 - 1*1024*1024 && monFlowData.getGprs() >= config.getMonthLimit()*1024*1024 - 1*1024*1024){
				flag =1;
			}
			if(flag > 0){
				Notification mNotification = new Notification(R.drawable.ic_launcher,getResources().getString(R.string.notify_flow_title),System.currentTimeMillis());
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			    NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
			    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
			    mNotification.setLatestEventInfo(this, getResources().getString(R.string.notify_flow_title), 
			    		String.format(getResources().getString(R.string.notify_flow_content_left), monFlowData.getGprs()/1024.0/1024.0,flag), contentIntent);  
	            mNotificationManager.notify(0, mNotification);  
			}
			
			
			if(config.isEnableDisconnect() && monFlowData.getGprs()>=config.getMonthLimit()*1024*1024){

				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				Class<? extends ConnectivityManager> ownerClass = connectivityManager.getClass();
				try {
					ownerClass.getMethod("setMobileDataEnabled",new Class[]{boolean.class}).invoke(connectivityManager, false);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
	            
			}
		}

		monFlowData.toJSONFile(this, calendar.get(Calendar.YEAR)+"-"+mMonth );
		dayFlowData.toJSONFile(this, calendar.get(Calendar.YEAR)+"-"+mMonth+"-"+mDay );
		
		refreshWidget();
	} 
	
	private String[] formatFlow(long flow){
		String[] flowstring = new String[2];
		if(flow >= ((long)99)*1024*1024*1024){
			flowstring[0] = (new DecimalFormat("000")).format(flow/1024.0/1024.0/1024.0);
			flowstring[1] = "G";
		}else if(flow >=999*1024*1024){
			flowstring[0] = (new DecimalFormat("0.0")).format(flow/1024.0/1024.0/1024.0);
			flowstring[1] = "G";
		}else if(flow >=99*1024*1024){
			flowstring[0] = (new DecimalFormat("000")).format(flow/1024.0/1024.0);
			flowstring[1] = "M";
		}else if(flow >=999*1024){
			flowstring[0] = (new DecimalFormat("0.0")).format(flow/1024.0/1024.0);
			flowstring[1] = "M";
		}else if(flow >=99*1024){
			flowstring[0] =(new DecimalFormat("000")).format(flow/1024.0);
			flowstring[1] = "K";
		}else if(flow >=1000){
			flowstring[0] =(new DecimalFormat("0.0")).format(flow/1024.0);
			flowstring[1] = "K";
		}else{
			flowstring[0] = Long.toString(flow) ;
			flowstring[1] = "B";
		}
		return flowstring;
	}
	public void refreshWidget(){
		if(config.isWidgetProgress()){
			if(config.getMonthLimit()>0){
				if(monFlowData.getGprs() >= config.getMonthLimit()*1024*1024) {
					updateViews.setInt(R.id.progressBar, "setProgress", 100);
				}else{
					updateViews.setInt(R.id.progressBar, "setProgress", (int) (monFlowData.getGprs()*100/(config.getMonthLimit()*1024*1024)));
				}
			}else{
				updateViews.setInt(R.id.progressBar, "setProgress", 0);
			}
		}
		String[] daystring = formatFlow(dayFlowData.getGprs());
		String[] monstring = formatFlow(monFlowData.getGprs());
		
		
		updateViews.setTextViewText(R.id.textViewDay, daystring[0]);
		updateViews.setTextViewText(R.id.textViewDayUnit, daystring[1]);
		updateViews.setTextViewText(R.id.textViewMon, monstring[0]);
		updateViews.setTextViewText(R.id.textViewMonUnit, monstring[1]);

		manager.updateAppWidget(widget, updateViews); 
	}
	
}