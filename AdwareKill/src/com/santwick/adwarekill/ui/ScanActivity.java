package com.santwick.adwarekill.ui;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.santwick.adwarekill.DataCenter;
import com.santwick.adwarekill.R;
import com.santwick.adwarekill.ScanEngine;
import com.santwick.adwarekill.object.AdwareObject;
import com.santwick.ui.UIActivity;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

@SuppressLint("HandlerLeak")
public class ScanActivity extends UIActivity implements OnItemClickListener{
	
	private List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter;
	private int scanState = ScanEngine.SCAN_START;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        
        this.setBackTips(R.string.header_back_back);
        this.addExtraButton(R.string.header_rescan, new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(scanState == ScanEngine.SCAN_END){
					startScan();
				}
			}
        	
        });
         
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

        
    	startScan();
    	DataCenter.setOnPackageRemovedHandler(new Handler(){
    		public void handleMessage(Message msg) {  
    			String packName = (String) msg.getData().get("packName");
    			
    			for(int i=0;i<itemList.size();i++){
    				if(itemList.get(i).get("packName").equals(packName)){
    					
    					AdwareObject adwareObject = DataCenter.getAdwareMap().get(packName);
    					if(adwareObject != null){
        					DataCenter.getHistoryList(ScanActivity.this).getAdwareMap().put(
        							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date()), adwareObject);
        					DataCenter.saveHistoryList(ScanActivity.this);
    					}
    					
    					itemList.remove(i);
    					adapter.notifyDataSetChanged();
    					((TextView)findViewById(R.id.textViewInfo)).setText(
    	        			String.format(getResources().getString(R.string.scan_info_end), itemList.size())	);
    				}
    			}
    		}
    	});
    }


    private void startScan(){
    	ScanEngine.scanAll(this, new Handler(){
        	public void handleMessage(Message msg) {  
        		scanState = msg.what;
        		switch(msg.what){
        		case ScanEngine.SCAN_START:

        	    	((TextView)findViewById(R.id.textViewInfo)).setText(getResources().getString(R.string.scan_info_start));
        	    	itemList.clear();
        	    	adapter.notifyDataSetChanged();
        			break;
        		case ScanEngine.SCAN_RUN:
        			((TextView)findViewById(R.id.textViewInfo)).setText(
        					String.format(getResources().getString(R.string.scan_info_scaning),
        							msg.getData().getInt("scaned"),
        							msg.getData().getInt("total")));
        			break;
        		case ScanEngine.SCAN_FIND:
    	    		HashMap<String,Object> map = new HashMap<String,Object>();
    	    		
    	    		map.put("icon", ScanEngine.getIcon(ScanActivity.this, (String) msg.getData().get("packName")));
    	    		map.put("name", msg.getData().get("name"));
    	    		map.put("packName", msg.getData().get("packName"));
    	    		map.put("adString", msg.getData().get("adString"));
    	    		itemList.add(map);
    	    		adapter.notifyDataSetChanged();

        			break;
        		case ScanEngine.SCAN_END:
            		
            		((TextView)findViewById(R.id.textViewInfo)).setText(
            				String.format(getResources().getString(R.string.scan_info_end), itemList.size())	);

        			break;
        		}
        	}
        });
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		
		new AlertDialog.Builder(this).
		setTitle(R.string.menu_title).
		setItems(new String[] { getResources().getString(R.string.menu_uninstall), 
				getResources().getString(R.string.menu_movetoignore), 
				getResources().getString(R.string.menu_search) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					Intent localIntent = new Intent("android.intent.action.DELETE");
				    localIntent.setData(Uri.parse("package:" + itemList.get(arg2).get("packName")));
				    startActivity(localIntent);
				}else if(which==1){

					DataCenter.getIgnoreList(ScanActivity.this).getAdwareSet().add((String) itemList.get(arg2).get("packName"));
					DataCenter.saveIgnoreList(ScanActivity.this);
					itemList.remove(arg2);
					adapter.notifyDataSetChanged();
					((TextView)findViewById(R.id.textViewInfo)).setText(
	        				String.format(getResources().getString(R.string.scan_info_end), itemList.size())	);
					
				}else if(which==2){
					Uri uri = Uri.parse(String.format(getResources().getString(R.string.search_url),
							ScanEngine.getName(ScanActivity.this, (String) itemList.get(arg2).get("packName"))));  
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
