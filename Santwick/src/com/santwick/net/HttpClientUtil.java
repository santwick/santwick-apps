package com.santwick.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public class HttpClientUtil {
	public static int METHOD_GET = 1;
	public static int METHOD_POST = 2;
	
	private String url;
	private int method;
	private NetClientCallback callback;
	private Map<String, String> paramsString = new HashMap<String,String>();
	private Map<String, File> paramsFile = new HashMap<String,File>();
	
	public HttpClientUtil(String url,int method,NetClientCallback callback){
		this.url = url;
		this.method = method;
		this.callback = callback;
	}
	
	public HttpClientUtil addParam(String name,String value){
		if(value == null){
			paramsString.put(name, "");
		}else{
			paramsString.put(name, value);
		}
		return this;
	}
	
	public HttpClientUtil addParam(String name,int value){
		paramsString.put(name, Integer.toString(value));
		return this;
	}
	
	public HttpClientUtil addParam(String name,File file){
		paramsFile.put(name, file);
		return this;
	}
	

	public void asyncDownloadTo(final String path,final boolean bForceRefresh){
		new Thread(new Runnable() {
			@Override
			public void run() {
				syncDownloadTo(path,bForceRefresh);
			}
		}).start();
	}
	
	public void syncDownloadTo(String path,boolean bForceRefresh){
		
		int status = NetClientCallback.NET_FAILED;

		OutputStream output=null;
		InputStream input = null;
		
		try {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 6000);
	        HttpConnectionParams.setSoTimeout(localBasicHttpParams, 6000);
	        DefaultHttpClient client = new DefaultHttpClient(localBasicHttpParams);
	        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
	        client.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
	        client.getParams().setParameter("http.protocol.expect-continue", Boolean.FALSE);
	        client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
	        
			HttpUriRequest request = getRequest();
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				long filelength = response.getEntity().getContentLength();
				if(filelength>0){
					input=response.getEntity().getContent();  
					File file=new File(path);
					if(!bForceRefresh && file.exists() && new FileInputStream(file).available()==filelength){
						if(callback!=null){
							callback.execute(NetClientCallback.NET_SUCCESS,"");
						}
						return;
					}
					output=new FileOutputStream(file);  
					
					long downlength = 0;
					
					byte[] buffer=new byte[4*1024];
					int len = input.read(buffer);
	
					while(len!=-1 ){
						output.write(buffer,0,len);
						downlength += len;
						len = input.read(buffer);
					}
					output.flush();
					if(downlength == filelength ){
						status = NetClientCallback.NET_SUCCESS;
					}
				}
			}
		} catch (SocketTimeoutException  e){
			status = NetClientCallback.NET_TIMEOUT;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch(IllegalStateException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
    		try {
				if(output!=null){
					output.close();
				}
			} catch (IOException e) {  
				e.printStackTrace();  
			}
    		try {
				if(input!=null){
					input.close();
				}
			} catch (IOException e) {  
				e.printStackTrace();  
			}
		}
		if(callback!=null){
			callback.execute(status,"");
		}
	}
	
	
	public void asyncConnect(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				syncConnect();
			}
		}).start();
	}
	
	public void syncConnect(){
		String json = null;
		BufferedReader reader = null;
		int status = NetClientCallback.NET_FAILED;
		try {
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 6000);
	        HttpConnectionParams.setSoTimeout(localBasicHttpParams, 6000);
	        DefaultHttpClient client = new DefaultHttpClient(localBasicHttpParams);
	        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
	        client.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
	        client.getParams().setParameter("http.protocol.expect-continue", Boolean.FALSE);
	        client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
	        
			HttpUriRequest request = getRequest();
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					sb.append(s);
				}
				json = sb.toString();
				status = NetClientCallback.NET_SUCCESS;
			}
		} catch (SocketTimeoutException  e){
			status = NetClientCallback.NET_TIMEOUT;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// ignore me
			}
		}
		if(callback!=null){
			callback.execute(status,json);
		}
	}

	private HttpUriRequest getRequest() {
		if (method==METHOD_POST) {
			HttpPost request = new HttpPost(url);
			if(paramsFile.size() > 0 ) {
				MultipartEntity  entity = new MultipartEntity();
				for (String name : paramsString.keySet()) {
					try {
						entity.addPart(name, new StringBody(paramsString.get(name) , Charset.forName("UTF-8")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				for (String name : paramsFile.keySet()) {
					entity.addPart(name, new FileBody(paramsFile.get(name)));
				}
				request.setEntity(entity);
			}else {
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				for (String name : paramsString.keySet()) {
					listParams.add(new BasicNameValuePair(name, paramsString.get(name)));
				}
				try {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(listParams,"UTF-8");
					request.setEntity(entity);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			return request;
		} else {
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			for (String name : paramsString.keySet()) {
				try {
					url += "&" + name + "=" + URLEncoder.encode(paramsString.get(name),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			HttpGet request = new HttpGet(url);
			return request;
		}
	}
}
