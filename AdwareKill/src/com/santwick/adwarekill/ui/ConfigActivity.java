package com.santwick.adwarekill.ui;


import com.santwick.adwarekill.DataCenter;
import com.santwick.adwarekill.R;
import com.santwick.adwarekill.UpdateService;
import com.santwick.adwarekill.object.ConfigObject;
import com.santwick.ui.AboutActivity;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.ui.SettingItem.OnItemClickListener;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;


public class ConfigActivity extends SettingActivity  {

	private ConfigObject config;
	
	private boolean isChanged = false;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("MainActivity"," onCreate");
        
        this.setBackTips(R.string.header_back_back);
        
        config = DataCenter.getConfigObject(this);
        
        this.addSettingGroup(new SettingGroup(this,R.string.config_normal_title)
        	.addSettingItem(
        			new SettingItem(this,R.string.config_normal_checknewapp)
        			.setState(config.isCheckNewApp()? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
        			.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(SettingItem item, View v) {
							config.setCheckNewApp(!config.isCheckNewApp());
							isChanged = true;
							item.setState(config.isCheckNewApp()? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
						}
        				
        			})
        		)
			
        	);
        
        this.addSettingGroup(new SettingGroup(this,R.string.config_update_title)
		.addSettingItem(
    			new SettingItem(this,R.string.config_update_rules)
    			.setState(config.isUpdateRules()? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
    			.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(SettingItem item, View v) {
						config.setUpdateRules(!config.isUpdateRules());
						isChanged = true;
						item.setState(config.isUpdateRules()? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
					}
    				
    			})
    		)
		.addSettingItem(
    			new SettingItem(this,R.string.config_update_wifi)
    			.setState(config.isUpdateRulesOnlyWifi()? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
    			.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(SettingItem item, View v) {
						config.setUpdateRulesOnlyWifi(!config.isUpdateRulesOnlyWifi());
						isChanged = true;
						item.setState(config.isUpdateRulesOnlyWifi()? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
					}
    				
    			})
    		)
    	);
        
        this.addSettingGroup(new SettingGroup(this,R.string.config_other_title)
        	.addSettingItem(new SettingItem(this,R.string.config_other_about)
        		.setState(SettingItem.STATE_MORE)
        		.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(SettingItem item, View v) {
						Intent intent = new Intent(ConfigActivity.this,AboutActivity.class);
						ConfigActivity.this.startActivity(intent);
					}
        			
        		})
        	
        	)
       );
    }
    
		
    
	@Override
	protected void onDestroy() {
		
		Log.i("MainActivity"," onDestroy");
		if(isChanged){
			config.toJSONFile(this, "config");
		}
		stopService(new Intent(this,UpdateService.class));
		if(config.isUpdateRules()){
			startService(new Intent(this,UpdateService.class));
		}

		super.onDestroy();
	}
}
