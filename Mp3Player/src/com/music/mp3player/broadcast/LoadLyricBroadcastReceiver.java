package com.music.mp3player.broadcast;

import com.music.constant.MusicContant;
import com.music.lyric.LyricLoadThread;
import com.music.mp3player.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

public class LoadLyricBroadcastReceiver extends BroadcastReceiver  {

	private LyricLoadThread lyricLoad = null;
		
	public LoadLyricBroadcastReceiver(LyricLoadThread lyricLoad) {
		this.lyricLoad = lyricLoad;
	}

	private Handler lyricLoadHandler = new Handler() {	
		@Override public void handleMessage(Message msg) {				
			super.handleMessage(msg);								
			showLyric();				
		}			
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		lyricLoadHandler.sendEmptyMessage(0);		
	}
	
	public void showLyric() {		
		MainActivity.getLyricView().setLyricInfo(lyricLoad.getLyricInfos(), 1);	
		MainActivity.getLyricView().beginRefreshLyric();
	}
	
	/**广播过滤器，接收特定的广播*/
	public IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();					
		intentFilter.addAction(MusicContant.LOAD_LYRIC_OVER);
		return intentFilter;
	}	
}
