package yuan.mp3player.service;

import java.io.File;
import java.util.List;

import yuan.constant.AppConstant;
import yuan.constant.MusicPlayer;
import yuan.constant.PlayModeConstant;
import yuan.factory.Mp3InfoSearchFactory;
import yuan.factory.OnlineFactory;
import yuan.image.LoadImageThread;
import yuan.lyric.LyricLoadThread;
import yuan.model.CopyMp3Infos;
import yuan.model.Mp3Info;
import yuan.mp3player.MainActivity;
import yuan.mp3player.R;
import yuan.mp3player.broadcast.LoadImageBroadcastReceiver;
import yuan.mp3player.broadcast.LoadLyricBroadcastReceiver;
import yuan.notification.TrayNotification;
import yuan.seekbar.PlayTime;
import yuan.utils.FileUtils;
import yuan.utils.Network;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

public class PlayerService extends Service{
	
	private boolean isPlaying = false;
	private boolean isPause = false;
	private int position = 0;
	private int msg = 0;
	
	private PlayTime playTime = null;
	private LoadImageThread loadImage = null;	
	private LyricLoadThread lyricLoadThread = null;	
	private Mp3Info mp3Info = null;		
	
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
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getExtra(intent);		
		selectPlayType();		
		return super.onStartCommand(intent, flags, startId);
	}

	/**获取从MainActivity传过来的信息*/
	private void getExtra(Intent intent) {
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		position = intent.getIntExtra("position", 0);//得到所选MP3在当前列表中的位置	
		msg = intent.getIntExtra("MSG", 0);
	}
	
	/**选择播放类型*/
	private void selectPlayType() {
		if(mp3Info != null) {
			if(msg == AppConstant.PlayerMsg.PLAY_MSG) {			
				play(mp3Info);
			}
			else if(msg == AppConstant.PlayerMsg.BEFORE_MSG) {
				before(mp3Info);
			}
			else if(msg == AppConstant.PlayerMsg.AFTER_MSG) {
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
		position = PlayModeConstant.playMode.getAfterPosition(position, mp3Infos);
		mp3Info = mp3Infos.get(position);
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
		AppConstant.PlayComponent.mp3Time.setText(FileUtils.
				IntTimeConvert(MusicPlayer.getPlayer().getDuration()));
		AppConstant.PlayComponent.miniMp3Time.setText(" - " + FileUtils.
				IntTimeConvert(MusicPlayer.getPlayer().getDuration()));
	}
	
	private void setPlayButton() {
		AppConstant.PlayComponent.playButton.setImageDrawable(getResources().
				getDrawable(R.drawable.play));
		AppConstant.PlayComponent.miniPlayBtn.setImageDrawable(getResources().
				getDrawable(R.drawable.play));
	}
	
	private void setPauseButton() {
		AppConstant.PlayComponent.playButton.setImageDrawable(getResources().
				getDrawable(R.drawable.pause));
		AppConstant.PlayComponent.miniPlayBtn.setImageDrawable(getResources().
				getDrawable(R.drawable.pause));
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
	private class ConnectAsyncTask extends AsyncTask<String,String,String>{
		@Override
		protected String doInBackground(String... params) {					
			loadMp3();						
			setMediaPlayer();	
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);							
			loadLyric();
			loadSingerImg();
			updateApplicationTrayTitle();
			showSingerName();		
			showSongName();	
			initTime();
			setPlayState();	//设置播放状态			
		}
										
		/**加载MP3*/
		private void loadMp3() {					
			if(MusicPlayer.getPlayer() != null) {
				setStopState();			
			}
			if(mp3Info.getMp3URL() == null && mp3Info.getMp3IdCode() != null) {
				OnlineFactory factory = new Mp3InfoSearchFactory(mp3Info.getMp3IdCode(), mp3Info);
				factory.execute();//获取MP3地址			
			}
			Uri mp3Uri = Uri.parse(mp3Info.getMp3URL());
			MusicPlayer.setPlayer(MediaPlayer.create(PlayerService.this, mp3Uri));													
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
		
		/**显示歌曲名*/
		private void showSongName() {
			AppConstant.PlayComponent.songName.setText(mp3Info.getMp3SimpleName());
			AppConstant.PlayComponent.miniSongName.setText(mp3Info.getMp3SimpleName());			
		}
		
		/**显示歌手名*/
		private void showSingerName() {
			AppConstant.PlayComponent.singerName.setText(mp3Info.getSingerName());
			AppConstant.PlayComponent.miniSingerName.setText(mp3Info.getSingerName());
		}
			
		/**加载歌词*/
		private void loadLyric() {																			
			MainActivity.getLyricView().setLyricInfo(null, 0);
			MainActivity.getLyricView().setCurrentMp3Info(mp3Info);
			lyricLoadThread = new LyricLoadThread(mp3Info, PlayerService.this);
			lyricLoadThread.start();		
			LoadLyricBroadcastReceiver lyricReceiver = new LoadLyricBroadcastReceiver(lyricLoadThread);
			registerReceiver(lyricReceiver, lyricReceiver.getIntentFilter());
		}
		
		/**加载歌手图片*/
		private void loadSingerImg() {				
			loadImage = new LoadImageThread(mp3Info, PlayerService.this, false);
			loadImage.start();						
			LoadImageBroadcastReceiver imageReceiver = new LoadImageBroadcastReceiver(loadImage);								
			registerReceiver(imageReceiver, imageReceiver.getIntentFilter());																						 
		}
		
		/**显示程序任务栏托盘*/
		private void updateApplicationTrayTitle() {
			StringBuilder title = new StringBuilder().append(mp3Info.getSingerName())
					.append(" - ").append(mp3Info.getMp3SimpleName());
			TrayNotification.addNotification(PlayerService.this, R.drawable.information_icon,
					title.toString());
		}
		
		/**初始化计时线程*/
		private void initTime() {
			if(MusicPlayer.getPlayer() != null)
				playTime = new PlayTime(MusicPlayer.getPlayer());
		}
	}
}
