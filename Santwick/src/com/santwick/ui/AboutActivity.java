package com.santwick.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.utils.PackageUtils;
import com.santwick.utils.UpdateUtils;

public class AboutActivity extends SettingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setBackTips(R.string.header_back_back);
		
		this.addSettingGroup(
				new SettingGroup(this).addSettingItem(
						new SettingItem(this,getResources().getString(R.string.config_about_version))
						.setHint(PackageUtils.getVersionName(AboutActivity.this))
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								UpdateUtils.checkUpdateNow(AboutActivity.this);
							}
						})
				).addSettingItem(
						new SettingItem(this,getResources().getString(R.string.config_about_share))
						.setSummary(getResources().getString(R.string.config_about_share_summary))
						.setState( SettingItem.STATE_MORE )
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								PackageUtils.sharePackage(AboutActivity.this); 
							}
						})
				).addSettingItem(
						new SettingItem(this,getResources().getString(R.string.config_about_recommend))
						.setSummary(getResources().getString(R.string.config_about_recommend_summary))
						.setState( SettingItem.STATE_MORE )
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								Intent intent = new Intent(AboutActivity.this,OurappsActivity.class);
								AboutActivity.this.startActivity(intent);
							}
						})
				).addSettingItem(
						new SettingItem(this,getResources().getString(R.string.config_about_contact))
						.setSummary(getResources().getString(R.string.config_about_contact_summary))
						.setState( SettingItem.STATE_MORE )
						.setOnItemClickListener(new SettingItem.OnItemClickListener() {
							@Override
							public void onClick(SettingItem item, View v) {
								Intent mailIntent=new Intent(android.content.Intent.ACTION_SEND);  
								mailIntent.setType("plain/text");  
								mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"santwick@gmail.com"});  
								mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");  
								mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format(getResources().getString(R.string.email_title),getResources().getString(R.string.app_name)));  
								mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.email_content));  
								startActivity(Intent.createChooser(mailIntent, getResources().getString(R.string.config_about_contact)));  
							}
						})
				).addSettingItem(
						new SettingItem(this,getResources().getString(R.string.config_about_coypright))
						.setSummary(getResources().getString(R.string.config_about_coypright_summary))
				)
		);
		
	}

}
