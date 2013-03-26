package com.santwick.ui;


import java.util.ArrayList;

import com.santwick.ui.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingGroup extends LinearLayout{
	
	private LinearLayout contentView;
	private ArrayList<SettingItem> itemList = new ArrayList<SettingItem>();

	public SettingGroup(Context context){
		this(context,null);
	}

	public SettingGroup(Context context,String title) {
		super(context);
		
		LayoutParams lp = 	new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 10, 10, 10);
		this.setLayoutParams(lp);
		this.setOrientation(LinearLayout.VERTICAL);
		
		if(title != null && !title.equals("")){
			TextView v = new TextView(this.getContext());
			LayoutParams l1p = 	new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			l1p.setMargins(0, 0, 0, 3);
			v.setLayoutParams(l1p);
			v.setText(title);
			this.addView(v);
		}
		
		contentView = new LinearLayout(context);
		this.addView(contentView);
		
		contentView.setOrientation(LinearLayout.VERTICAL);
		contentView.setBackgroundResource(R.drawable.setting_group_backgroud);
		
	}
	
	public SettingGroup(Context context,int resTitleId) {
		this(context,context.getResources().getString(resTitleId));
	}
	
	public void delSettingItem(SettingItem item){
		if(itemList.remove(item)){
			refreshViews();
		}
	}

	public SettingGroup addSettingItem(SettingItem item){
		itemList.add(item);
		refreshViews();
		return this;
	}
	private void addDivideLine(){
		View v = new View(this.getContext());
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,1);
		v.setLayoutParams(lp);
		
		v.setBackgroundResource(R.color.setting_stroke);
		contentView.addView(v);
	}
	private void refreshViews(){
		contentView.removeAllViews();
		if(itemList.size()==1){
			contentView.addView(itemList.get(0).getView());
			itemList.get(0).getView().setBackgroundResource(R.drawable.setting_item_single);
		}else if(itemList.size() > 1){
			contentView.addView(itemList.get(0).getView());
			itemList.get(0).getView().setBackgroundResource(R.drawable.setting_item_first);
			for(int i=1;i<itemList.size()-1;i++){
				addDivideLine();
				contentView.addView(itemList.get(i).getView());
				itemList.get(i).getView().setBackgroundResource(R.drawable.setting_item_middle);
			}
			addDivideLine();
			contentView.addView(itemList.get(itemList.size()-1).getView());
			itemList.get(itemList.size()-1).getView().setBackgroundResource(R.drawable.setting_item_last);
		}
	}


}
