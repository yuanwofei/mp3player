package com.music.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.WindowManager;

/**歌手图片处理类*/
public class BitmapHandle {	
	
	/**颜色矩阵*/
	private ColorMatrix colorMatrix = null;		
	
	/**lightValue的值范围是0-2f，其中1表示正常，如果小于1f，图片将变暗，反之亦然*/	 
	private float lightValue = 0.64f;
	
	/**设备的屏幕宽度*/
	private int deviceWidth; 
	/**设备的屏幕高度*/
	private int deviceHeight;
	/**设备的比例（高/宽）*/
	private float deviceRatio;
	
	private final static int WIDTH = 320;
	private final static int HEIGHT = 480;
		
	public BitmapHandle(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		deviceWidth = manager.getDefaultDisplay().getWidth();
		deviceHeight = manager.getDefaultDisplay().getHeight();
		deviceRatio = (float)deviceHeight / deviceWidth;
	}
	
	/**
	 * 降低图片的亮度
	 * @param sourceBitmap 原图片的bitmap
	 * @return 返回一个经过降低亮度处理的bitmap, sourceBitmap为空时返回null。
	 */
	private Bitmap reducePicLight(Bitmap sourceBitmap) {
		if(sourceBitmap == null) {
			return null;
		}
		Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
		// 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片
		Canvas canvas = new Canvas(newBitmap); // 得到画笔对象
		Paint paint = new Paint(); // 新建paint
		paint.setAntiAlias(true); // 设置抗锯齿,也即是边缘做平滑处理

		colorMatrix = new ColorMatrix(); // 用于颜色变换的矩阵，android位图颜色变化处理主要是靠该对象完成		
		colorMatrix.reset(); // 设为默认值
		colorMatrix.setScale(lightValue, lightValue, lightValue, 1);
		
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));// 设置颜色变换效果
		canvas.drawBitmap(sourceBitmap, 0, 0, paint); // 将颜色变化后的图片输出到新创建的位图区
		return newBitmap;	
	}
	
	/**
	 * 对原图片裁剪出一个小的正方形歌手图片
	 * @param sourceBitmap 原标准歌手图片（屏幕大小一致的Bitmap）
	 * @return 一个小的正方形歌手图片，sourceBitmap为空时返回null
	 */
	public Bitmap clipMiniPic(Bitmap sourceBitmap) {
		if(sourceBitmap == null) {
			return null;
		}
		return Bitmap.createBitmap(sourceBitmap, 0, 0, 
				sourceBitmap.getWidth(), sourceBitmap.getWidth());
	}
	
	/**
	 * 对下载后的图片进行缩小并裁剪使得图片大小和屏幕的大小一致
	 * 返回和屏幕大小一致的Bitmap，sourceBitmap为空时返回null
	 */
	public Bitmap clipBigPic(Bitmap sourceBitmap) {		
		if(sourceBitmap == null) {
			return null;
		}
		Bitmap bitmap = null;
		if(sourceBitmap.getWidth() >= WIDTH) {
			Matrix matrix = new Matrix();    
			float scaleWidth = ((float) WIDTH / sourceBitmap.getWidth());       	     
			matrix.postScale(scaleWidth, scaleWidth);       
			bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
					sourceBitmap.getHeight(), matrix, true);			
		} 	
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
	}
	
	/**裁剪图片使得和屏幕分辨率的比例一致*/
	public Bitmap clipPlayBgPic(Bitmap sourceBitmap) {
		if(sourceBitmap == null) {
			return null;
		}
		float picRatio = (float)sourceBitmap.getHeight() / sourceBitmap.getWidth();
		Bitmap bitmap = null;
		if(deviceRatio < picRatio) {
			bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), (int)(WIDTH * deviceRatio));
		} else if(deviceRatio > picRatio) {
			int width = (int)(sourceBitmap.getHeight()/deviceRatio);
			bitmap = Bitmap.createBitmap(sourceBitmap, (WIDTH - width)/2, 0, width, sourceBitmap.getHeight());
		} else {
			bitmap = sourceBitmap;
		}		
		return reducePicLight(bitmap);
	}
	
	/**返回设备的屏幕宽度*/
	public int getDeviceWidth() {
		return deviceWidth;
	}
	
	/**返回设备的屏幕高度*/
	public int getDeviceHeight() {
		return deviceHeight;
	}	
}
