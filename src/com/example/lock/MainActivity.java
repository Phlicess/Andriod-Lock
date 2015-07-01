package com.example.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.lock.MainActivity;
import com.example.lock.ScreenObserver;
import com.example.lock.observerScreenStateUpdateListener;
import com.example.lock.Mserver;
import com.example.lock.R;

import android.R.string;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.UpdateAppearance;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;

public class MainActivity extends Activity {
   
	private ScreenObserver mScreenObserver;
	private RelativeLayout rl;
	private TextView timeTextView;
	private TextView dateTextView;
	private TextView circleText;
	private TextView pointunlockText;
	private String time;
	private String date;
	private Point point;
	private int circleR;
	private View shapeLayout;
	private Canvas canvas;
	private DensityUtil du;
	private static Context mContext; 
	private TouchListenerImp tl;
	private Context thisContext;
	private static MainActivity instance = null;
	private TipHelper tipHelper;
		
	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�滻ԭ������
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
       //ȥ����Ϣ�� 
  		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
  		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
  		instance = this;
  		
  		//����home��
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
		
		
		setContentView(R.layout.activity_main);
		mContext = getApplicationContext();  
		
		Paint mCirclePaint = new Paint();
	    mCirclePaint.setAntiAlias(true);
	    mCirclePaint.setColor(Color.WHITE);
	    mCirclePaint.setStyle(Style.FILL);
	    Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888); //����λͼ�Ŀ��,bitmapΪ͸��  
	    
	    canvas = new Canvas(bitmap);
	    canvas.drawCircle(150, 50, 50, mCirclePaint);
		//����ϵͳ����
		//����myservice��intent��filter
		//Intent toMainIntent = new Intent(myService.this, MainActivity.class);
		//toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//���flags��ʾ����Ѿ������activity�������е��ᵽջ���������½�һ��activity��
				
		// ע��ʱ�����Ź㲥
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(br, filter);
			
		
		Date crrentTimeDate = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("MM��dd�� HH:mm");
		
		// ����ʱ�������
		date = dateformat.format(crrentTimeDate).split(" ")[0];
		time = dateformat.format(crrentTimeDate).split(" ")[1];
		if(date.substring(0, 1).equals("0")){
			date = date.substring(1, date.length());
		};
		if(time.substring(0, 1).equals("0")){
			time = time.substring(1, time.length());
		};

		timeTextView = (TextView) findViewById(R.id.time);
		dateTextView = (TextView) findViewById(R.id.date);
		circleText = (TextView) findViewById(R.id.circle);
		pointunlockText = (TextView) findViewById(R.id.point_unlock);
		shapeLayout = findViewById(R.drawable.circular_layout);
		
		rl = (RelativeLayout) findViewById(R.id.main);
	
		du = new DensityUtil();
		// ��Ӵ����¼�����
		tl = new TouchListenerImp();
		this.rl.setOnTouchListener(tl);
		
		// ΪTextView����ʱ�������
		dateTextView.setText(date);
		timeTextView.setText(time);
		
		
		
		//���� ������̨����
		 thisContext  = this;
		
		// Intent intent = new Intent(this,Mserver.class);
	    // this.startService(intent);
		 Intent lockintent = new Intent();
         lockintent.setClass(this, Mserver.class);
         lockintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         startService(lockintent);
		
	}
	
	//�ó��� ��פ
	protected void onDestroy() {
	    super.onDestroy();  
        if (mScreenObserver!=null) {  
            mScreenObserver.stopScreenStateUpdate();  
        }  
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
	    if(keyCode==KeyEvent.KEYCODE_MENU){
	       return true;
	    }
	    return super.onKeyLongPress(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
	// ����ʱ�����Ź㲥 ������ʱ���������ʾ
	BroadcastReceiver br = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Date crrentTimeDate = new Date();
			SimpleDateFormat dateformat = new SimpleDateFormat("MM��dd�� HH:mm");		
			// ����ʱ�������
			date = dateformat.format(crrentTimeDate).split(" ")[0];
			time = dateformat.format(crrentTimeDate).split(" ")[1];			
			if(date.substring(0, 1).equals("0")){
				date = date.substring(1, date.length());
			};
			if(time.substring(0, 1).equals("0")){
				time = time.substring(1, time.length());
			};
			
			// ΪTextView����ʱ�������
			dateTextView.setText(date);
			timeTextView.setText(time);
		}
		
	};
	
	
	// ����ʱ�����Ź㲥 ������ʱ���������ʾ
	BroadcastReceiver disa = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(context.KEYGUARD_SERVICE);
			KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
			keyguardLock.disableKeyguard();//����ϵͳ����
			//startActivity();//��ת��������
		}
		
	};
	
	
	// ����״̬������
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
         // TODO Auto-generated method stub
         super.onWindowFocusChanged(hasFocus);
         sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
	 }
	
		
	// ���γ����˵���
	@Override
	public void onAttachedToWindow() {
        this.getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
	
	// ���η��ؼ� 
	//
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
        	return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU){
        	return true;
        }  else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
        	return true;
        } else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
        	return true;
        }
        return super.onKeyDown(keyCode, event);
	}
	
	// ��ȡ����λ��
	private class TouchListenerImp implements OnTouchListener {
		int x, y;
		int padding;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // TODO ����ʱ����
				circleText.setX(event.getX() - 50);
				circleText.setY(event.getY() - 50);
				//point.x = (int) event.getX();
				//point.y = (int) event.getY();
				padding = du.dip2px(mContext, 20);
				circleText.setPadding(padding, padding, padding, padding);
				
				padding = du.dip2px(mContext, 50);
				pointunlockText.setPadding(padding, padding, padding, padding);
			    
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // TODO ����ʱ�ƶ�
            	circleText.setX((int) event.getX() - 50);
            	circleText.setY((int) event.getY() - 50);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // TODO ����ʱ̧��
            	circleText.setPadding(0, 0, 0, 0);
            	pointunlockText.setPadding(0, 0, 0, 0);
            	
            	x = (int) event.getX();
            	y = (int) event.getY();
            	if((pointunlockText.getLeft() <= x - 80) && (pointunlockText.getLeft() + 150 >= x - 80) && (pointunlockText.getTop() <= y-40) && (pointunlockText.getTop() >= y-200)){
                	//Toast.makeText(MainActivity.this, String.valueOf(pointunlockText.getLeft()), Toast.LENGTH_SHORT).show();
            		tipHelper = new TipHelper();
            		tipHelper.Vibrate(instance, 50);
                	instance.finish();
            	}
            	
            }
			return true;
		}
		
		
		
		
		//���� ��Դ��
		private void initScreenObserver() {
			// TODO Auto-generated method stub
			mScreenObserver = new ScreenObserver(MainActivity.this);
			mScreenObserver.obserScreenStateUpdate(new observerScreenStateUpdateListener() {
				
				@Override
				public void onScreenOn(Context context) {
					// TODO Auto-generated method stub
				
					//showLockView(context);				
					
				}

				@Override
				public void onScrennOff(Context context) {
					// TODO Auto-generated method stub
					
					// showLockView(context);
			
				}
				
				private void showLockView(Context context) {
				// TODO Auto-generated method stub
				 Intent intent = new Intent(context,MainActivity.class);
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.startActivity(intent);
			    }
			});
	}
	}
	
}


	

