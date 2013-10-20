package yuan.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class OnlineActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN); 
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// зЂвт  
	        intent.addCategory(Intent.CATEGORY_HOME); 
	        startActivity(intent); 
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}
}
