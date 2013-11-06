package com.music.seekbar;

import com.music.constant.MusicContant;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;

/**
 * 更新歌曲播放时间和更新进度条
 * @author Administrator
 */
public class PlayTime {
	
	private Handler handler = new Handler();
	private MediaPlayer player = null;
	private TimerThread timerThread = null;
	
	Context context;	
	
	public PlayTime(Context context, MediaPlayer player) {
		this.player = player;
		this.context = context;
		this.timerThread = new TimerThread();
	}
	
	/**歌曲播放计时的线程,注意这个线程不是一个新的线程，它是UI的主线程*/
	private class TimerThread extends Thread {
		@Override
		public void run() {
			if (player != null) {
				int currentPosition = player.getCurrentPosition();			
				updateSeekBar(currentPosition);
				handler.post(timerThread);
			}		
		}		
	}
	
	private void updateSeekBar(int currentPosition){
		Intent intent = new Intent();
		intent.setAction(MusicContant.UPDATE_UI_ACTION);
		intent.setFlags(0x16);
		intent.putExtra("processRate", (float)currentPosition / player.getDuration());
		intent.putExtra("currentMusicTime", timeConvert(currentPosition / 1000));
		context.sendBroadcast(intent);
	}
	
	/**开始计时*/
	public void beginCountTime() {		
		handler.post(timerThread);
	}
	
	/**暂停计时*/
	public void pauseCountTime() {
		stopCountTime();
	}
	
	/**停止计时*/
	public void stopCountTime() {
		handler.removeCallbacks(timerThread);
	}
		
	/**歌曲时间转换*/
	private String timeConvert(int millTime) {
		int min = millTime / 60; //分钟
		int sec = millTime % 60; //秒钟			
		return getFormatTime(min) + ":" + getFormatTime(sec);
	}
	
	/**@return 返回歌曲的格式化标准时间 ，格式如：00:00*/
	private String getFormatTime(int time) {
		return (time >9 ? time : "0" + time).toString();		
	}
}
