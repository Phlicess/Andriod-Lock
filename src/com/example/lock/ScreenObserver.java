package com.example.lock;

import java.lang.reflect.Method;
import java.security.PublicKey;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;

public class ScreenObserver{

	private Context mContext;
	private Context lContext;
	private ScreenBroadcastReceiver mScrennBroadcastReceiver;
	private observerScreenStateUpdateListener mObserverScreenStateUpdateListener;
	private static Method mReflectIsScreenOnMethod;
	
	public ScreenObserver (Context context) {
		mContext = context;
		mScrennBroadcastReceiver = new ScreenBroadcastReceiver();
		
		try{
			mReflectIsScreenOnMethod = PowerManager.class.getMethod("isScreenOn",new Class[] {});
		}catch(NoSuchMethodException nsme){
			   System.out.println("API<7不可使用使用");  
		} 
	}
	
	
	private class ScreenBroadcastReceiver extends BroadcastReceiver{
		private String action = null;
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			lContext = context;
			action = intent.getAction();		
			if(intent.ACTION_SCREEN_ON.equals(action)){
				mObserverScreenStateUpdateListener.onScreenOn(lContext);
			}else if(intent.ACTION_SCREEN_OFF.equals(action)){
				mObserverScreenStateUpdateListener.onScrennOff(lContext);
			}
		}
		
	}
	
	
	//此为入口
	//监视屏幕状态
	public void obserScreenStateUpdate(observerScreenStateUpdateListener listener){
		mObserverScreenStateUpdateListener = listener;
		
		registerScreenBroadcastReceiver();
	}

    //注册监听
	private void registerScreenBroadcastReceiver() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mContext.registerReceiver(mScrennBroadcastReceiver, filter);
	}
	
	//刚运行应用程序时判断屏幕状态
	private void firstGetScreenState(){
		PowerManager manager = (PowerManager) mContext.getSystemService(Activity.POWER_SERVICE);
        if(isScreenOnForFirst(manager)){
        	if(mObserverScreenStateUpdateListener != null){
        		mObserverScreenStateUpdateListener.onScreenOn(lContext);
        		System.out.print("刚运行程序时，屏幕为打开状态");
        	}
        }else {
			if(mObserverScreenStateUpdateListener != null){
				mObserverScreenStateUpdateListener.onScrennOff(lContext);
				System.out.print("刚运行程序时，屏幕为关闭状态");
			}
		}	
	}
	
	//刚运行应用程序判断屏幕是否关闭
	private static boolean isScreenOnForFirst(PowerManager powerManager){
		boolean screenState;
		try {
			screenState = (Boolean) mReflectIsScreenOnMethod.invoke(powerManager);
		} catch (Exception e) {
			// TODO: handle exception
			screenState = false;
		}
		
		return screenState;
	}
	
	public void stopScreenStateUpdate() {
		mContext.unregisterReceiver(mScrennBroadcastReceiver);
	}

	

}
