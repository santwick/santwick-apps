package com.santwick.flowwidget.ui;

import android.os.Bundle;
import android.view.View;

import com.santwick.flowwidget.DataCenter;
import com.santwick.flowwidget.R;
import com.santwick.flowwidget.object.ConfigObject;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;


public class ConfigAlermActivity extends SettingActivity {

	private ConfigObject config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setBackTips(R.string.header_back_back);
		
		config = DataCenter.getConfigObject(this);
		
		this.addSettingGroup(
				new SettingGroup(this,R.string.config_alerm_title).addSettingItem(
						new SettingItem(this,R.string.config_alerm_25)
						.setState(config.isAlermOn(ConfigObject.ALERM_25) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								config.setAlerm(ConfigObject.ALERM_25, !config.isAlermOn(ConfigObject.ALERM_25) );
								item.setState(config.isAlermOn(ConfigObject.ALERM_25) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
							}
						})
				).addSettingItem(
						new SettingItem(this,R.string.config_alerm_50)
						.setState(config.isAlermOn(ConfigObject.ALERM_50) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								config.setAlerm(ConfigObject.ALERM_50, !config.isAlermOn(ConfigObject.ALERM_50) );
								item.setState(config.isAlermOn(ConfigObject.ALERM_50) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
							}
						})
				).addSettingItem(
						new SettingItem(this,R.string.config_alerm_75)
						.setState(config.isAlermOn(ConfigObject.ALERM_75) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								config.setAlerm(ConfigObject.ALERM_75, !config.isAlermOn(ConfigObject.ALERM_75) );
								item.setState(config.isAlermOn(ConfigObject.ALERM_75) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
							}
						})
				).addSettingItem(
						new SettingItem(this,R.string.config_alerm_100)
						.setState(config.isAlermOn(ConfigObject.ALERM_100) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								config.setAlerm(ConfigObject.ALERM_100, !config.isAlermOn(ConfigObject.ALERM_100) );
								item.setState(config.isAlermOn(ConfigObject.ALERM_100) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
							}
						})
				).addSettingItem(
						new SettingItem(this,R.string.config_alerm_5M)
						.setState(config.isAlermOn(ConfigObject.ALERM_5M) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								config.setAlerm(ConfigObject.ALERM_5M, !config.isAlermOn(ConfigObject.ALERM_5M) );
								item.setState(config.isAlermOn(ConfigObject.ALERM_5M) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
							}
						})
				).addSettingItem(
						new SettingItem(this,R.string.config_alerm_1M)
						.setState(config.isAlermOn(ConfigObject.ALERM_1M) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								config.setAlerm(ConfigObject.ALERM_1M, !config.isAlermOn(ConfigObject.ALERM_1M) );
								item.setState(config.isAlermOn(ConfigObject.ALERM_1M) ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
							}
						})
				)
		);
		
	}
	
	
}
