package com.santwick.net;


import com.santwick.ui.R;

import android.app.AlertDialog;
import android.content.Context;

public class MsgUtils {
	public static void MsgBox(Context context,String sTitle,String sMsg){
		new AlertDialog.Builder(context).
		setTitle(sTitle).
		setMessage(sMsg).
		setPositiveButton(R.string.button_ok,null).
		show();
	}
}
