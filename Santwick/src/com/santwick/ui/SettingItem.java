package com.santwick.ui;

import com.santwick.ui.R;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingItem{
	
	public abstract static interface OnItemClickListener {
		  public abstract void onClick(SettingItem item,View v);
	};
	
	public static final int STATE_NONE = 0;
	public static final int STATE_MORE = 1;
	public static final int STATE_CHECK_TRUE = 2;
	public static final int STATE_CHECK_FALSE = 3;
	public static final int STATE_RADIO_TRUE = 4;
	public static final int STATE_RADIO_FALSE = 5;
	public static final int STATE_LIST = 6;

	
	private View view;
	
	private Object icon;
	private String title;
	private String summary;
	private Object hint;
	private int state;

	
	public SettingItem(Context context,String title) {
		this(context,title,R.layout.setting_item);
	}
	public SettingItem(Context context,int titleResId) {
		this(context,context.getResources().getString(titleResId));
	}
	public SettingItem(Context context,String title,int resLayoutId) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(resLayoutId, null);
		this.setTitle(title);
	}
	
	public Object getIcon() {
		return icon;
	}
	public SettingItem setIcon(Object icon) {
		this.icon = icon;
		if(view!=null){
			if(icon instanceof Drawable){
				((ImageView)view.findViewById(android.R.id.icon)).setImageDrawable((Drawable) icon);
			}else if(icon instanceof Integer){
				((ImageView)view.findViewById(android.R.id.icon)).setImageResource((Integer) icon);
			}
		}
		return this;
	}
	public String getTitle() {
		return title;
	}
	public SettingItem setTitle(String title) {
		this.title = title;
		if(view!=null){
			((TextView)view.findViewById(android.R.id.title)).setText(title);
		}
		return this;
	}

	public String getSummary() {
		return summary;
	}

	public SettingItem setSummary(String summary) {
		this.summary = summary;
		if(view!=null){
			if(summary!=null && !summary.equals("")){
				((TextView)view.findViewById(android.R.id.summary)).setVisibility(View.VISIBLE);
				((TextView)view.findViewById(android.R.id.summary)).setText(summary);
			}else{
				((TextView)view.findViewById(android.R.id.summary)).setVisibility(View.GONE);
				((TextView)view.findViewById(android.R.id.summary)).setText("");
			}
			
		}
		return this;
	}
	
	public Object getHint() {
		return hint;
	}
	public SettingItem setHint(Object hint) {
		this.hint = hint;
		
		if(view!=null){
			
			if(hint instanceof Drawable){
				((TextView)view.findViewById(android.R.id.hint)).setText("");
				((TextView)view.findViewById(android.R.id.hint)).setBackgroundDrawable((Drawable) hint);
			}else if(hint instanceof String){
				((TextView)view.findViewById(android.R.id.hint)).setText((String) hint);
				((TextView)view.findViewById(android.R.id.hint)).setBackgroundDrawable(null);
			}else if(hint instanceof Integer){
				
				try{
					String string= view.getContext().getResources().getString((Integer) hint);
					((TextView)view.findViewById(android.R.id.hint)).setText(string);
					((TextView)view.findViewById(android.R.id.hint)).setBackgroundDrawable(null);
					return this;
				}catch(NotFoundException e){
					e.printStackTrace();
				}
				
				try{
					Drawable drawable= view.getContext().getResources().getDrawable((Integer) hint);
					((TextView)view.findViewById(android.R.id.hint)).setText("");
					((TextView)view.findViewById(android.R.id.hint)).setBackgroundDrawable(drawable);
				}catch(NotFoundException e){
					e.printStackTrace();
				}
			}
		}
		return this;
	}
	public int getState() {
		return state;
	}
	public SettingItem setState(int state) {
		this.state = state;
		if(view != null){
			ImageView imageView = (ImageView)view.findViewById(android.R.id.toggle);
			switch(state){
			case STATE_NONE:
				imageView.setImageDrawable(null);
				break;
			case STATE_MORE:
				imageView.setImageResource(R.drawable.setting_item_state_more);
				break;
			case STATE_CHECK_TRUE:
				imageView.setImageResource(R.drawable.setting_item_state_check_true);
				break;
			case STATE_CHECK_FALSE:
				imageView.setImageResource(R.drawable.setting_item_state_check_false);
				break;
			case STATE_RADIO_TRUE:
				imageView.setImageDrawable(null);
				break;
			case STATE_RADIO_FALSE:
				imageView.setImageDrawable(null);
				break;
			case STATE_LIST:
				imageView.setImageDrawable(null);
				break;
			}
		}

		return this;
	}

	public View getView() {
		return view;
	}



	public SettingItem setOnItemClickListener(final OnItemClickListener onItemClickListener) {
		if(onItemClickListener==null){
			view.setOnClickListener(null);
			return this;
		}
		view.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null){
					onItemClickListener.onClick(SettingItem.this, view);
				}
			}
		});
		return this;
	}


}
