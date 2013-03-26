package com.santwick.flowwidget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class FlowWidget extends AppWidgetProvider {

 
    @Override  
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,  
            int[] appWidgetIds) {  

        final int N = appWidgetIds.length;  
        for (int i = 0; i < N; i++) {  
            int appWidgetId = appWidgetIds[i];  
            Log.i("FlowWidget", "this is [" + appWidgetId + "] onUpdate!");  
        }
        
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context,FlowService.class));
    }  
    
    
  
    @Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  

        final int N = appWidgetIds.length;  
        for (int i = 0; i < N; i++) {  
            int appWidgetId = appWidgetIds[i];  
            Log.i("FlowWidget", "this is [" + appWidgetId + "] onDelete!");  
        }

        super.onDeleted(context, appWidgetIds);
    }


}
