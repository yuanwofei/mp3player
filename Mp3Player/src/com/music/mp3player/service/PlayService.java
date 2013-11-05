package com.music.mp3player.service;

import java.io.File;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.music.R;
import com.music.constant.Music;
import com.music.constant.MusicPlayer;
import com.music.factory.HttpApiFactory;
import com.music.factory.model.CopyMp3Infos;
import com.music.factory.model.Mp3Info;
import com.music.factory.model.http.Mp3InfoHttpApi;
import com.music.lyric.LyricLoadThread;
import com.music.mp3player.MainActivity;
import com.music.mp3player.broadcast.LoadLyricBroadcastReceiver;
import com.music.notification.TrayNotification;
import com.music.seekbar.PlayTime;
import com.music.utils.FileUtils;
import com.music.utils.Network;

public class PlayService extends Service{
	
	private static final String TAG = "PlayService";
	
	private boolean isPlaying = false;
	private boolean isPause = false;
	private int index = 0;
	private int msg = 0;
	
	private PlayTime playTime = null;
	private LyricLoadThread lyricLoadThread = null;	
	private Mp3Info mp3Info = null;
	
	PlayServiceReceiver mReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
		
	@Override
	public void onDestroy() {				
		//释放player
		if(MusicPlayer.getPlayer() != null) {
			playTime.stopCountTime();
			MainActivity.getLyricView().stopRefreshLyric();
			MusicPlayer.getPlayer().stop();
			MusicPlayer.getPlayer().release();
		}	
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}
	
	class PlayServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			getExtra(intent);
			selectPlayType();			
		}	
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mReceiver = new PlayServiceReceiver();
		registerReceiver(new PlayServiceReceiver(), new IntentFilter(Music.PLAY_MUSIC_ACTION));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {	
		getExtra(intent);	
		selectPlayType();		
		return super.onStartCommand(intent, flags, startId);
	}

	/**获取从MainActivity传过来的信息*/
	private void getExtra(Intent intent) {
		mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");		
		index = intent.getIntExtra("index", 0);//得到所选MP3在当前列表中的位置	
		msg = intent.getIntExtra("MSG", 0);
	}
	
	/**选择播放类型*/
	private void selectPlayType() {
		if(mp3Info != null) {
			if(msg == Music.PlayState.PLAY) {			
				play(mp3Info);
			}
			else if(msg == Music.PlayState.PRE) {
				before(mp3Info);
			}
			else if(msg == Music.PlayState.NEXT) {
				after(mp3Info);
			}
		}
	}
	
	private void play(Mp3Info mp3Info) {
		if(!isPlaying && !isPause || MusicPlayer.isFirstPlaying) {							
			if(MusicPlayer.getPlayer() != null) {
				setStopState();
			}
			if(mp3Info.getMp3URL() != null && !mp3Info.getMp3URL().contains("http")) {
				File mp3File = new File(mp3Info.getMp3URL());
				if(mp3File.exists() && mp3File.length() > 0) {
					new ConnectAsyncTask().execute("本地mp3文件");
				} else {
					mp3File.delete(); //删掉不合格的MP3
					Toast.makeText(this, mp3Info.getMp3URL() + "不合法", Toast.LENGTH_SHORT).show();
				}
			} else {
				if(Network.isAccessNetwork(this)) {
					new ConnectAsyncTask().execute("在线mp3文件");
				} else {
					Toast.makeText(this, "当前还没联网", Toast.LENGTH_SHORT).show();
				}			
			}					
		} 
		else if(isPlaying) {				
			setPauseState();//设置暂停状态					
		} 
		else if(isPause) {			
			setPlayState();	//设置继续播放状态									
		}
	}
	
	private void nextSong() {
		List<Mp3Info> mp3Infos = CopyMp3Infos.getMP3INFOS();
		index = MainActivity.getPlayMode().nextSongIndex(index, mp3Infos.size());
		mp3Info = mp3Infos.get(index);
		play(mp3Info);
	}
	
	private void before(Mp3Info mp3Info) {		
		if(MusicPlayer.getPlayer() != null)
			setStopState();
		play(mp3Info);		
	}
	
	private void after(Mp3Info mp3Info) {		
		if(MusicPlayer.getPlayer() != null)
			setStopState();
		play(mp3Info);		
	}

	private void setPlayState() {				
		if(MusicPlayer.getPlayer() != null) {			
			MusicPlayer.getPlayer().start();		
			playTime.beginCountTime();
			if(MainActivity.getLyricView().isPauseRefreshLyric) {
				MainActivity.getLyricView().beginRefreshLyric();
			}
			isPlaying = true;
			isPause = false;
			setPauseButton();
			setMp3Time();
		}
	}
	
	private void setPauseState() {
		if(MusicPlayer.getPlayer() != null) {
			playTime.pauseCountTime();
			MainActivity.getLyricView().pauseRefreshLyric();
			MusicPlayer.getPlayer().pause();
			isPlaying = false;
			isPause = true;

			//选择暂停按钮图片
			setPlayButton();
		}		
	}
	
	private void setStopState() {
		if(MusicPlayer.getPlayer() != null) {
			playTime.stopCountTime();
			MainActivity.getLyricView().stopRefreshLyric();
			MusicPlayer.getPlayer().stop();
			MusicPlayer.getPlayer().release();
			MusicPlayer.setPlayer(null);
			isPlaying = false;
			isPause = false;
			setPlayButton();			
		}
	}
	
	/**歌曲的持续时间*/
	private void setMp3Time() {
		Intent intent = new Intent();
		intent.setAction(Music.UPDATE_UI_ACTION);
		intent.setFlags(0x14);
		intent.putExtra("duration", FileUtils.IntTimeConvert(MusicPlayer.getPlayer().getDuration()));
		sendBroadcast(intent);
	}
	
	private void setPlayButton() {
		Intent intent = new Intent();
		intent.setAction(Music.UPDATE_UI_ACTION);
		intent.setFlags(0x11);
		sendBroadcast(intent);	
	}
	
	private void setPauseButton() {
		Intent intent = new Intent();
		intent.setAction(Music.UPDATE_UI_ACTION);
		intent.setFlags(0x12);
		sendBroadcast(intent);		
	}
	
	/**MediaPlayer监听器，用于判断一首歌是否已播放完毕，以便播放下一首歌曲 */
	private class MediaPlayerCompletionListener implements OnCompletionListener {
		public void onCompletion(MediaPlayer arg0) {			
			setStopState();
			nextSong();
		}	
	}
	
	private class MediaPlayerErrorListenner implements OnErrorListener {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			MusicPlayer.getPlayer().reset();
			setStopState();
			nextSong();
			return false;
		}
	}
	
	/***/
	private class ConnectAsyncTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {					
			loadMp3();					
			setMediaPlayer();	
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);							
			updateUI();
			loadLyric();		
			updateApplicationTrayTitle();			
			initTime();
			setPlayState();	//设置播放状态			
		}
										
		/**加载MP3*/
		private void loadMp3() {					
			if(MusicPlayer.getPlayer() != null) {
				setStopState();			
			}
			if(mp3Info.getMp3URL() == null && mp3Info.getMp3IdCode() != null) {
				HttpApiFactory factory = Mp3InfoHttpApi.factory;
				Bundle bundle = new Bundle();
				bundle.putString("mp3Id", mp3Info.getMp3IdCode());
				factory.getHttpApi().execute(bundle, mp3Info);//获取MP3地址
			}
			Uri mp3Uri = Uri.parse(mp3Info.getMp3URL());
			System.out.println(mp3Info.getMp3URL());
			MusicPlayer.setPlayer(MediaPlayer.create(PlayService.this, mp3Uri));													
		}
		
		/**设置MediaPlayer的播放参数*/
		private void setMediaPlayer() {
			try {
				if(MusicPlayer.getPlayer() != null) {
					MusicPlayer.isFirstPlaying = false;
					MusicPlayer.getPlayer().setLooping(false);
					MusicPlayer.getPlayer().setOnCompletionListener(new MediaPlayerCompletionListener());
					MusicPlayer.getPlayer().setOnErrorListener(new MediaPlayerErrorListenner());
				} 				
			} catch(Exception e) {				
				System.err.println("mp3初始化出错！" + e.getMessage());
			}	
		}
		
		private void updateUI() {
			Intent intent = new Intent();
			intent.setAction(Music.UPDATE_UI_ACTION);
			intent.setFlags(0x13);
			intent.putExtra("musicSimpleName", mp3Info.getMp3SimpleName());
			intent.putExtra("singerName", mp3Info.getSingerName());
			intent.putExtra("singerBg", "null");
			sendBroadcast(intent);
		} 
			
		/**加载歌词*/
		private void loadLyric() {																			
			MainActivity.getLyricView().setLyricInfo(null, 0);
			MainActivity.getLyricView().setCurrentMp3Info(mp3Info);
			lyricLoadThread = new LyricLoadThread(mp3Info, PlayService.this);
			lyricLoadThread.start();		
			LoadLyricBroadcastReceiver lyricReceiver = new LoadLyricBroadcastReceiver(lyricLoadThread);
			registerReceiver(lyricReceiver, lyricReceiver.getIntentFilter());
		}
		
		/**显示程序任务栏托盘*/
		private void updateApplicationTrayTitle() {
			StringBuilder title = new StringBuilder().append(mp3Info.getSingerName())
					.append(" - ").append(mp3Info.getMp3SimpleName());
			TrayNotification.addNotification(PlayService.this, R.drawable.information_icon,
					title.toString());
		}
		
		/**初始化计时线程*/
		private void initTime() {
			if(MusicPlayer.getPlayer() != null)
				playTime = new PlayTime(PlayService.this, MusicPlayer.getPlayer());
		}
	}
}
