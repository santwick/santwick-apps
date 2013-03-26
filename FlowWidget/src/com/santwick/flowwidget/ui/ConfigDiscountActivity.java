package com.santwick.flowwidget.ui;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;


import com.santwick.flowwidget.DataCenter;
import com.santwick.flowwidget.R;
import com.santwick.flowwidget.object.ConfigObject;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.ui.SettingItem.OnItemClickListener;

public class ConfigDiscountActivity extends SettingActivity {
	private ConfigObject config;
	private String[] discount_type_hints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setBackTips(R.string.header_back_back);
		
		config = DataCenter.getConfigObject(this);
		discount_type_hints = this.getResources().getStringArray(R.array.config_discount_type_hint);
		
		this.addSettingGroup(new SettingGroup(this,R.string.config_discount_title)
    	.addSettingItem(
			new SettingItem(this,R.string.config_discount_type)
			.setState(SettingItem.STATE_MORE)
			.setHint(discount_type_hints[config.getDiscountType()])
			.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onClick(final SettingItem item, View v) {
					new AlertDialog.Builder(ConfigDiscountActivity.this)
					.setSingleChoiceItems(R.array.config_discount_select_string, config.getDiscountType(), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(config.getDiscountType()!=which){
								config.setDiscountType(which);
								item.setHint(discount_type_hints[config.getDiscountType()]);	
							}
							dialog.dismiss();
						}
					}).setNegativeButton(R.string.button_cancel, null).show();
				}
			}))
    	.addSettingItem(new SettingItem(this,R.string.config_discount_start)
			.setState(SettingItem.STATE_MORE)
			.setHint(String.format("%1$02d:%2$02d", config.getDiscountStartHour(), config.getDiscountStartMin()))
			.setOnItemClickListener(new OnItemClickListener(){
	
				@Override
				public void onClick(final SettingItem item, View v) {
					new TimePickerDialog(v.getContext(),new OnTimeSetListener(){
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							if(config.getDiscountStartHour()!=hourOfDay || config.getDiscountStartMin()!=minute) {
								config.setDiscountStartHour(hourOfDay);
								config.setDiscountStartMin(minute);
								item.setHint(String.format("%1$02d:%2$02d", config.getDiscountStartHour(), config.getDiscountStartMin()));
							}
						}
					},config.getDiscountStartHour(),config.getDiscountStartMin(), true).show();
				}
			}))
		.addSettingItem(new SettingItem(this,R.string.config_discount_stop)
			.setState(SettingItem.STATE_MORE)
			.setHint(String.format("%1$02d:%2$02d", config.getDiscountStopHour(), config.getDiscountStopMin()))
			.setOnItemClickListener(new OnItemClickListener(){
	
				@Override
				public void onClick(final SettingItem item, View v) {
					new TimePickerDialog(v.getContext(),new OnTimeSetListener(){
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							if(config.getDiscountStopHour()!=hourOfDay || config.getDiscountStopMin()!=minute) {
								config.setDiscountStopHour(hourOfDay);
								config.setDiscountStopMin(minute);
								item.setHint(String.format("%1$02d:%2$02d", config.getDiscountStopHour(), config.getDiscountStopMin()));
							}
						}
					},config.getDiscountStopHour(),config.getDiscountStopMin(), true).show();
				}
			}))
   		);		
	}
	

}
