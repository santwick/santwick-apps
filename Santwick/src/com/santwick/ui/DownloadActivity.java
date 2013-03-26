package com.santwick.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.santwick.utils.PackageUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;


@SuppressLint("HandlerLeak")
public class DownloadActivity extends Activity {
	
	private final static int DOWN_FAILED = -1;
	private final static int DWON_SUCCESS = 101;

	private ProgressBar progressBar;
	private TextView textView;
	private String downloadUrl;
	private Integer downloadProgress = 0;
	private String dirAppSave;
	private String pathAppSave ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		
		downloadUrl = getIntent().getStringExtra("url");
		
		dirAppSave = Environment.getExternalStorageDirectory().getPath() + "/santwick/" + PackageUtils.getAppId(this);
		File fileAppSaveDir = new File(dirAppSave);
		if(!fileAppSaveDir.exists()){
			fileAppSaveDir.mkdirs();
		}
		pathAppSave = dirAppSave + "/temp.apk";
		
		progressBar = (ProgressBar)findViewById(R.id.progressBarDownload);
		textView = (TextView)findViewById(R.id.textViewDownload);
		startDownload();
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {  
			if(msg.what==DOWN_FAILED){
				//Toast.makeText(UpdateActivity.this, "下载已中断",Toast.LENGTH_SHORT).show();
				finish();
			}else if(msg.what==DWON_SUCCESS){
				Uri uri = Uri.fromFile(new File(pathAppSave)); 
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
				startActivity(intent);
				finish();
			}else{
				progressBar.setProgress(msg.what);
				textView.setText(String.format("%d%%", msg.what));
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			synchronized(downloadProgress){
				downloadProgress = DOWN_FAILED;
				mHandler.sendEmptyMessage(downloadProgress);
			}
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	public void startDownload(){

		new Thread(new Runnable(){
			@Override
			public void run() {
				OutputStream output=null;  
				HttpURLConnection conn = null;
				try {  
					URL url=new URL(downloadUrl);  
					conn=(HttpURLConnection)url.openConnection();  
					int filelength = conn.getContentLength();
					int downlength = 0;
					if(filelength>0){
						File file=new File(pathAppSave);
						InputStream input=conn.getInputStream();  
						if(file.exists()){  
						    file.delete();
						}
						file.createNewFile();//新建文件  
						output=new FileOutputStream(file);  
						//读取大文件  
						byte[] buffer=new byte[4*1024];
						int len = input.read(buffer);

						while(len!=-1 ){
							output.write(buffer,0,len);
							downlength += len;
							len = input.read(buffer);
							
							synchronized(downloadProgress){
								if(downloadProgress ==DOWN_FAILED){
									break;
								}
								downloadProgress = downlength*100/filelength;
								mHandler.sendEmptyMessage(downloadProgress);
							}
						}
						output.flush();  
	                }
	            } catch (MalformedURLException e) {  
	                e.printStackTrace();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }finally{
            		try {
						if(output!=null){
							output.close();
						}
            		 } catch (IOException e) {  
            			 e.printStackTrace();  
            		 }
                	if(conn!=null){
                		conn.disconnect();
                	}
                	
                	synchronized(downloadProgress){
                		if(downloadProgress==100){
                			downloadProgress = DWON_SUCCESS;
                		}else{
                			downloadProgress = DOWN_FAILED;
                		}
                		mHandler.sendEmptyMessage(downloadProgress);
                	}
	            }  
			}
		}).start();
	}

	

}
