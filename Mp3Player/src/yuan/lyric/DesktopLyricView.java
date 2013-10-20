package yuan.lyric;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.widget.TextView;

public class DesktopLyricView extends TextView {
	private float float1 = 0.0f;
	private float float2 = 0.01f;	
	private Handler handler;	
	private String text;
	
	public DesktopLyricView(Context context) {
		super(context);
		handler = new Handler();
		handler.post(update);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float1 += 0.005f;
		float2 += 0.005f;	
		
		if(float2 > 1.0){
			float1 = 0.0f;
			float2 = 0.01f;
		}		
		float len = this.getTextSize() * text.length();
		
		Shader shader = new LinearGradient(0, 0, len, 0, 
				new int[]{Color.GREEN, Color.WHITE}, new float[]{float1, float2},
				TileMode.CLAMP);
		
		//设置画笔
		Paint paint = new Paint();
		paint.setTextSize(16);
		paint.setTextAlign(Align.CENTER); 
		paint.setShader(shader);	
		
		//求出text在canvas中居中显示的Y坐标
		FontMetrics fontMetrics = paint.getFontMetrics(); 
		float fontHeight = fontMetrics.bottom - fontMetrics.top; 
		float textBaseY = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom; 
		
		canvas.drawText(text, getWidth()/2, textBaseY, paint);
			
	}
	
	private Runnable update = new Runnable() {
        public void run() {
        	handler.removeCallbacks(update);      	
        	postInvalidate();
        	handler.postDelayed(update, 5);
        }
    };
}
