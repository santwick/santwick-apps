package com.santwick.adwarekill.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.santwick.adwarekill.DataCenter;
import com.santwick.adwarekill.R;
import com.santwick.adwarekill.UpdateService;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.ui.SettingItem.OnItemClickListener;
import com.santwick.utils.UpdateUtils;


@SuppressLint("HandlerLeak")
public class MainActivity extends SettingActivity {
	
	private SettingItem ignorelistItem;
	private SettingItem historyItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UpdateUtils.checkUpdateSchedule(this);
		
		this.setBackTips(R.string.header_back_exit);
        this.addExtraButton(R.string.header_config, new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
				MainActivity.this.startActivity(intent);
				
			}
        	
        });
        

        this.addView(R.layout.activity_main);
        findViewById(R.id.buttonCheckNow).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,ScanActivity.class);
				MainActivity.this.startActivity(intent);
				
			}
        	
        });
        
        
        
        this.addSettingGroup(new SettingGroup(this)
    		.addSettingItem(new SettingItem(this,R.string.main_adwarerule)
    			.setHint(DataCenter.getAdwareRuleObject(this).getUpdateDate())
    			.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(final SettingItem item, View v) {
						UpdateService.CheckUpdate(MainActivity.this, new Handler(){
							public void handleMessage(Message msg) {

								switch(msg.what){
								case UpdateService.UPDATE_START:
									Toast.makeText(MainActivity.this, R.string.update_rule_start, Toast.LENGTH_SHORT).show();
									break;
								case UpdateService.UPDATE_SUCCESS:
									item.setHint(DataCenter.getAdwareRuleObject(MainActivity.this).getUpdateDate());
									Toast.makeText(MainActivity.this, R.string.update_rule_success, Toast.LENGTH_SHORT).show();
									break;
								case UpdateService.UPDATE_FAILED:
									Toast.makeText(MainActivity.this, R.string.update_rule_failed, Toast.LENGTH_SHORT).show();
									break;
								case UpdateService.UPDATE_ALREADY_LATEST:
									Toast.makeText(MainActivity.this, R.string.update_rule_alreadylatest, Toast.LENGTH_SHORT).show();
									break;
								}
							}
						});
					}
    				
    			})
    		).addSettingItem(
    				ignorelistItem = new SettingItem(this,R.string.main_ignorelist)
    			.setState(SettingItem.STATE_MORE)
    			.setHint(Integer.toString(DataCenter.getIgnoreList(this).getAdwareSet().size()))
    			.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(SettingItem item, View v) {
						Intent intent = new Intent(MainActivity.this,IgnoreListActivity.class);
						MainActivity.this.startActivity(intent);
					}
    				
    			})
    		).addSettingItem(
    				historyItem = new SettingItem(this,R.string.main_history)
    			.setState(SettingItem.STATE_MORE)
    			.setHint(Integer.toString(DataCenter.getHistoryList(this).getAdwareMap().size()))
    			.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(SettingItem item, View v) {
						Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
						MainActivity.this.startActivity(intent);
					}
    				
    			})
    		)
		
    	);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ignorelistItem.setHint(Integer.toString(DataCenter.getIgnoreList(this).getAdwareSet().size()));
		historyItem.setHint(Integer.toString(DataCenter.getHistoryList(this).getAdwareMap().size()));
	}

}
