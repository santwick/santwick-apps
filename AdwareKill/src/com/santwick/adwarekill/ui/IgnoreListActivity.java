package com.santwick.adwarekill.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.santwick.adwarekill.DataCenter;
import com.santwick.adwarekill.R;
import com.santwick.adwarekill.ScanEngine;
import com.santwick.adwarekill.object.AdwareObject;
import com.santwick.graphics.FormatConvert;
import com.santwick.ui.UIActivity;

@SuppressLint("HandlerLeak")
public class IgnoreListActivity extends UIActivity  implements OnItemClickListener{
	
	private List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setBackTips(R.string.header_back_back);
		
		setContentView(R.layout.activity_scan);
		
		adapter = new SimpleAdapter(this, itemList, R.layout.listitem, 
        		new String[]{"name","adString","icon"}, 
        		new int[]{R.id.textViewName,R.id.textViewAdName,R.id.imageView});
        adapter.setViewBinder(new ViewBinder(){
            public boolean setViewValue(View view,Object data,String textRepresentation){
                if(view instanceof ImageView && data instanceof Drawable){
                    ImageView iv=(ImageView)view;
                    iv.setImageDrawable((Drawable)data);
                    return true;
                }
                else return false;
            }
        });
        
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);
        ((ListView)findViewById(R.id.listView)).setOnItemClickListener( this);
        
        HashSet<String> overdueSet = new HashSet<String>();
        
        for(String packName:DataCenter.getIgnoreList(this).getAdwareSet()){
        	String adString = ScanEngine.scanPack(this, packName);
        	if(adString.equals("")){
        		overdueSet.add(packName);
        	}else{
            	HashMap<String,Object> map = new HashMap<String,Object>();
        		
        		map.put("icon", ScanEngine.getIcon(IgnoreListActivity.this,  packName));
        		map.put("name", ScanEngine.getName(IgnoreListActivity.this,  packName));
        		map.put("packName", packName);
        		map.put("adString", adString);
        		itemList.add(map);
        		
        		AdwareObject adwareObject = new AdwareObject();
        		adwareObject.setName(ScanEngine.getName(IgnoreListActivity.this,  packName));
        		adwareObject.setPackName(packName);
        		adwareObject.setAdString(adString);
        		adwareObject.setIcon(FormatConvert.drawableToString(ScanEngine.getIcon(IgnoreListActivity.this,  packName)));
        		DataCenter.getAdwareMap().put(packName, adwareObject);
        	}
        }
        if(!overdueSet.isEmpty()){
        	for(String packName:overdueSet){
        		DataCenter.getIgnoreList(this).getAdwareSet().remove(packName);
        	}
        	DataCenter.saveIgnoreList(this);
        }
        
        ((TextView)findViewById(R.id.textViewInfo)).setText(
    			String.format(getResources().getString(R.string.ignore_info), itemList.size())	);

        adapter.notifyDataSetChanged();
        

    	DataCenter.setOnPackageRemovedHandler(new Handler(){
    		public void handleMessage(Message msg) {  
    			String packName = (String) msg.getData().get("packName");
    			for(int i=0;i<itemList.size();i++){
    				if(itemList.get(i).get("packName").equals(packName)){
    					DataCenter.getIgnoreList(IgnoreListActivity.this).getAdwareSet().remove(packName);
    					DataCenter.saveIgnoreList(IgnoreListActivity.this);
    					
    					AdwareObject adwareObject = DataCenter.getAdwareMap().get(packName);
    					if(adwareObject != null){
    						DataCenter.getHistoryList(IgnoreListActivity.this).getAdwareMap().put(
        							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date()), adwareObject);
        					DataCenter.saveHistoryList(IgnoreListActivity.this);
    					}

    					itemList.remove(i);
    					adapter.notifyDataSetChanged();
    					((TextView)findViewById(R.id.textViewInfo)).setText(
    	        			String.format(getResources().getString(R.string.ignore_info), itemList.size())	);
    					return;
    				}
    			}
    		}
    	});
		
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		new AlertDialog.Builder(this).
		setTitle(R.string.menu_title).
		setItems(new String[] { getResources().getString(R.string.menu_uninstall), 
				getResources().getString(R.string.menu_moveoutignore), 
				getResources().getString(R.string.menu_search) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					Intent localIntent = new Intent("android.intent.action.DELETE");
				    localIntent.setData(Uri.parse("package:" + itemList.get(arg2).get("packName")));
				    startActivity(localIntent);
				}else if(which==1){
					DataCenter.getIgnoreList(IgnoreListActivity.this).getAdwareSet().remove(itemList.get(arg2).get("packName"));
					DataCenter.saveIgnoreList(IgnoreListActivity.this);
					itemList.remove(arg2);
					adapter.notifyDataSetChanged();
					((TextView)findViewById(R.id.textViewInfo)).setText(
    	        			String.format(getResources().getString(R.string.ignore_info), itemList.size())	);
				}else if(which==2){
					Uri uri = Uri.parse(String.format(getResources().getString(R.string.search_url),
							ScanEngine.getName(IgnoreListActivity.this, (String) itemList.get(arg2).get("packName"))));  
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
					startActivity(intent);
				}
			}
		}).setNegativeButton(R.string.button_cancel, null).show();
		
		
	}
	
	@Override
	protected void onDestroy() {
		DataCenter.setOnPackageRemovedHandler(null);
		super.onDestroy();
	}

}
