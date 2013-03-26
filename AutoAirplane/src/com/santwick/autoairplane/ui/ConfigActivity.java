package com.santwick.autoairplane.ui;


import com.santwick.autoairplane.R;
import com.santwick.autoairplane.TimerService;
import com.santwick.autoairplane.object.ConfigObject;
import com.santwick.ui.AboutActivity;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.ui.SettingItem.OnItemClickListener;
import com.santwick.utils.PackageUtils;
import com.santwick.utils.UpdateUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;


public class ConfigActivity extends SettingActivity  {

	private ConfigObject config = new ConfigObject();
	
	private boolean isChanged = false;
	private boolean isAirplaneModeOn;
	private String[] hint_automode;
	private SettingItem itemEnter;
	private SettingItem itemLeave;
	private SettingGroup groupNormal;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("MainActivity"," onCreate");
        
        UpdateUtils.checkUpdateSchedule(this);
        
        this.setBackTips(R.string.header_back_exit);
        this.addExtraButton(R.string.header_share, new OnClickListener(){

			@Override
			public void onClick(View v) {
				PackageUtils.sharePackage(ConfigActivity.this);
			}
        	
        });
        
        config.fromJSONFile(this, "config");
        hint_automode = this.getResources().getStringArray(R.array.config_automode_hint);
        
        isAirplaneModeOn = TimerService.getAirplaneMode(this);
        
        this.addSettingGroup(new SettingGroup(this,R.string.config_manual_title)
        	.addSettingItem(new SettingItem(this,R.string.config_manual_mode)
        		.setState(isAirplaneModeOn?SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE)
        		.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onClick(SettingItem item, View v) {
						isAirplaneModeOn = !isAirplaneModeOn;
						TimerService.setAirplaneMode(ConfigActivity.this, isAirplaneModeOn);
						item.setState(isAirplaneModeOn?SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE);
					}
        			
        		})));
        
        this.addSettingGroup(groupNormal = new SettingGroup(this,R.string.config_mode_title)
        	.addSettingItem(
        			new SettingItem(this,R.string.config_automode_mode)
        			.setState(SettingItem.STATE_MORE)
        			.setHint(hint_automode[config.getAutoMode()])
        			.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(final SettingItem item, View v) {
							new AlertDialog.Builder(ConfigActivity.this)
							.setSingleChoiceItems(R.array.config_automode_string, config.getAutoMode(), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(config.getAutoMode()!=which){
										config.setAutoMode(which);
										groupNormal.delSettingItem(itemEnter);
										groupNormal.delSettingItem(itemLeave);
										if((config.getAutoMode() & ConfigObject.AUTOMODE_ENTER) != 0){
											groupNormal.addSettingItem(itemEnter);
										}
										if((config.getAutoMode() & ConfigObject.AUTOMODE_LEAVE) != 0){
											groupNormal.addSettingItem(itemLeave);
										}
										isChanged = true;
										item.setHint(hint_automode[config.getAutoMode()]);	
									}
									dialog.dismiss();
								}
							}).setNegativeButton(R.string.button_cancel, null).show();
						}
        				
        			})
        		));
        
        itemEnter = new SettingItem(this,R.string.config_automode_start)
					.setState(SettingItem.STATE_MORE)
					.setHint(String.format("%1$02d:%2$02d", config.getStartHour(), config.getStartMin()))
					.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(final SettingItem item, View v) {
							new TimePickerDialog(v.getContext(),new OnTimeSetListener(){
								@Override
								public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
									if(config.getStartHour()!=hourOfDay || config.getStartMin()!=minute) {
										config.setStartHour(hourOfDay);
										config.setStartMin(minute);
										isChanged = true;
										item.setHint(String.format("%1$02d:%2$02d", config.getStartHour(), config.getStartMin()));
									}
								}
							},config.getStartHour(),config.getStartMin(), true).show();
						}
					});
		itemLeave = new SettingItem(this,R.string.config_automode_stop)
					.setState(SettingItem.STATE_MORE)
					.setHint(String.format("%1$02d:%2$02d", config.getStopHour(), config.getStopMin()))
					.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onClick(final SettingItem item, View v) {
							new TimePickerDialog(v.getContext(),new OnTimeSetListener(){
								@Override
								public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
									if(config.getStopHour()!=hourOfDay || config.getStopMin()!=minute) {
										config.setStopHour(hourOfDay);
										config.setStopMin(minute);
										isChanged = true;
										item.setHint(String.format("%1$02d:%2$02d", config.getStopHour(), config.getStopMin()));
									}
								}
							},config.getStopHour(),config.getStopMin(), true).show();
						}
					});
		
		if((config.getAutoMode() & ConfigObject.AUTOMODE_ENTER) != 0){
			groupNormal.addSettingItem(itemEnter);
		}
		if((config.getAutoMode() & ConfigObject.AUTOMODE_LEAVE) != 0){
			groupNormal.addSettingItem(itemLeave);
		}
		
        
        this.addSettingGroup(new SettingGroup(this,R.string.config_other_title)
	    	.addSettingItem(new SettingItem(this,R.string.config_other_wifi)
	    		.setSummary(this.getResources().getString(R.string.config_other_wifi_summary))
	    		.setState(config.isKeepWifi() ? SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE)
	    		.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onClick(SettingItem item, View v) {
						config.setKeepWifi(!config.isKeepWifi());
						config.toJSONFile(ConfigActivity.this, "config");
						item.setState(config.isKeepWifi() ? SettingItem.STATE_CHECK_TRUE:SettingItem.STATE_CHECK_FALSE);
					}
	    		}))
	        .addSettingItem(new SettingItem(this,R.string.config_other_about)
	        		.setState(SettingItem.STATE_MORE)
	        		.setOnItemClickListener(new OnItemClickListener(){
						@Override
						public void onClick(SettingItem item, View v) {
							Intent intent = new Intent(ConfigActivity.this,AboutActivity.class);
							ConfigActivity.this.startActivity(intent);
						}
	        		}))
       );
    }
    
		
    
	@Override
	protected void onDestroy() {
		
		Log.i("MainActivity"," onDestroy");
		if(isChanged){
			config.toJSONFile(this, "config");
			stopService(new Intent(this,TimerService.class));
		}
		
		if(config.getAutoMode() != ConfigObject.AUTOMODE_DISABLE){
			startService(new Intent(this,TimerService.class));
		}
		
		super.onDestroy();
	}
}
