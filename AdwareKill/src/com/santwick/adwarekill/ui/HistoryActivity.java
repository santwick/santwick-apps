package com.santwick.adwarekill.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.santwick.adwarekill.object.AdwareObject;
import com.santwick.graphics.FormatConvert;
import com.santwick.ui.UIActivity;

public class HistoryActivity extends UIActivity   implements OnItemClickListener{
	
	private List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setBackTips(R.string.header_back_back);
		
		setContentView(R.layout.activity_scan);
		
		adapter = new SimpleAdapter(this, itemList, R.layout.historylistitem, 
        		new String[]{"name","adString","icon","time"}, 
        		new int[]{R.id.textViewName,R.id.textViewAdName,R.id.imageView,R.id.textViewExtra});
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
        
        for(Entry<String,AdwareObject> entry:DataCenter.getHistoryList(this).getAdwareMap().entrySet()){
        	HashMap<String,Object> map = new HashMap<String,Object>();
    		
    		map.put("icon", FormatConvert.stringToDrawable(entry.getValue().getIcon()));
    		map.put("name", entry.getValue().getName());
    		map.put("packName", entry.getValue().getPackName());
    		map.put("adString", entry.getValue().getAdString());
    		map.put("time", entry.getKey());
    		itemList.add(map);
        }
        
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);
        ((ListView)findViewById(R.id.listView)).setOnItemClickListener( this);
        
        ((TextView)findViewById(R.id.textViewInfo)).setText(
    			String.format(getResources().getString(R.string.history_info), itemList.size())	);

        adapter.notifyDataSetChanged();
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		new AlertDialog.Builder(this).
		setTitle(R.string.menu_title).
		setItems(new String[] { getResources().getString(R.string.menu_clearlog), 
				getResources().getString(R.string.menu_search) }, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){

					DataCenter.getHistoryList(HistoryActivity.this).getAdwareMap().remove((String) itemList.get(arg2).get("time"));
					DataCenter.saveHistoryList(HistoryActivity.this);
					itemList.remove(arg2);
					adapter.notifyDataSetChanged();
					((TextView)findViewById(R.id.textViewInfo)).setText(
	        				String.format(getResources().getString(R.string.history_info), itemList.size())	);
					
				}else if(which==1){
					Uri uri = Uri.parse(String.format(getResources().getString(R.string.search_url),
							itemList.get(arg2).get("name")));  
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
					startActivity(intent);
				}
			}
		}).setNegativeButton(R.string.button_cancel, null).show();
		
		
		
	}

}
