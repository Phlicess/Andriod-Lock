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
			   System.out.println("API<7����ʹ��ʹ��");  
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
	
	
	//��Ϊ���
	//������Ļ״̬
	public void obserScreenStateUpdate(observerScreenStateUpdateListener listener){
		mObserverScreenStateUpdateListener = listener;
		
		registerScreenBroadcastReceiver();
	}

    //ע�����
	private void registerScreenBroadcastReceiver() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mContext.registerReceiver(mScrennBroadcastReceiver, filter);
	}
	
	//������Ӧ�ó���ʱ�ж���Ļ״̬
	private void firstGetScreenState(){
		PowerManager manager = (PowerManager) mContext.getSystemService(Activity.POWER_SERVICE);
        if(isScreenOnForFirst(manager)){
        	if(mObserverScreenStateUpdateListener != null){
        		mObserverScreenStateUpdateListener.onScreenOn(lContext);
        		System.out.print("�����г���ʱ����ĻΪ��״̬");
        	}
        }else {
			if(mObserverScreenStateUpdateListener != null){
				mObserverScreenStateUpdateListener.onScrennOff(lContext);
				System.out.print("�����г���ʱ����ĻΪ�ر�״̬");
			}
		}	
	}
	
	//������Ӧ�ó����ж���Ļ�Ƿ�ر�
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
