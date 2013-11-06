package com.music.mp3player;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class OnlineActivity extends Activity {

	boolean isCanBackToDesktop = false; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (!isCanBackToDesktop) {
			Toast.makeText(this, "再按一次返回到桌面", Toast.LENGTH_SHORT).show();
			isCanBackToDesktop = true;		
			final Timer timer = new Timer(); //如果用户在两秒之内没有按两下返回键，就重置
			timer.schedule(new TimerTask() {						
				@Override
				public void run() {
					isCanBackToDesktop = false;
					timer.cancel();
				}
			}, 3000);
			return true;
		}
		Intent intent = new Intent(Intent.ACTION_MAIN); 
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
        intent.addCategory(Intent.CATEGORY_HOME); 
        startActivity(intent); 
        return true; 
	}
}
