package com.santwick.flowwidget.ui;


import java.util.Calendar;

import com.santwick.flowwidget.DataCenter;
import com.santwick.flowwidget.FlowService;
import com.santwick.flowwidget.R;
import com.santwick.flowwidget.object.ConfigObject;
import com.santwick.flowwidget.object.FlowObject;
import com.santwick.ui.AboutActivity;
import com.santwick.ui.SettingActivity;
import com.santwick.ui.SettingGroup;
import com.santwick.ui.SettingItem;
import com.santwick.ui.SettingItem.OnItemClickListener;
import com.santwick.utils.PackageUtils;
import com.santwick.utils.UpdateUtils;

import android.os.Bundle;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;


public class ConfigActivity extends SettingActivity  {

	private int mAppWidgetId;  
	private ConfigObject config;
	
	private EditText textEdit;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("ConfigActivity"," onCreate");
        
        UpdateUtils.checkUpdateSchedule(this);
		
		setResult(RESULT_CANCELED);
		
		
		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						mAppWidgetId);
				
				setResult(RESULT_OK, resultValue);
			}
		}
		
		this.setBackTips(R.string.header_back_exit);
        this.addExtraButton(R.string.header_share, new OnClickListener(){

			@Override
			public void onClick(View v) {
				PackageUtils.sharePackage(ConfigActivity.this);
			}
        	
        });
		
		config = DataCenter.getConfigObject(this);

		
		this.addSettingGroup(
			new SettingGroup(this,R.string.config_normal_title).addSettingItem(
					new SettingItem(this,R.string.config_normal_monthlimit)
					.setState(SettingItem.STATE_MORE)
					.setHint(config.getMonthLimit()>0 ? Integer.toString(config.getMonthLimit()) + "MB" : R.string.config_normal_monthlimit_hint)
					.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onClick(final SettingItem item, View v) {
							textEdit  = new EditText(ConfigActivity.this);
							textEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
							textEdit.setKeyListener(new NumberKeyListener(){
								protected char[] getAcceptedChars()
								{
									char numberChars[]={'1','2','3','4','5','6','7','8','9','0',};
									return numberChars;
								}
								@Override
								public int getInputType() {
									return InputType.TYPE_CLASS_PHONE;
								}
							});
							textEdit.setText(Integer.toString(config.getMonthLimit()));
							new AlertDialog.Builder(ConfigActivity.this).
							setTitle(getResources().getString(R.string.input_monthlimit_title)).
							setIcon(android.R.drawable.ic_dialog_info).setView(textEdit).
							setPositiveButton(getResources().getString(R.string.button_ok), 
									new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface arg0,int arg1) {
											config.setMonthLimit(Integer.parseInt(textEdit.getText().toString()));
											item.setHint(config.getMonthLimit()>0 ? Integer.toString(config.getMonthLimit()) + "MB" : R.string.config_normal_monthlimit_hint);
										}
							}).
							setNegativeButton(getResources().getString(R.string.button_cancel), null).show();
						}
					})
			).addSettingItem(
					new SettingItem(this,R.string.config_normal_alerm)
					.setState(SettingItem.STATE_MORE)
					.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onClick(SettingItem item, View v) {
							Intent intent = new Intent(ConfigActivity.this,ConfigAlermActivity.class);
							ConfigActivity.this.startActivity(intent);
						}
					})
			).addSettingItem(
					new SettingItem(this,R.string.config_normal_block)
					.setSummary(getResources().getString(R.string.config_normal_block_summary))
					.setState(config.isEnableDisconnect() ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE)
					.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onClick(SettingItem item, View v) {
							config.setEnableDisconnect(!config.isEnableDisconnect());
							item.setState(config.isEnableDisconnect() ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
						}
					})
			)
		);
		
		this.addSettingGroup(
				new SettingGroup(this,R.string.config_adv_title).addSettingItem(
						new SettingItem(this,R.string.config_adv_flowcorrect)
						.setState(SettingItem.STATE_MORE)
						.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onClick(final SettingItem item, View v) {
							
							Calendar calendar = Calendar.getInstance(); 
							final String mon = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH);
							final FlowObject flow = new FlowObject();
							flow.fromJSONFile(ConfigActivity.this, mon );
							
							textEdit  = new EditText(ConfigActivity.this);
							textEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
							textEdit.setKeyListener(new NumberKeyListener(){
								protected char[] getAcceptedChars()
								{
									char numberChars[]={'1','2','3','4','5','6','7','8','9','0','.'};
									return numberChars;
								}
								@Override
								public int getInputType() {
									return InputType.TYPE_CLASS_PHONE;
								}
							});
							textEdit.setText(String.format("%.2f", flow.getGprs()/1024.0/1024.0));
							new AlertDialog.Builder(ConfigActivity.this).
							setTitle(getResources().getString(R.string.input_flowcorrect_title)).
							setIcon(android.R.drawable.ic_dialog_info).setView(textEdit).
							setPositiveButton(getResources().getString(R.string.button_ok), 
									new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface arg0,int arg1) {
											double fflow = 0;
											try{
												fflow = Double.parseDouble(textEdit.getText().toString());
											}catch(NumberFormatException e){
												e.printStackTrace();
												Toast.makeText(ConfigActivity.this, R.string.tips_flowcorrect_failed, Toast.LENGTH_SHORT).show();
												return;
											};
											flow.correctGprs(fflow);
											stopService(new Intent(ConfigActivity.this,FlowService.class));
											flow.toJSONFile(ConfigActivity.this, mon);
											startService(new Intent(ConfigActivity.this,FlowService.class));
											Toast.makeText(ConfigActivity.this, R.string.tips_flowcorrect_succes, Toast.LENGTH_SHORT).show();
										}
							}).
							setNegativeButton(getResources().getString(R.string.button_cancel), null).show();
						}
					}))
				.addSettingItem(new SettingItem(this,R.string.config_adv_discount)
						.setState(SettingItem.STATE_MORE)
						.setOnItemClickListener(new OnItemClickListener(){
							@Override
							public void onClick(SettingItem item, View v) {
								Intent intent = new Intent(ConfigActivity.this,ConfigDiscountActivity.class);
								ConfigActivity.this.startActivity(intent);
							}
						}))
			);
		
		this.addSettingGroup(
				new SettingGroup(this,R.string.config_ui_title).addSettingItem(
						new SettingItem(this,R.string.config_ui_progress)
						.setState(config.isWidgetProgress() ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE )
						.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onClick(SettingItem item, View v) {
							config.setWidgetProgress(!config.isWidgetProgress());
							item.setState(config.isWidgetProgress() ? SettingItem.STATE_CHECK_TRUE : SettingItem.STATE_CHECK_FALSE);
						}
					})
				)
			);
		
			this.addSettingGroup(
				new SettingGroup(this,R.string.config_other_title).addSettingItem(
						new SettingItem(this,R.string.config_other_about)
						.setState(SettingItem.STATE_MORE)
						.setOnItemClickListener(new OnItemClickListener() {
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
		
		Log.i("ConfigActivity"," onDestroy");
		
		config.toJSONFile(this, "config");
		stopService(new Intent(this,FlowService.class));
		startService(new Intent(this,FlowService.class));
		super.onDestroy();
	}


}
