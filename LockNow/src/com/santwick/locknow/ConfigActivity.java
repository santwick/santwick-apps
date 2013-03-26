package com.santwick.locknow;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.santwick.ui.AboutActivity;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.ui.SettingItem.OnItemClickListener;
import com.santwick.utils.PackageUtils;
import com.santwick.utils.UpdateUtils;

public class ConfigActivity extends SettingActivity {
	
	private DevicePolicyManager policyManager; 
    private ComponentName componentName;
    private SettingItem accessItem;
    private ConfigObject config = new ConfigObject();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UpdateUtils.checkUpdateSchedule(this);
		
		config.fromJSONFile(this, "config");
		this.setBackTips(R.string.header_back_exit);
        this.addExtraButton(R.string.header_share, new OnClickListener(){

			@Override
			public void onClick(View v) {
				PackageUtils.sharePackage(ConfigActivity.this);
			}
        	
        });
		
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE); 
        componentName = new ComponentName(this, LockReceiver.class); 

		 this.addSettingGroup(new SettingGroup(this,R.string.config_access_title)
	     	.addSettingItem(accessItem = new SettingItem(this,R.string.config_access_privileges)
	     		.setSummary(getResources().getString(R.string.config_access_summary))
	     		.setState(policyManager.isAdminActive(componentName)?SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE)
	     		.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(SettingItem item, View v) {
							if(policyManager.isAdminActive(componentName)){
								policyManager.removeActiveAdmin(componentName);
								item.setState(SettingItem.STATE_CHECK_FALSE);
							}else{
								Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN); 
						        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName); 
						        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.app_name)); 
						        startActivity(intent);
							}
						}
		     		})
		     	
		     	)
		    );
		 
		 TextView v = new TextView(this);
		 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 lp.setMargins(20, 0, 20, 20);
		 v.setLayoutParams(lp);
		 v.setTextColor(Color.RED);
		 v.setText(R.string.config_access_uninstall_title);
		 
		 this.addView(v);
        
		 this.addSettingGroup(new SettingGroup(this,R.string.config_compatible_title)
	     	.addSettingItem(new SettingItem(this,R.string.config_compatible_mode)
	     		.setState(config.isCompatibleMode()?SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE)
	     		.setSummary(getResources().getString(R.string.config_compatible_summary))
	     		.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(SettingItem item, View v) {
							config.setCompatibleMode(!config.isCompatibleMode());
							item.setState(config.isCompatibleMode()?SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE);
							config.toJSONFile(ConfigActivity.this, "config");
						}
		     		})
		     	)
		    );
		 
		 this.addSettingGroup(new SettingGroup(this,R.string.config_shortcut_title)
	     	.addSettingItem(new SettingItem(this,R.string.config_shortcut_desktop)
	     		.setState(SettingItem.STATE_MORE)
	     		.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(SettingItem item, View v) {
							Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
							shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,getString(R.string.app_name));
							shortcutIntent.putExtra("duplicate", false);
							Intent intent = new Intent();
							intent.setComponent(new ComponentName(getPackageName(), LockActivity.class.getName()));
							shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
							shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(ConfigActivity.this, R.drawable.icon));
							sendBroadcast(shortcutIntent);
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
	protected void onResume() {
		accessItem.setState(policyManager.isAdminActive(componentName)?SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE);
		super.onResume();
	}

}
