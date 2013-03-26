package com.santwick.ui;

import com.santwick.ui.R;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SettingActivity extends UIActivity {
	
	private LinearLayout rootView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ScrollView scrollView = new ScrollView(this);
		setContentView(scrollView);
		scrollView.setBackgroundResource(R.color.setting_backgroud);
		
		rootView = new LinearLayout(this);
		rootView.setOrientation(LinearLayout.VERTICAL);
		
		scrollView.addView(rootView);
		
	}
	
	public void addView(int layoutResID) {
		rootView.addView(getLayoutInflater().inflate(layoutResID, null));
	}
	
	public void addView(View v){
		rootView.addView(v);
	}
	
	public void addSettingGroup(SettingGroup group){
		rootView.addView(group);
	}

}
