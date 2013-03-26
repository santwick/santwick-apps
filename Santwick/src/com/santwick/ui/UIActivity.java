package com.santwick.ui;

import com.santwick.ui.R;
import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UIActivity extends Activity {
	private LinearLayout contentView;
	private LinearLayout headerExtraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		super.setContentView(R.layout.ui_header);
		
		contentView = (LinearLayout) findViewById(R.id.viewContent);
		headerExtraView = (LinearLayout) findViewById(R.id.layoutExtra);
		
		findViewById(R.id.buttonBack).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView)findViewById(R.id.textViewTitle)).setText(this.getTitle());

	}

	@Override
	public void setContentView(int layoutResID) {
		this.setContentView(this.getLayoutInflater().inflate(layoutResID, null));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		contentView.addView(view, params);
	}

	@Override
	public void setContentView(View view) {
		
		this.setContentView(view,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public void setBackTips(String tips){
		if(tips!=null && !tips.equals("")){
			((Button)findViewById(R.id.buttonBack)).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.buttonBack)).setText(tips);
		}else{
			((Button)findViewById(R.id.buttonBack)).setVisibility(View.GONE);
			((Button)findViewById(R.id.buttonBack)).setText("");
		}
		
	}
	
	public void setBackTips(int resId){
		setBackTips(getResources().getString(resId));
	}
	
	public View addExtraButton(Object objRes,OnClickListener listener){
		
		if(objRes instanceof Drawable){
			ImageView imageView = new ImageView(this);
			imageView.setImageDrawable((Drawable) objRes);
			imageView.setBackgroundResource(R.drawable.header_extra);
			imageView.setOnClickListener(listener);
			headerExtraView.addView(imageView);
			return imageView;

		}else if(objRes instanceof String){
			TextView textView = new TextView(this);
			textView.setText((String) objRes);
			textView.setTextColor(getResources().getColor(R.color.ui_header_textcolor));
			textView.setBackgroundResource(R.drawable.header_extra);
			textView.setGravity(Gravity.CENTER);
			textView.setOnClickListener(listener);
			headerExtraView.addView(textView);
			return textView;
		}else if(objRes instanceof Integer){
			
			try{
				String string= getResources().getString((Integer) objRes);
				TextView textView = new TextView(this);
				textView.setText((String) string);
				textView.setTextColor(getResources().getColor(R.color.ui_header_textcolor));
				textView.setBackgroundResource(R.drawable.header_extra);
				textView.setGravity(Gravity.CENTER);
				textView.setOnClickListener(listener);
				headerExtraView.addView(textView);
				return textView;
			}catch(NotFoundException e){
				e.printStackTrace();
			}
			
			try{
				Drawable drawable= getResources().getDrawable((Integer) objRes);
				ImageView imageView = new ImageView(this);
				imageView.setImageDrawable( drawable);
				imageView.setBackgroundResource(R.drawable.header_extra);
				imageView.setOnClickListener(listener);
				headerExtraView.addView(imageView);
				return imageView;
			}catch(NotFoundException e){
				e.printStackTrace();
			}
		}
		return null;
	}

}
