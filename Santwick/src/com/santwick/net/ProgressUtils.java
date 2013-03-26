package com.santwick.net;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

public class ProgressUtils {
	public static void SetTimeoutMessage(final ProgressDialog pd,final Handler handler,final int timeout,final int timeoutMessage){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.currentThread();
					Thread.sleep(timeout  *   1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(pd.isShowing()){
					Message message = new Message();  
			        message.what = timeoutMessage;  
			        handler.sendMessage(message);
				}
			}
        }).start();
	}
}
