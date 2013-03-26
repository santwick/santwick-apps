package com.santwick.utils;

import java.util.Calendar;

public class TimeUtils {
	public static boolean timeBetween(int timeStart,int timeEnd,int timeCheck){
		boolean bDiscountTime = false;
		if(timeStart>timeEnd){
			if(timeStart > timeCheck && timeCheck>=timeEnd){
			}else{
				bDiscountTime = true;
			}
		}else if(timeStart<timeEnd){
			if(timeStart <= timeCheck && timeCheck<timeEnd){
				bDiscountTime = true;
			}
		}
		return bDiscountTime;
	}
	//当前时间是否在二者之间
	public static boolean timeBetween(int timeStart,int timeEnd){
		int timeNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*60 + Calendar.getInstance().get(Calendar.MINUTE);
		return timeBetween(timeStart, timeEnd, timeNow);
	}
}
