package yuan.lyric;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class DesktopLyric {

	Context context;
	
	public DesktopLyric(Context context) {
		this.context = context;
	}

	public void showDesktopLRC(){
		Rect rect = new Rect();  
		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  	
		WindowManager windowManager = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;		
		params.width = WindowManager.LayoutParams.FILL_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.alpha = 80;		
		params.gravity=Gravity.LEFT|Gravity.TOP;   
		//以屏幕左上角为原点，设置x、y初始值
		params.x = 0;
		params.y = 0;
	        
		DesktopLyricView lrcView = new DesktopLyricView(context);
		windowManager.addView(lrcView, params);
	}
}