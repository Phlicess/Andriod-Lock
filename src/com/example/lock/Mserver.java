package com.example.lock;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class Mserver extends Service{

	private Intent startIntent =null;
	public static KeyguardLock kk;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		registerReceiver(br, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		
		startIntent=intent; 
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF); 
        filter.addAction("android.intent.action.SCREEN_ON");      
        registerReceiver(receiver, filter);
	}
	
	
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);  
		
      
	   if(startIntent!=null){  
          
            startService(startIntent);  
        } 
	}
	
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();  
            System.out.println("action is "+action);  
            if(kk == null){
                KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
                kk = km.newKeyguardLock("");
            }
            if(action.equals(Intent.ACTION_SCREEN_OFF)){
                kk.disableKeyguard();
                Intent unlockIntent=new Intent(context,MainActivity.class);
                unlockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(unlockIntent);  
            }

            
        }};
	
	
	
	   private BroadcastReceiver br = new BroadcastReceiver() {
	        
	        @SuppressWarnings("deprecation")
			@Override
	        public void onReceive(Context context, Intent intent) {
	            // TODO Auto-generated method stub
	            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
	             
	            	//ÆÁ±Î ¼üÅÌ
	            	KeyguardManager mKeyGuardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

	                KeyguardLock mLock = mKeyGuardManager.newKeyguardLock("×Ô¼ºActivityÃû×Ö");

	                mLock.disableKeyguard();   
	                
	                showLockView(context);
	                
	                
	            }
	            
	        }

			private void showLockView(Context context) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,MainActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        context.startActivity(intent);
			}
	    };

}
