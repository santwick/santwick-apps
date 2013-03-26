package com.santwick.graphics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class FormatConvert {
	
	//public static Drawable 
	
	public static Drawable drawableFromFile(Context context,String path,int width,int height){
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(metrics);
		width *= metrics.density;
		height *= metrics.density;
		
		BitmapDrawable bd= new BitmapDrawable(path);
		if(bd.getIntrinsicWidth()==width && bd.getIntrinsicHeight() == height){
			return bd;
		}
		
		Bitmap bitmap = Bitmap.createBitmap(width, height,     
				bd.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  : Bitmap.Config.RGB_565);     
        Canvas canvas = new Canvas(bitmap);     
        bd.setBounds(0,0,width,height);     
        bd.draw(canvas);  
		return new BitmapDrawable(bitmap);
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable){ 
        int width = drawable.getIntrinsicWidth();     
        int height = drawable.getIntrinsicHeight(); 
        width = width==-1?72:width;
        height = height==-1?72:height;
        
        Bitmap bitmap = Bitmap.createBitmap(width, height,     
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888     
                        : Bitmap.Config.RGB_565);     
        Canvas canvas = new Canvas(bitmap);     
        drawable.setBounds(0,0,width,height);     
        drawable.draw(canvas);     
        return bitmap;     
    }
	
	public static Drawable bitmapToDrawable(Bitmap bitmap){
		BitmapDrawable bd= new BitmapDrawable(bitmap);
		return bd;
	}
	
	public static String drawableToString(Drawable drawable){
		byte[] bytes = getBitmapBytes(drawableToBitmap(drawable));
		if(bytes != null){
			StringBuffer str = new StringBuffer();
			for(byte b:bytes){
				str.append(Integer.toHexString((b & 0x000000FF) | 0xFFFFFF00).substring(6));
			}
			return str.toString();
		}
		return null;
	}
	

	
	public static Drawable stringToDrawable(String str){
		int len = str.length();

		if(len%2 != 0){
			return null;
		}
		byte[] bytes = new byte[len/2];
		char[] hexChars = str.toCharArray();  
		for(int i=0;i<len/2 ;i++){
			int h = "0123456789abcdef".indexOf(hexChars[i*2]);
			int l = "0123456789abcdef".indexOf(hexChars[i*2+1]);
			if(h==-1 || l==-1){
				return null;
			}
			bytes[i] = (byte) (h << 4 | l); 
		}
		
		return bitmapToDrawable(getBitmapFromBytes(bytes));
		
	}
	
	public static Bitmap getBitmapFromBytes(byte[] temp){
		if(temp != null){
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		}else{
			return null;
		}
	} 
	public static byte[] getBitmapBytes(Bitmap bitmap){

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return out.toByteArray();
	}
}
