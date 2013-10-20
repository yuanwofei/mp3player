package yuan.mp3player.broadcast;

import yuan.constant.AppConstant;
import yuan.image.LoadImageThread;
import yuan.mp3player.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;

/**加载歌手图片广播监听器*/
public class LoadImageBroadcastReceiver extends BroadcastReceiver {		
	
	private LoadImageThread loadImage = null;		
	
	private Handler loadImageHandler = new Handler() {	
		@Override public void handleMessage(Message msg) {				
			super.handleMessage(msg);								
			showSingerImg();				
		}			
	};	
	
	public LoadImageBroadcastReceiver(LoadImageThread loadImage) {
		this.loadImage = loadImage;		
	}		
	
	/**当loadImage.getOldBitmap()不为空时就显示歌手图片，不存在，不显示*/
	private void showSingerImg() {	
		if(loadImage.getBigBitmap() != null) {
			AppConstant.PlayComponent.miniSingerImage.setImageBitmap(loadImage.
					getMiniBitmap());								
			AppConstant.PlayComponent.playerBg.setBackgroundDrawable(
					new BitmapDrawable(loadImage.getBigBitmap()));
		} else {
			AppConstant.PlayComponent.miniSingerImage.setImageResource(
					R.drawable.mini_default_singerimage);
			AppConstant.PlayComponent.playerBg.setBackgroundResource(R.drawable.root_bg);
		}
		loadImage.freeBitmap();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {			
		loadImageHandler.sendEmptyMessage(0);			
	} 
		
	/**广播过滤器，接收特定的广播*/
	public IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();					
		intentFilter.addAction(AppConstant.LOAD_SINGER_IMAGE_OVER);
		return intentFilter;
	}		
}