package com.music.mp3player;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.music.R;
import com.music.constant.Music;
import com.music.factory.PlayModeFactory;
import com.music.factory.model.CopyMp3Infos;
import com.music.factory.model.Mp3Info;
import com.music.factory.model.playmode.AbstractPlayMode;
import com.music.factory.model.playmode.RandomPlayMode;
import com.music.factory.model.playmode.SequencePlayMode;
import com.music.factory.model.playmode.SinglePlayMode;
import com.music.image.LoadImageThread;
import com.music.lyric.LyricTextView;
import com.music.mp3player.service.PlayService;
import com.music.notification.TrayNotification;
import com.music.seekbar.SeekBarListener;
import com.music.utils.FileUtils;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnClickListener{

	private static final String TAG = "MainActivity";
	
	private ArrayList<Mp3Info> mp3Infos = null;
	private Mp3Info mp3Info = null;
	public static int index = 0;
	private int modeValue = Music.PlayMode.SEQUENCE_MODE;

	public ViewFlipper appFlipper = null;
	public static ViewFlipper viewFlipper = null;
	private RelativeLayout miniPlayControlBar = null;
	private String title[] = {
		"本地音乐", 
		"我的收藏", 
		"在线音乐" 
	};
	private View localView, onlineView, searchView;
	private View view[] = {
		localView, 
		onlineView, 
		searchView 
	};
	private Class<?> className[] = {
		LocalHomeActivity.class,
		OnlineActivity.class, 
		SearchActivity.class 
	};

	private TextView singerName;
	private TextView musicName;		
	private TextView countTime;
	private TextView mp3Time;
	
	private TextView miniSingerName;
	private TextView miniSongName;
	private TextView miniCountTime;	
	private TextView miniMp3Time;
				
	private ImageButton playBtn;
	private ImageButton prevBtn;
	private ImageButton nextBtn;
	
	private ImageButton miniPlayBtn;
	private ImageButton miniPrevBtn;
	private ImageButton miniNextBtn;
	
	private ImageButton returnBack;
	private ImageButton playMode;
	private ImageView miniSingerBg;
	private SeekBar timeSeekBar;
	private ImageView singerBg;
	
	private static LyricTextView lyricView = null;
	private boolean isMainUI = true;
	
	private static PlayModeFactory playModeFactory;
	private static boolean isStartedPlayService = false;
	UpdateUiReceiver mReceiver;
	LoadImageThread mLoadImageThread = null;

	class UpdateUiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int flag = intent.getFlags();
			switch(flag) {
			case 0x11:
				 playBtn.setImageResource(R.drawable.play);
				 miniPlayBtn.setImageResource(R.drawable.play);				
				 break;			 
			
			case 0x12:
				 playBtn.setImageResource(R.drawable.pause);
				 miniPlayBtn.setImageResource(R.drawable.pause);
				 break;			 
			
			case 0x13:
				 String musicSimpleName = intent.getStringExtra("musicSimpleName");
				 String singerNames = intent.getStringExtra("singerName");
				 musicName.setText(musicSimpleName);
				 miniSongName.setText(musicSimpleName);
				 singerName.setText(singerNames);
				 miniSingerName.setText(singerNames);
				 singerBg.setImageBitmap(null);
				 miniSingerBg.setImageBitmap(null);
				 
				 mLoadImageThread = new LoadImageThread(mp3Info, MainActivity.this, false);
				 mLoadImageThread.start();
				 break;				 
			
			case 0x14:
				 String duration = intent.getStringExtra("duration");
				 mp3Time.setText(duration);
				 miniMp3Time.setText(" - " + duration);
				 break;
				
			case 0x15:
				 singerBg.setImageBitmap(mLoadImageThread.getBigBitmap());
				 miniSingerBg.setImageBitmap(mLoadImageThread.getMiniBitmap());
				 mLoadImageThread.freeBitmap();
				 break;	
				 
			case 0x16:
				 int processRate = intent.getIntExtra("processRate", 1);
				 String currentMusicTime = intent.getStringExtra("currentMusicTime");
				 timeSeekBar.setProgress(processRate * timeSeekBar.getMax());
				 
				 countTime.setText(currentMusicTime);
				 miniCountTime.setText(currentMusicTime);
				 break;
			case 0x17:
				mLoadImageThread = new LoadImageThread(mp3Info, MainActivity.this, true);
				mLoadImageThread.start();
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		mp3Infos = FileUtils.getMediaStoreMp3Infos(this);
		CopyMp3Infos.setMP3INFOS(mp3Infos);
		mReceiver = new UpdateUiReceiver();
		registerReceiver(mReceiver, new IntentFilter(Music.UPDATE_UI_ACTION));
		
		initComponent();
		registerMonitor();
		TabHost tabHost = getTabHost();
		for (int i = 0; i < title.length; i++) {
			view[i] = (View) LayoutInflater.from(this).inflate(
					R.layout.tab_item, null);
			TextView text = (TextView) view[i].findViewById(R.id.tab_label);
			text.setText(title[i]);
			text.setTextSize(17);
			tabHost.addTab(tabHost.newTabSpec(title[i]).setIndicator(view[i])
					.setContent(new Intent(this, className[i])));
		}
	}

	class MiniPlayControlListener implements OnClickListener {
		public void onClick(View v) {
			isMainUI = false;
			appFlipper.showNext();
		}
	}

	class ReturnBackListener implements OnClickListener {
		public void onClick(View v) {
			appFlipper.showPrevious();
		}
	}

	@Override	
	public void onClick(View v) {
		mp3Infos = CopyMp3Infos.getMP3INFOS();
		if(v.getId() == R.id.play || v.getId() == R.id.mini_play) {
			setIntent(Music.PlayState.PLAY);
		} else if (v.getId() == R.id.prev || v.getId() == R.id.mini_prev) {
			index = getPlayMode().preSongIndex(index, mp3Infos.size());		
			setIntent(Music.PlayState.PRE);
		} else if (v.getId() == R.id.next || v.getId() == R.id.mini_next) {
			index = getPlayMode().nextSongIndex(index, mp3Infos.size());
			setIntent(Music.PlayState.NEXT);
		}
	}
	
	
	/** 设置Intent */
	private void setIntent(int flag) {
		mp3Info = mp3Infos.get(index);
		Intent intent = new Intent();
		intent.putExtra("MSG", flag);
		intent.putExtra("mp3Info", mp3Info);
		intent.putExtra("index", index);
		if (isStartedPlayService) {
			intent.setAction(Music.PLAY_MUSIC_ACTION);
			sendBroadcast(intent);
		} else {
			isStartedPlayService = true;
			intent.setClass(this, PlayService.class);
			startService(intent);
		}		
	}
	
	public static AbstractPlayMode getPlayMode() {
		return playModeFactory.createPlayMode();
	}
	
	public void setPlayModeFactory(PlayModeFactory playModeFactory) {
		MainActivity.playModeFactory = playModeFactory;
	}

	/** 初始化各种组件 */
	private void initComponent() {
		setPlayModeFactory(SequencePlayMode.factory);
		
		appFlipper = (ViewFlipper) findViewById(R.id.app_viewflipper);

		miniPlayControlBar = (RelativeLayout) findViewById(R.id.mini_play_control);
		
		miniPlayBtn = (ImageButton) findViewById(R.id.mini_play);
		miniPrevBtn = (ImageButton) findViewById(R.id.mini_prev);
		miniNextBtn = (ImageButton) findViewById(R.id.mini_next);
		miniSongName = (TextView) findViewById(R.id.mini_song_name);
		miniSingerName = (TextView) findViewById(R.id.mini_singer_name);
		miniCountTime = (TextView) findViewById(R.id.mini_counttime_name);
		miniMp3Time = (TextView) findViewById(R.id.mini_songtime_name);
		miniSingerBg = (ImageView) findViewById(R.id.mini_singer_bg);

		singerName = (TextView) findViewById(R.id.singer_name);
		musicName = (TextView) findViewById(R.id.song_name);
		playBtn = (ImageButton) findViewById(R.id.play);
		prevBtn = (ImageButton) findViewById(R.id.prev);
		nextBtn = (ImageButton) findViewById(R.id.next);

		playMode = (ImageButton) findViewById(R.id.play_mode);
		returnBack = (ImageButton) findViewById(R.id.return_back);
		singerBg = (ImageView) findViewById(R.id.singer_bg);
		lyricView = (LyricTextView) findViewById(R.id.lyric);
		timeSeekBar = (SeekBar) findViewById(R.id.seekbar);
		countTime = (TextView) findViewById(R.id.increase_time);
		mp3Time = (TextView) findViewById(R.id.mp3_time);

	}

	/** 为播放组件注册监听器 */
	private void registerMonitor() {
		miniPlayControlBar.setOnClickListener(new MiniPlayControlListener());
		miniPlayBtn.setOnClickListener(this);
		miniPrevBtn.setOnClickListener(this);
		miniNextBtn.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		prevBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		
		playMode.setOnClickListener(new PlayModeListener());
		returnBack.setOnClickListener(new ReturnBackListener());
		timeSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
	}

	/** 播放模式监听器 */
	class PlayModeListener implements OnClickListener {
		public void onClick(View v) {
			switch (modeValue) {
			case Music.PlayMode.SEQUENCE_MODE:
				playMode.setImageResource(R.drawable.random_mode);
				modeValue = Music.PlayMode.RANDOM_MODE;
				setPlayModeFactory(RandomPlayMode.factory);
				Toast.makeText(MainActivity.this, R.string.random_mode, Toast.LENGTH_SHORT).show();
				break;
			case Music.PlayMode.RANDOM_MODE:
				playMode.setImageResource(R.drawable.single_mode);
				modeValue = Music.PlayMode.SINGLE_MODE;
				setPlayModeFactory(SinglePlayMode.factory);
				Toast.makeText(MainActivity.this, R.string.single_mode, Toast.LENGTH_SHORT).show();
				break;
			case Music.PlayMode.SINGLE_MODE:
				playMode.setImageResource(R.drawable.sequence_mode);
				modeValue = Music.PlayMode.SEQUENCE_MODE;
				setPlayModeFactory(SequencePlayMode.factory);
				Toast.makeText(MainActivity.this, R.string.sequence_mode, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	public static LyricTextView getLyricView() {
		return lyricView;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0: exitApp();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isMainUI) {
			if (viewFlipper.getChildCount() == 1) {
				Intent intent = new Intent(Intent.ACTION_MAIN); 
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
		        intent.addCategory(Intent.CATEGORY_HOME); 
		        startActivity(intent); 
		        return true; 
			} else if (viewFlipper.getChildCount() == 2) {
				viewFlipper.removeView(viewFlipper.getCurrentView());
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK && !isMainUI) {
			appFlipper.showPrevious();
			isMainUI = true;
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	/** 退出程序 */
	private void exitApp() {
		TrayNotification.removeNotification(this);
		
		unregisterReceiver(mReceiver);
		
		Intent intent = new Intent(this, PlayService.class);
		stopService(intent);

		finish();
		System.exit(0);
	}

	public static boolean isStartedPlayService() {
		return isStartedPlayService;
	}

	public static void setStartedPlayService(boolean isStartedPlayService) {
		MainActivity.isStartedPlayService = isStartedPlayService;
	}
}
